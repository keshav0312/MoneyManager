package com.moneymanger.moneytracker.service;


import com.moneymanger.moneytracker.dto.AuthDTO;
import com.moneymanger.moneytracker.dto.ProfileDTO;
import com.moneymanger.moneytracker.entity.ProfileEntity;
import com.moneymanger.moneytracker.reposistory.ProfileRepository;
import com.moneymanger.moneytracker.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService   {
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private final AuthenticationManager authenticationManager;



//

   @Value("${app.activation.url}")
      private String backend_url;



    private final ModelMapper modelMapper;
    private final SendEmailService sendEmailService;

    private final ProfileRepository profileRepository;

    public ProfileService(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, ModelMapper modelMapper, SendEmailService sendEmailService, ProfileRepository profileRepository,@Value("${app.activation.url}") String backend_url) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.backend_url = backend_url;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.sendEmailService = sendEmailService;
        this.profileRepository = profileRepository;
    }


    public ProfileDTO registerUser(ProfileDTO profileDTO) throws MessagingException, UnsupportedEncodingException {
        Optional<ProfileEntity> profileEntityemail = profileRepository.findByEmail(profileDTO.getEmail());
        if (profileEntityemail.isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        logger.info(backend_url);

        ProfileEntity profileEntity = modelMapper.map(profileDTO, ProfileEntity.class);
        profileEntity.setPassword(passwordEncoder.encode(profileDTO.getPassword()));
        profileEntity.setActivationToken(UUID.randomUUID().toString());
        ProfileEntity profileEntity11 = profileRepository.save(profileEntity);
        ProfileDTO profileDTO1 = modelMapper.map(profileEntity11, ProfileDTO.class);

    try
    {
        String emailActivationLink = backend_url+"/api/v1.0/activate?token=" + profileEntity11.getActivationToken();
          String subject = "Activate your Money Manager profile";
//        String body = "<p>Click the link below to activate your profile:</p>"
//                + "<a href='" + emailActivationLink + "'>Activate Profile</a>";
//         Email HTML body (fancy, professional, attractive)
        String body = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f7; margin: 0; padding: 0; }" +
                ".container { max-width: 600px; margin: 20px auto; background: #fff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }" +
                "h1 { color: #6D28D9; }" +
                "p { color: #333; font-size: 16px; line-height: 1.5; }" +
                ".button { display: inline-block; padding: 12px 25px; margin-top: 20px; background: #6D28D9; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }" +
                ".footer { margin-top: 30px; font-size: 12px; color: #888; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<h1>🎉 Welcome to Money Manager, " + profileEntity11.getFullName()+ "!</h1>" +
                "<p>Thank you for registering. To start managing your finances, please activate your profile by clicking the button below:</p>" +
                "<a href='" + emailActivationLink + "' class='button'>Activate Profile ✅</a>" +
                "<p>After activation, you can track your income, expenses, and stay on top of your budget efficiently!</p>" +
                "<div class='footer'>MoneyManager &copy; 2025 | All rights reserved 💜</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        sendEmailService.sendMail(profileEntity11.getEmail(), subject, body);
        return profileDTO1;

    }catch (Exception e)
    {
        throw new RuntimeException(e.getMessage());
    }
    }

    public boolean activateProfile(String activationToken) {
        Optional<ProfileEntity> profileEntity = profileRepository.findByActivationToken(activationToken);
        if (profileEntity.isPresent()) {
            ProfileEntity profileEntity1 = profileEntity.get();
            profileEntity1.setIsActive(true);
            profileRepository.save(profileEntity1);
            return true;
        }
        return false;
    }

    public boolean isAccountActive(String email) {
        return profileRepository.findByEmail(email).map(ProfileEntity::getIsActive).orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Email not found"));
    }

    public ProfileDTO getPublicProfile(String email) {
        ProfileEntity currentUser = null;
        if (email == null)
            currentUser = getCurrentProfile();
        else {
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not Found with this Email"+email));
        }
        return modelMapper.map(currentUser, ProfileDTO.class);
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDTO authDTO) {
  try {

      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));

      return Map.of(
              "token", jwtUtil.generateToken(authDTO.getEmail()),
              "profile", getPublicProfile(authDTO.getEmail())
      );
  }catch(Exception e) {
      throw new RuntimeException(e.getMessage());
  }
    }

    public boolean doesUserExist(String email) {
        return profileRepository.findByEmail(email).isPresent();
    }

}
