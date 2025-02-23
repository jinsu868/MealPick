package com.all_i.allibe.admin.domain;

import com.all_i.allibe.common.util.Validator;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "UNIQUE_USERNAME",
            columnNames = {"username"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Admin {

    private static final int MIN_PASSWORD_LENGTH = 4;
    private static final int MAX_PASSWORD_LENGTH = 255;
    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MAX_USERNAME_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String password;

    public Admin(String username, String password) {
        this(null, username, password);
    }

    public Admin(Long id, String username, String password) {
        validate(username, password);
        this.id = id;
        this.username = username;
        this.password = password;
    }

    private void validate(String username, String password) {
        validateUsername(username);
        validatePassword(password);
    }

    private void validateUsername(String password) {
        String fieldName = "password";
        Validator.notBlank(password, fieldName);
        Validator.minLength(password, MIN_USERNAME_LENGTH, fieldName);
        Validator.maxLength(password, MAX_USERNAME_LENGTH, fieldName);
    }

    private void validatePassword(String password) {
        String fieldName = "password";
        Validator.notBlank(password, fieldName);
        Validator.minLength(password, MIN_PASSWORD_LENGTH, fieldName);
        Validator.maxLength(password, MAX_PASSWORD_LENGTH, fieldName);
    }

}
