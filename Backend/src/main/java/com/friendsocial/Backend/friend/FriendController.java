package com.friendsocial.Backend.friend;

import com.friendsocial.Backend.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="api/v1/friends")
public class FriendController {
  private final FriendService friendService;
  private final UserService userService;

  public FriendController(FriendService friendService, UserService userService) {
    this.friendService = friendService;
    this.userService = userService;
  }

  @GetMapping
  public List<Friend> getFriends() {
    return friendService.getFriends();
  }

  @GetMapping(path = "{userId}")
  public List<Friend> getFriends(@PathVariable("userId") Long id) {
    System.out.println("GETTING LIST OF FRIENDS");
    return friendService.getFriendsOfUserById(id);
  }

  // POST (ADD) A FRIEND
  @PostMapping(path = "{userId}/{friendId}")
  public void createFriend(@PathVariable("userId") Long userId, @PathVariable("friendId") Long friendId, @RequestBody Friend friendRequest) {
    System.out.println("ADDING FRIEND");
    // Save the post entity
    friendService.addNewFriend(userId, friendId, friendRequest);
  }

  // DELETE A FRIEND
  // pass the friendshipId within the path. Grab the post ID with @PathVariable
  @DeleteMapping(path = "{friendshipId}")
  public void deletePost(@PathVariable("friendshipId") Long id) {
    System.out.println("DELETING FRIEND");
    friendService.deleteFriend(id);
  }
}

