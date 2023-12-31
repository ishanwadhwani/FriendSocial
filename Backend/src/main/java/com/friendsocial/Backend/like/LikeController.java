package com.friendsocial.Backend.like;

import com.friendsocial.Backend.post.PostRepository;
import com.friendsocial.Backend.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="api/v1/likes")
public class LikeController {
  private final LikeService likeService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  public LikeController(LikeService likeService) {
    this.likeService = likeService;
  }

  // GET ALL LIKES
  // Get mapping because we want to get something out from our server
  @GetMapping
  public List<Like> getLikes() {
    return likeService.getLikes();
  }

  @GetMapping(path = "{postId}")
  public List<Object[]> getLikesByPost(@PathVariable("postId") Long id) {
    List<Object[]> likes = likeService.getLikesOfPostById(id);
    System.out.println("likes//");
    System.out.println(likes.get(0)[0]);
    return likes;
  }

  @GetMapping(path = "isLiked/{postId}/{userId}")
  public Long getLikeOfProfile(@PathVariable("postId") Long postId, @PathVariable("userId") Long userId) {
    return likeService.getLikeOfUserOnPost(postId, userId);
  }

  @PostMapping(path = "{userId}/{postId}")
  public ResponseEntity<Void> createLike(@PathVariable("userId") Long userId, @PathVariable("postId") Long postId, @RequestBody Like likeRequest) {
    likeService.addNewLike(userId, postId, likeRequest);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping(path = "{likeId}")
  public void deleteLike(@PathVariable("likeId") Long likeId) {
    likeService.deleteLike(likeId);
  }

}
