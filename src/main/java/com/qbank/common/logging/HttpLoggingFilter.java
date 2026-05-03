package com.qbank.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_CHARS = 2000;
    private static final String DEFAULT_ENCODING = "UTF-8";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        if (isSkippable(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        MDC.put("traceId", traceId);
        response.setHeader("X-Trace-Id", traceId);

        ContentCachingRequestWrapper req = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper res = new ContentCachingResponseWrapper(response);

        long start = System.currentTimeMillis();
        Throwable thrown = null;

        try {
            filterChain.doFilter(req, res);
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            writeLog(req, res, duration, thrown);
            res.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void writeLog(ContentCachingRequestWrapper req, ContentCachingResponseWrapper res,
                           long duration, Throwable error) {
        int status = res.getStatus();
        String method = req.getMethod();
        String url = buildUrl(req);
        String userId = valueOrDefault(req.getHeader("X-User-Id"), "anonymous");
        String clientIp = extractClientIp(req);
        String userAgent = valueOrDefault(req.getHeader("User-Agent"), "-");
        String contentType = valueOrDefault(req.getContentType(), "-");
        String reqBody = readBody(req.getContentAsByteArray(), req.getCharacterEncoding(), req.getContentType());
        String resBody = readBody(res.getContentAsByteArray(), DEFAULT_ENCODING, res.getContentType());

        String message = String.format(
                "%s %s | user=%s | ip=%s | agent=%s | content-type=%s → %d (%dms)%n" +
                "  REQ : %s%n" +
                "  RES : %s",
                method, url, userId, clientIp, userAgent, contentType,
                status, duration,
                format(reqBody),
                format(resBody)
        );

        if (error != null || status >= 500) {
            log.error(message);
        } else if (status >= 400) {
            log.warn(message);
        } else {
            log.info(message);
        }
    }

    private String buildUrl(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        return StringUtils.hasText(query) ? uri + "?" + query : uri;
    }

    // X-Forwarded-For 헤더가 있으면 프록시 앞단 원본 IP를 추출
    private String extractClientIp(HttpServletRequest req) {
        String forwarded = req.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = req.getHeader("X-Real-IP");
        if (StringUtils.hasText(realIp)) {
            return realIp;
        }
        return req.getRemoteAddr();
    }

    private String readBody(byte[] content, String encoding, String contentType) {
        if (content == null || content.length == 0) return "";
        if (isBinaryContent(contentType)) return "[binary]";
        try {
            Charset charset = StringUtils.hasText(encoding)
                    ? Charset.forName(encoding)
                    : StandardCharsets.UTF_8;
            return new String(content, charset);
        } catch (Exception e) {
            return "[unreadable]";
        }
    }

    private boolean isBinaryContent(String contentType) {
        if (!StringUtils.hasText(contentType)) return false;
        return contentType.startsWith("image/")
                || contentType.startsWith("video/")
                || contentType.startsWith("audio/")
                || contentType.startsWith("multipart/")
                || contentType.contains("octet-stream");
    }

    private String format(String body) {
        if (!StringUtils.hasText(body)) return "(empty)";
        String normalized = body.replaceAll("\\s+", " ").trim();
        return normalized.length() > MAX_BODY_CHARS
                ? normalized.substring(0, MAX_BODY_CHARS) + " ... [truncated]"
                : normalized;
    }

    private boolean isSkippable(HttpServletRequest req) {
        String uri = req.getRequestURI();
        return uri.equals("/favicon.ico") || uri.equals("/api/notifications/stream");
    }

    private String valueOrDefault(String value, String defaultValue) {
        return StringUtils.hasText(value) ? value : defaultValue;
    }
}
