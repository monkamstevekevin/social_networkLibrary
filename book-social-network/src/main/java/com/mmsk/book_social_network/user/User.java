package com.mmsk.book_social_network.user;

import com.mmsk.book_social_network.book.Book;
import com.mmsk.book_social_network.history.BookTransactionHistory;
import com.mmsk.book_social_network.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Column( unique = true)
    private String email;
    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    @OneToMany(mappedBy = "owner")
    private List<Book> books;
    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory>histories;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;



    // Example of roles/authorities mapping


    /**
     * Returns the authorities granted to the user.
     * In this case, roles are treated as granted authorities.
     *
     * @return the authorities granted to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles != null ? roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    /**
     * Returns the password of the user.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return true if the user's account is valid (non-expired), false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // You can implement custom logic if needed
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return true if the user is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked; // You can implement custom logic if needed
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return true if the user's credentials are valid (non-expired), false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can implement custom logic if needed
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return true if the user is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Returns the name of this Principal (username).
     *
     * @return the username as the Principal name
     */
    @Override
    public String getName() {
        return this.email;
    }
    public String fullName(){
        return firstName + " " + lastName;
    }

}
