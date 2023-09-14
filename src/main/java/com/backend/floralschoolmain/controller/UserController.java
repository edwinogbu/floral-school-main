package com.backend.floralschoolmain.controller;


import com.backend.floralschoolmain.exception.UserNotFoundException;
import com.backend.floralschoolmain.model.User;
import com.backend.floralschoolmain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@CrossOrigin("http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

//
//    @GetMapping
//    public List<User> getAllUsers(){
//
//        return userService.getAllUsers();
//    }

    @PostMapping("/sign-in")
    public ResponseEntity<String> signIn(@RequestParam("email") String email, @RequestParam("password") String password) {
        User user = userService.signIn(email, password);

        if (user != null && user.isEnabled()) {
            // User successfully signed in, handle as needed
            return ResponseEntity.ok("Successfully logged in"); // You can return a success response
        } else {
            // Sign-in failed, handle accordingly (e.g., show an error message)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed"); // Return an unauthorized status with an error message
        }
    }

//    @PostMapping("/logout")
//    public ResponseEntity<String> logout() {
//        userService.logout(); // Call the logout method from the UserService
//        return ResponseEntity.ok("Successfully logged out");
//    }
//

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            userService.logout();
        }
        return ResponseEntity.ok("Successfully logged out");
    }


    @GetMapping("/viewUserProfile/{userId}")
    public ResponseEntity<Object> viewUserProfile(@PathVariable("userId") Long userId) {
        try {
            Optional<User> userProfile = userService.getUserProfile(userId);
            if (userProfile.isPresent()) {
                return ResponseEntity.ok(userProfile.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User profile not found");
            }
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @PutMapping("/updateUserProfile/{userId}")
    public ResponseEntity<String> updateUserProfile(@PathVariable("userId") Long userId, @RequestBody User updatedUser) {
        try {
            updatedUser.setId(userId); // Set the user's ID based on the path variable
            userService.updateUser(updatedUser);
            return ResponseEntity.ok("User profile updated successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
