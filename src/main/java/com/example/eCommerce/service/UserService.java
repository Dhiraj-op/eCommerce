package com.example.eCommerce.service;

import com.example.eCommerce.dto.ChangePasswordRequest;
import com.example.eCommerce.exception.ResourceNotFoundException;
import com.example.eCommerce.model.User;
import com.example.eCommerce.repoitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public User registerUser(User user){
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ADMIN);
        user.setConformationCode(generateConfirmationCode());
        user.setEmailConfirmation(false);
        emailService.sendConfirmationCode(user);
//        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

//          user.setRole(user.getRole());
//        user.setRole(User.Role.ADMIN);

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

    public void changePassword(String email, ChangePasswordRequest request){
        User user = getUserByEmail(email);
        String password = user.getPassword();
        if(!passwordEncoder.matches(request.getCurrentPassword(),user.getPassword())){
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }

    public void confirmEmail(String email , String confirmationCode){
        User user = getUserByEmail(email);
        if(user.getConformationCode().equals(confirmationCode)){
            user.setEmailConfirmation(true);
            user.setConformationCode(null);
            userRepository.save(user);
        }else {
            throw new BadCredentialsException("Invalid confirmation code");
        }
    }

    private String generateConfirmationCode(){
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }





}
