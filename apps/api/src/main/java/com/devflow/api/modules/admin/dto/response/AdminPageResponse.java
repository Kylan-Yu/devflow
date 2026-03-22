package com.devflow.api.modules.admin.dto.response;

import java.util.List;

/**
 * Admin page response wrapper
 */
public class AdminPageResponse<T> {
    private List<T> items;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;
    private boolean hasPrevious;

    public AdminPageResponse() {}

    public AdminPageResponse(List<T> items, int currentPage, int pageSize, long totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        this.hasNext = currentPage < totalPages;
        this.hasPrevious = currentPage > 1;
    }

    public static <T> AdminPageResponse<T> of(List<T> items, int page, int size, long totalItems) {
        return new AdminPageResponse<>(items, page, size, totalItems);
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
