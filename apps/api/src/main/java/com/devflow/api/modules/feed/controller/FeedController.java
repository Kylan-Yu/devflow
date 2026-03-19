package com.devflow.api.modules.feed.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.feed.service.FeedService;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/latest")
    public ApiResponse<CursorPageResponse<PostSummaryResponse>> latestFeed(@RequestParam(required = false) String cursor,
                                                                           @RequestParam(defaultValue = "10") Integer size,
                                                                           @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(feedService.latestFeed(cursor, size == null ? 10 : size, categoryId));
    }

    @GetMapping("/hot")
    public ApiResponse<CursorPageResponse<PostSummaryResponse>> hotFeed(@RequestParam(required = false) String cursor,
                                                                        @RequestParam(defaultValue = "10") Integer size,
                                                                        @RequestParam(required = false) Long categoryId) {
        return ApiResponse.success(feedService.hotFeed(cursor, size == null ? 10 : size, categoryId));
    }
}
