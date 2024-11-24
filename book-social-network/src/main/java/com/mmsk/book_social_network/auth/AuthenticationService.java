package com.mmsk.book_social_network.auth;

import com.mmsk.book_social_network.role.RoleRepository;
import com.mmsk.book_social_network.security.JwtService;
import com.mmsk.book_social_network.user.Token;
import com.mmsk.book_social_network.user.TokenRepository;
import com.mmsk.book_social_network.user.User;
import com.mmsk.book_social_network.user.UserRepository;
import com.mmsk.book_social_network.email.EmailService;
import com.mmsk.book_social_network.email.EmailTemplateName;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
private final  RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;
private final UserRepository userRepository;
private final TokenRepository tokenRepository;
private final EmailService emailService;
private final AuthenticationManager authenticationManager;
private final JwtService jwtService;
@Value("${application.mailing.frontend.activationLink}")
private String activationLink;
    /**
 * Registers a new user in the system.
 * 
 * This method creates a new user account based on the provided registration request,
 * assigns the default user role, encodes the password, and saves the user to the database.
 * After successful registration, a validation email is sent to the user.
 *
 * @param request The RegistrationRequest object containing user registration details
 *                such as first name, last name, email, and password.
 * @throws MessagingException If there's an error while sending the validation email.
 * @throws IllegalStateException If the default user role is not found in the system.
 */
public void register(RegistrationRequest request) throws MessagingException {
    var userRole = roleRepository.findByName("USER")
    //TODO: make it more secure
    .orElseThrow(() -> new IllegalStateException("user role not set"));
    var user = User.builder()
    .firstName(request.getFirstName())
    .lastName(request.getLastName())
    .email(request.getEmail())
    .password(passwordEncoder.encode(request.getPassword()))
            .accountLocked(false)
            .enabled(false)
    .roles(List.of(userRole))

    .build();
    userRepository.save(user);
    sendValidationEmail(user);
}

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveValidationToken(user);
        emailService.sendEmail(user.getEmail(),user.fullName(), EmailTemplateName.ACTIVATE_ACCOUNT,activationLink, newToken,"Activate your account");

    }

    private String generateAndSaveValidationToken(User user) {

        // TODO: implement token generation and saving
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(15))
                .user(user)
                .build();

        tokenRepository.save(token);

        return generatedToken ;
    }

    private String generateActivationCode(int length) {
        String character = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        SecureRandom secureRandom =  new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(character.length());
            sb.append(character.charAt(index));
        }
        return sb.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
var auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        )
);
 var claims =  new HashMap<String, Object>();
  var user =  ((User)auth.getPrincipal());
   claims.put("fullName", user.fullName());
 var jwtToken =  jwtService.generateToken(claims,user);
  return AuthenticationResponse.builder().token(jwtToken).build();
    }
//@Transactional
    public void activateAccount(String token) throws MessagingException {
    Token savedToken = tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Could not find token  "));
    if(LocalDateTime.now().isAfter(savedToken.getExpiryDate())){
        sendValidationEmail(savedToken.getUser());
        throw new IllegalStateException("token expired, a new token will be sent to your email");
    }
     var user =userRepository.findById(savedToken.getUser().getId()).orElseThrow(() -> new RuntimeException("Could not find user"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);


    }
}
