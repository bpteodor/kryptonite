package tech.bran.idp.service.repo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
public class UserData {

    @NotBlank
    String username;

    @NotBlank
    String password;

    String name;

    boolean accountLocked;

}
