package com.backend.floralschoolmain.controller;


import com.backend.floralschoolmain.event.RegistrationCompleteEvent;
import com.backend.floralschoolmain.event.listener.VerificationToken;
import com.backend.floralschoolmain.exception.UserNotFoundException;
import com.backend.floralschoolmain.model.User;
import com.backend.floralschoolmain.repository.VerificationTokenRepository;
import com.backend.floralschoolmain.request.RegistrationRequest;
import com.backend.floralschoolmain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
@CrossOrigin("http://localhost:3000")

public class RegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private ApplicationEventPublisher publisher;



    @GetMapping("/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
//
//    @GetMapping("/getUserById/{userId}")
//    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
//        try {
//            Optional<User> user = userService.getUserById(userId);
//            if (user.isPresent()) {
//                return ResponseEntity.ok(user.get());
//            } else {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            }
//        } catch (UserNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }



//    @GetMapping("/getUserById/{userId}")
//    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
//        try {
//            Optional<User> user = userService.getUserById(userId);
//            return user.map(ResponseEntity::ok)
//                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"));
//        } catch (UserNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
//    }


    @PostMapping("/save")
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest request){
        User user = userService.registerUsers(registrationRequest);
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUr(request)));
        return  "Success please check yor email to complete registration ";
    }

    @GetMapping("/verifyEmail")
    public  String verifyEmail(@RequestParam("token") String token){
        VerificationToken theToken = verificationTokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "This account has already been verified, please login.";
        }
        String verificationResult = userService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfully. You can login to account Now ";
        }
        return "Invalid verification token";
    }

    private String applicationUr(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
    }

//    @PostMapping("/login")
//    public String signIn(@RequestParam("email") String email, @RequestParam("password") String password) {
//        User user = userService.signIn(email, password);
//
//
//        if (user != null && user.isEnabled()) {
//
//            // User successfully signed in, handle as needed
//            return "redirect:/dashboard"; // Redirect to the dashboard or home page
//        } else {
//            // Sign-in failed, handle accordingly (e.g., show an error message)
//            return "redirect:/login?error"; // Redirect back to the login page with an error parameter
//        }
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        userService.logout(); // Call the logout method from the UserService
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
