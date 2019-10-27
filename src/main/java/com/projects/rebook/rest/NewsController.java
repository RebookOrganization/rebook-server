package com.projects.rebook.rest;

import com.projects.rebook.bean.Request.CommentRequest;
import com.projects.rebook.bean.Request.LikeRequest;
import com.projects.rebook.bean.Request.PostNewsRequest;
import com.projects.rebook.bean.Request.ShareRequest;
import com.projects.rebook.bean.Response.CommonResponse;
import com.projects.rebook.model.ContactOwner;
import com.projects.rebook.repository.ContactOwnerRepository;
import com.projects.rebook.service.NewsItemService;
import com.projects.rebook.service.UserService;
import com.projects.rebook.service.impl.NewsItemServiceImpl;
import com.projects.rebook.service.impl.UserServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/news")
public class NewsController {

    @Autowired
    private NewsItemService newsItemService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/all-news")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommonResponse getAllNewsItem() throws IOException {
        return newsItemService.getAllNewsItem();
    }

    @GetMapping(value = "/search-by-address")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommonResponse searchNewsByAddress(String address) throws Exception {
        return userService.searchNewsByAddress(address);
    }

    @PostMapping(value = "/search-by-user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommonResponse searchNewsByUser(String username) throws Exception {
        return userService.searchNewsByUser(username);
    }

    @PostMapping(value = "/create-post")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createNewsPost(@RequestBody PostNewsRequest request) throws Exception {
        return new ResponseEntity<>(userService.createNewsPost(request), HttpStatus.OK);
    }

    @PostMapping(value = "/like")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommonResponse likeNewsFeed(@RequestBody LikeRequest like) throws Exception {
        return userService.likeNewsFeed(like);
    }

    @PostMapping(value = "/comment")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommonResponse commentNewsFeed(@RequestBody CommentRequest comment) throws Exception {
        return userService.commentNewsFeed(comment);
    }

    @PostMapping(value = "/share")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public CommonResponse shareNewsFeed(@RequestBody ShareRequest share) throws Exception {
        return userService.shareNewsFeed(share);
    }

    @GetMapping(value = "/user-news")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getAllNewsByUser(@RequestParam Long userID) throws IOException {
        return new ResponseEntity<>(newsItemService.getAllNewsByUser(userID), HttpStatus.OK);
    }

}
