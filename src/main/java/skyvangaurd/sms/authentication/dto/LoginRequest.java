package skyvangaurd.sms.authentication.dto;

import java.util.List;

import skyvangaurd.sms.authentication.model.Role;

public record LoginRequest(
    String email,
    String password,
    List<Role> roles) {
}
