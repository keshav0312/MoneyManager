package com.moneymanger.moneytracker.controller;




import com.moneymanger.moneytracker.dto.AuthDTO;
import com.moneymanger.moneytracker.dto.CategoryDTO;
import com.moneymanger.moneytracker.dto.ProfileDTO;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.service.CategoryService;
import com.moneymanger.moneytracker.service.ProfileService;
import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.servlet.function.ServerRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class ProfileController {

    @Value("${money.manager.frontend.url}")
   private String frontendUrl;

    private final CategoryService categoryService;
    private ProfileService profileService;
    public ProfileController(ProfileService profileService, CategoryService categoryService) {
        this.profileService = profileService;
        this.categoryService = categoryService;
    }



    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerUser(@RequestBody ProfileDTO profileDTO) throws MessagingException, UnsupportedEncodingException {
        ProfileDTO profileDTO1=profileService.registerUser(profileDTO);
        return new  ResponseEntity<>(profileDTO1, HttpStatus.OK);
    }

//    @GetMapping("/activate")
//    public ResponseEntity<String> activateProfile(@RequestParam String token) {
//        boolean bollean = profileService.activateProfile(token);
//        if (bollean == true) {
//            return new ResponseEntity<>("Activated Successfully", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Activation Failed", HttpStatus.OK);
//        }
//    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activateProfile(@RequestParam String token) {
        boolean activated = profileService.activateProfile(token);

        String redirectUrl;
        if (activated) {
            redirectUrl = frontendUrl+ "/activation_success";
        } else {
            redirectUrl = frontendUrl+"/activation_fail";
        }
      HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        return new ResponseEntity<>(headers, HttpStatus.FOUND); // 302 redirect
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO authDTO) {
        log.info("{}", authDTO);
        try {
            if (!profileService.doesUserExist(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "User not found. Please register first."));
            }

            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Account not activated. Please check your email."));
            }

            Map<String, Object> map = profileService.authenticateAndGenerateToken(authDTO);
            return ResponseEntity.ok(map);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password."));
        }
    }

    @GetMapping("/currentprofile")
    public  ProfileEntity  test()
    {
       ProfileEntity profileEntity= profileService.getCurrentProfile();
       return  profileEntity;
    }
    @GetMapping("/profile")
    public  ResponseEntity<ProfileDTO> getPublicProfile()
    {
       ProfileDTO  profileDTO =   profileService.getPublicProfile(null);
       return new  ResponseEntity<>(profileDTO, HttpStatus.OK);
    }


}
