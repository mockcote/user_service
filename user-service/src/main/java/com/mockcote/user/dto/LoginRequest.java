package com.mockcote.user.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;
}
