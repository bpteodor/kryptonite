package tech.bran.idp.service.repo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserData {

    String username;
    String password;
    boolean accountLocked;

}
