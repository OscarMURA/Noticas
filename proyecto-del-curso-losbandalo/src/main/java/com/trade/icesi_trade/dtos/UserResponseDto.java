/**
 * This class represents a public user response DTO with fields for user ID, email, hashed password,
 * full name, and phone number.
 */
package com.trade.icesi_trade.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Public representation of a user")
public class UserResponseDto {

    @Schema(description = "User ID", example = "1")
    private Long id;

    @Schema(description = "User email", example = "user@example.com")
    private String email;

    @Schema(description = "User password (hashed)", example = "$2a$10$...")
    private String password;

    @Schema(description = "User full name", example = "John Doe")
    private String name;

    @Schema(description = "User phone number", example = "3123456789")
    private String phone;
}
