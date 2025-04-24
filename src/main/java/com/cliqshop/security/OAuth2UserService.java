package com.cliqshop.security;

import com.cliqshop.entity.User;
import com.cliqshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        
        User user = userService.findByEmail(email);
        
        if (user == null) {
            // Create a new user if not exists
            user = new User();
            user.setEmail(email);
            user.setName(name);
            user.setUsername(email); // Use email as username for OAuth users
            user.setPhoneNumber(""); // Set empty phone number for OAuth users
            
            // Generate a random password for OAuth users
            String randomPassword = UUID.randomUUID().toString();
            user.setPassword(randomPassword);
            
            userService.registerUser(user);
        }
        
        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
            oAuth2User.getAttributes(),
            "email"
        );
    }
}
