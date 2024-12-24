package io.github.buzzxu.spuddy.security.jwt;

import com.google.common.base.Strings;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static com.google.common.base.Preconditions.checkArgument;

@Component
@Getter
public class JwtConfig {

    private final String issuer;
    /**
     * 时效 单位秒
     */
    private final long expiration;

    private final boolean def;

    public JwtConfig(@Value("${jwt.issuer:localhost}")String issuer
            , @Value("${jwt.expiration:7}") int expiration
            , @Value("${jwt.def:true}") boolean def) {
        this.issuer = issuer;
        this.expiration = Duration.ofDays(expiration).toSeconds();
        this.def = def;

    }
}
