package com.backend.floralschoolmain.service;



import com.backend.floralschoolmain.exception.UserNotFoundException;
import com.backend.floralschoolmain.model.User;
import com.backend.floralschoolmain.request.RegistrationRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    User registerUsers(RegistrationRequest request);

    // New method for getting a user by ID
    Optional<User> getUserById(Long userId);

    Optional<User> findByEmail(String email);

    void saveUserVerificationToken(User theUser, String verificationToken);

    String validateToken(String theToken);

    // New method for user sign-in
    User signIn(String email, String password);

    // New method for user logout
    void logout();


//    // New method for updating user profile
//    User updateUserProfile(User user);
//
//    // New method for deleting a user
//    void deleteUser(Long userId);
//
//    // New method for viewing user profile by ID
//    Optional<User> viewUserProfile(Long userId);

    void updateUser(User user);

    void deleteUser(Long userId) throws UserNotFoundException;

    Optional<User>  getUserProfile(Long userId) throws UserNotFoundException;

}
