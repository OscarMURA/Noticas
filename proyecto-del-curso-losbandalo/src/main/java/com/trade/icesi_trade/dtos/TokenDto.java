/**
 * The TokenDto class represents a data transfer object containing information about a user's token,
 * including name, email, roles, token value, creation date, and expiration date.
 */
package com.trade.icesi_trade.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {
    private String name;
    private String email;
    private List<String> roles;
    private String token;
    private long creationDate;
    private long expirationDate;
}