package com.mmsk.book_social_network.history;

import com.mmsk.book_social_network.book.Book;
import com.mmsk.book_social_network.common.BaseEntity;
import com.mmsk.book_social_network.user.User;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BookTransactionHistory extends BaseEntity {
    private User user;
    private Book book;
    private boolean returned;
    private boolean returnedApproved;

}
