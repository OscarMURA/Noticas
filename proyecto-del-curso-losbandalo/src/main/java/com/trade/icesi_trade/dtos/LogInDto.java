package com.trade.icesi_trade.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogInDto {
    String email;
    String password;
}
