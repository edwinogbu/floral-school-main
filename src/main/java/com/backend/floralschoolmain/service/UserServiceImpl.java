package com.backend.floralschoolmain.service;


import com.backend.floralschoolmain.event.listener.VerificationToken;
import com.backend.floralschoolmain.exception.UserAreadyExistException;
import com.backend.floralschoolmain.exception.UserNotFoundException;
import com.backend.floralschoolmain.model.User;
import com.backend.floralschoolmain.repository.UserRepository;
import com.backend.floralschoolmain.repository.VerificationTokenRepository;
import com.backend.floralschoolmain.request.RegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;



    @Override
    public User registerUsers(RegistrationRequest request) {
        Optional<User> existingUser = findByEmail(request.email());
        if (existingUser.isPresent()) {
            throw new UserAreadyExistException("User with email " + request.email() + " already exists");
        }

        User newUser = new User();
        newUser.setName(request.name());
        newUser.setEmail(request.email());
        newUser.setPhone(request.phone());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
    }


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = verificationTokenRepository.findByToken(theToken);
        if (token == null) {
            return "Invalid verification token";
        }

        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if (token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(token);
            return "Token has expired";
        }

        user.setEnabled(true);
        userRepository.save(user);
        return "Token is valid";
    }

    @Override
    public User signIn(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user; // User successfully signed in
            }
        }

        return null; // Sign-in failed
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext(); // Clear the authentication context
    }


    @Override
    public void updateUser(User user) {
        Optional<User> existingUser = userRepository.findById(user.getId());
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            // Update user fields here based on your requirements
            updatedUser.setName(user.getName());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPhone(user.getPhone());
            updatedUser.setRole(user.getRole());
            // Save the updated user
            userRepository.save(updatedUser);
        } else {
            throw new UserNotFoundException("User with ID " + user.getId() + " not found");
        }
    }

    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        Optional<User> existingUser = userRepository.findById(userId);
        if (existingUser.isPresent()) {
            userRepository.delete(existingUser.get());
        } else {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
    }

//    @Override
//    public void deleteUser(Long userId) throws UserNotFoundException {
//        Optional<User> userOptional = userRepository.findById(userId);
//        if (userOptional.isPresent()) {
//            userRepository.deleteById(userId);
//        } else {
//            throw new UserNotFoundException("User with ID " + userId + " not found");
//        }
//    }


    @Override
    public Optional<User> getUserProfile(Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional;
        } else {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }


    }




//    @Override
//    public User updateUserProfile(User user) {
//        // Retrieve the user from the database using their ID
//        Optional<User> existingUser = userRepository.findById(user.getId());
//
//        if (existingUser.isPresent()) {
//            User updatedUser = existingUser.get();
//
//            // Update the user's profile information with the new data
//            updatedUser.setName(user.getName());
//            updatedUser.setPhone(user.getPhone());
//
//            // Save the updated user profile in the database
//            return userRepository.save(updatedUser);
//        } else {
//            // Handle the case where the user with the given ID does not exist
//            throw new UserNotFoundException(user.getId());
//        }
//    }
//
//    @Override
//    public void deleteUser(Long userId) {
//        // Check if the user exists
//        if (userRepository.existsById(userId)) {
//            // Delete the user from the database
//            userRepository.deleteById(userId);
//        } else {
//            // Handle the case where the user with the given ID does not exist
//            throw new UserNotFoundException("User with ID " + userId + " not found");
//        }
//    }
//
//    @Override
//    public Optional<User> viewUserProfile(Long userId) {
//        // Retrieve the user profile from the database by ID
//        return userRepository.findById(userId);
//    }


}

//@Service
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService{
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//    @Autowired
//    private VerificationTokenRepository verificationTokenRepository;
//
//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User registerUsers(RegistrationRequest request) {
//        Optional<User> user = this.findByEmail(request.email());
//        if (user.isPresent()) {
//            throw new UserAreadyExistException("User with email " +request.email() + "already exist");
//        }
//        var newUser = new  User();
//        newUser.setName(request.name());
//        newUser.setEmail(request.email());
//        newUser.setPhone(request.phone());
//        newUser.setPassword(passwordEncoder.encode(request.password()));
//        newUser.setRole(request.role());
//        return userRepository.save(newUser);
//    }
//    @Override
//    public Optional<User> findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    @Override
//    public void saveUserVerificationToken(User theUser, String token) {
//        var verificationToken = new VerificationToken(token, theUser);
//        verificationTokenRepository.save(verificationToken);
//    }
//
//    @Override
//    public String validateToken(String theToken) {
//        VerificationToken token = verificationTokenRepository.findByToken(theToken);
//        if (token == null){
//            return "Invalid verification token";
//        }
//
//        User user = token.getUser();
//        Calendar calendar = Calendar.getInstance();
//        if(token.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0){
//            verificationTokenRepository.delete(token);
//            return "Token already exist";
//        }
//        user.setEnabled(true);
//        userRepository.save(user);
//        return "valid";
//    }
//
//
//    @Override
//    public User signIn(String email, String password) {
//        Optional<User> userOptional = userRepository.findByEmail(email);
//
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (passwordEncoder.matches(password, user.getPassword())) {
//                return user; // User successfully signed in
//            }
//        }
//
//        return null; // Sign-in failed
//    }
//}
