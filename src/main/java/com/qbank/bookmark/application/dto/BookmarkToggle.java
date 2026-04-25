package com.qbank.bookmark.application.dto;

public class BookmarkToggle {

    public record Response(boolean isBookmarked, long bookmarkCount) {}
}
