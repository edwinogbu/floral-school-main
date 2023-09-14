package com.backend.floralschoolmain.event.listener;



import com.backend.floralschoolmain.event.RegistrationCompleteEvent;
import com.backend.floralschoolmain.model.User;
import com.backend.floralschoolmain.repository.VerificationTokenRepository;
import com.backend.floralschoolmain.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
        private JavaMailSender mailSender;
    private User theUser;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
         theUser = event.getUser();
//        User theUser = event.getUser();
        // get the newly registered user

        String verificationToken = UUID.randomUUID().toString();
        // create a verification token for the user
        // save the verification token for the user
        userService.saveUserVerificationToken(theUser,verificationToken);
        // build the verification URL to be sent to the user
        String url = event.getApplicationUrl() + "/register/verifyEmail?token=" + verificationToken;

            sendVerificationEmail(url, theUser);


        // Log the info
        logger.info("RegistrationCompleteEvent received for user: {}, Verification URL: {}", theUser.getName(), url);

        // send the email to the user
    }


    public void sendVerificationEmail(String url, User theUser) {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent =
                "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; background-color: #f4f4f4; }" +
                        ".container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #ffffff; }" +
                        ".header { background-color: #007BFF; color: #fff; text-align: center; padding: 10px; }" +
                        ".content { padding: 20px; }" +
                        "a { color: #007BFF; text-decoration: none; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class='container'>" +
                        "<div class='header'>" +
                        "<h2>Email Verification</h2>" +
                        "</div>" +
                        "<div class='content'>" +
                        "<p>Hi, " + theUser.getName() + ",</p>" +
                        "<p>Thank you for registering with User Registration Portal Service. To complete your registration, please click the following link:</p>" +
                        "<p><a href='" + url + "'>" + url + "</a></p>" +
                        "<p>This link will expire in 24 hours for security reasons.</p>" +
                        "<p>If you did not request this registration, you can safely ignore this email.</p>" +
                        "<p>Best regards,</p>" +
                        "<p>" + senderName + "</p>" +
                        "</div>" +
                        "</div>" +
                        "</body>" +
                        "</html>";

        // Create a MimeMessage
        MimeMessage message = mailSender.createMimeMessage();

        try {
            // Create a MimeMessageHelper to easily build the email
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(subject);
            helper.setFrom("anointedboy4real80@gmail.com", senderName);
            helper.setTo(theUser.getEmail()); // Set the recipient's email address
            helper.setText(mailContent, true); // Set the email content as HTML

            // You can also attach files or add inline images if needed
            // helper.addAttachment("attachment.txt", new ClassPathResource("attachment.txt"));

            // Send the email
            mailSender.send(message);

            // Optionally, you can log a message or perform other actions here upon successful email sending
            // logger.info("Verification email sent to: " + theUser.getEmail());

        } catch (Exception e) {
            // Handle exceptions, e.g., email sending failure
            // logger.error("Error sending verification email", e);
        }
    }

}
