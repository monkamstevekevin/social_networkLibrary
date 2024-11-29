package com.mmsk.book_social_network.book;

import com.mmsk.book_social_network.common.BaseEntity;
import com.mmsk.book_social_network.feedback.Feedback;
import com.mmsk.book_social_network.history.BookTransactionHistory;
import com.mmsk.book_social_network.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.beans.Transient;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory>histories;
    @Transient
    public double getRate(){
        if (feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate = feedbacks.stream().mapToDouble(Feedback::getNote).average().orElse(0);
        return Math.round(rate * 10) / 10.0;
    };


}
