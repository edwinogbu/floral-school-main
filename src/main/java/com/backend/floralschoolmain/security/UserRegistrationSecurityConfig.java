package com.backend.floralschoolmain.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class UserRegistrationSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/register/**").permitAll()
                                .requestMatchers("/users/**")
                                .hasAnyAuthority("USER", "ADMIN")
                                .anyRequest()
                                .authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .logout(logout ->
                        logout
                                .logoutUrl("/register/logout") // Customize the logout URL as needed
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                );


        return http.build();
    }

}



//package com.backend.floralschooldashboard.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class UserRegistrationSecurityConfig {
// @Bean
//    public PasswordEncoder passwordEncoder(){
//     return  new BCryptPasswordEncoder();
// }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http.cors()
//                .and().csrf().disable()
//                .authorizeHttpRequests()
//                .requestMatchers("/register/**")
//                .permitAll()
//                .and()
//                .authorizeHttpRequests()
//                .requestMatchers("/users/**")
//                .hasAnyAuthority("USER", "ADMIN")
//                .and()
//                .formLogin()
//                .and()
//                .build();
//    }
//}
