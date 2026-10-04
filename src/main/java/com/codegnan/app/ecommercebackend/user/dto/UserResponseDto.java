package com.codegnan.app.ecommercebackend.user.dto;

import java.io.Serializable;

public record UserResponseDto(Long id, String firstName, String lastName, String email) implements Serializable {
}
