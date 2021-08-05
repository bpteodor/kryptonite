package tech.bran.idp.util.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import tech.bran.idp.api.model.AuthzRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Teodor Bran
 */
@Slf4j
@RequiredArgsConstructor(staticName = "that")
public class Check<T> {

    private final T value;
    private Predicate<? super T> predicate;

    public Check<T> is(Predicate<? super T> expr) {
        isValid(expr);
        return this;
    }

    public Check<T> isEqualTo(T code) {
        return isValid(x -> Objects.equals(code, x));
    }

    public <X extends CharSequence> Check<X> isNotNull() {
        isValid((x) -> x != null);
        return (Check<X>) this;
    }

    public <X extends CharSequence> Check<X> isNotEmpty() {
        isValid((x) -> x instanceof CharSequence && ((CharSequence) x).length() > 0
                || x instanceof Collection && ((Collection<?>) x).size() > 0);
        return (Check<X>) this;
    }

    public <X extends String> Check<X> matches(String regex) {
        isValid((x) -> x != null && ((String) x).matches(regex));
        return (Check<X>) this;
    }

    public <X extends String> Check<X> emptyOrMatches(String regex) {
        isValid((x) -> x == null || "".equals(x) || ((String) x).matches(regex));
        return (Check<X>) this;
    }

    public <T extends String> Check<T> emptyOr(Predicate<Object> expr) {
        isValid((x) -> x == null || "".equals(x) || expr.test(x));
        return (Check<T>) this;
    }

    public Check<T> isContainedIn(Collection<T> collection) {
        isValid(x -> collection != null && x != null && collection.contains(x));
        return this;
    }

    public <E, X extends Collection<E>> Check<X> areContainedIn(Collection<E> collection) {
        isValid(x -> collection != null && x != null &&
                ((x instanceof Collection && collection.containsAll((Collection<E>) x)) ||
                        (x instanceof Object[] && collection.containsAll(Arrays.asList((E[]) x))) ||
                        collection.contains(x)));
        return (Check<X>) this;
    }

    /**
     * @param expr predicate that must resolve to true if the field is valid
     */
    public Check<T> isValid(Predicate<? super T> expr) {
        predicate = expr;
        return this;
    }

    public void orSendAuthorizationError(AuthzRequest req, String error) {
        orSendAuthorizationError(req, error, null);
    }

    public void orSendAuthorizationError(AuthzRequest req, String error, String description) {
        if (!predicate.test(value)) {
            Assert.hasLength(req.getRedirectUri(), "expected non empty string");
            throw new AuthzResponseException(req, error, description);
        }
    }

    public void orSend(String msg, Object... params) {
        if (!predicate.test(value)) {
            final String error = String.format(msg, params);
            log.debug(error);
            throw new ResponseException(error);
        }
    }

    public Check<T> emptyOrValidUri() {
        return isValid((x) -> x == null || "".equals(x) || isValidUri((String) x));
    }

    private static <T> boolean isString(T x) {
        return x instanceof CharSequence && ((CharSequence) x).length() > 0;
    }

    private boolean isValidUri(String x) {
        try {
            new URI(x);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }
}
