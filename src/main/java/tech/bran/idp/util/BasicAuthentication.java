package tech.bran.idp.util;

import lombok.Data;

@Data
public class BasicAuthentication {
    private final String username;
    private final String password;
}
