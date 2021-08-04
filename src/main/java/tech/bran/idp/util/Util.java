package tech.bran.idp.util;

import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import tech.bran.idp.api.model.AuthzRequest;
import tech.bran.idp.service.validation.ErrorResponseException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class Util {

    public static String authzErr(AuthzRequest req, String error, String description) {
        if (!StringUtils.hasLength(req.getRedirectUri())) {
            throw new ErrorResponseException(error);
        }
        return "redirect:" + UriComponentsBuilder.fromUriString(req.getRedirectUri())
                .queryParam("error", error)
                .queryParamIfPresent("error_description", Optional.ofNullable(description))
                .queryParamIfPresent("state", Optional.ofNullable(req.getState()))
                .build();
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("UTC"));
    }
}
