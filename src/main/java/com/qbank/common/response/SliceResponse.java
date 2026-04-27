package com.qbank.common.response;

import java.util.List;
import java.util.function.Function;

public record SliceResponse<T>(
        List<T> content,
        boolean hasNext,
        int number
) {
    public <R> SliceResponse<R> map(Function<T, R> mapper) {
        return new SliceResponse<>(
                content.stream().map(mapper).toList(),
                hasNext,
                number
        );
    }

    public static <T> SliceResponse<T> empty(int pageNumber) {
        return new SliceResponse<>(List.of(), false, pageNumber);
    }
}
