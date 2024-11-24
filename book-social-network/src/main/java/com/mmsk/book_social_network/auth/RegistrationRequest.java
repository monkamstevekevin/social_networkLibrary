package com.mmsk.book_social_network.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class RegistrationRequest {

    @NotEmpty(message = "firstname cannot be empty")
    @NotBlank(message = "firstname cannot be blank")
        private String firstName;

        @NotEmpty(message = "lastname cannot be empty")
        @NotBlank(message = "lastname cannot be blank")
        private String lastName;

        @NotEmpty(message = "email cannot be empty")
        @NotBlank(message = "email cannot be blank")
        @Email(message = "email is not formatted correctly")
        private String email;
        @Size(message = "password must be at least 8 characters long", min = 8)

        private String password;


}
