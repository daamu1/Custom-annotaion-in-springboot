package com.saurabh.model;

import com.saurabh.annoation.TrackTableChanges;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "Student")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@TrackTableChanges
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    Long userId;

    @Column(name = "password", nullable = true)
    String password;

    @Column(name = "old_password", nullable = true)
    String oldPassword;

    @Column(name = "name")
    String name;

    @Column(name = "email")
    String email;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
