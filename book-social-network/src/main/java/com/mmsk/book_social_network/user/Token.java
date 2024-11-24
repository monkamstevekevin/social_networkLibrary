package com.mmsk.book_social_network.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;
    private LocalDateTime validatedAt;
    @ManyToOne()
    @JoinColumn(name = "user_id",nullable = false)

    private User user;


}
