package com.backend.floralschoolmain.repository;


import com.backend.floralschoolmain.event.listener.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
}