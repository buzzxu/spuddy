package io.github.buzzxu.spuddy.security.services;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.objects.Pair;
import io.github.buzzxu.spuddy.redis.Redis;
import io.github.buzzxu.spuddy.util.Captcha;
import io.github.buzzxu.spuddy.util.Duration;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author xux
 * @date 2024年12月28日 22:28:30
 */
public class CaptchaService {
    private static final String KEY_CAPTCHA = "valicode:captcha:";
    @Autowired(required = false)
    private Captcha captcha;
    @Resource
    private Redis redis;
    private final long expire;

    public CaptchaService() {
        this("30s");
    }

    @Inject
    public CaptchaService(@Value("${cache.expire.valicode:30s}") String expire) {
        this.expire = Duration.parse(expire).toSeconds();
    }

    public Pair<String, String> generator(String key) {
        String $key = Strings.isNullOrEmpty(key) ? RandomStringUtils.randomNumeric(6) : key;
        Pair<String, String> $captcha = this.captcha.generator();
        this.storeCode("valicode:captcha:" + $key,$captcha.getKey(), this.expire);
        return Pair.of($key, $captcha.getValue());
    }

    public boolean check(String key, String valiCode) {
        return this.checkCode("valicode:captcha:" + key, valiCode);
    }

    protected void storeCode(String key, String code, long seconds) {
        this.redis.execute((redis) -> redis.setex(key, seconds, code));
    }

    protected boolean checkCode(String key, String code) {
        return this.redis.execute((redis) -> {
            if (redis.exists(key) ){
                try {
                    return StringUtils.equals(code, redis.get(key));
                } finally {
                    redis.del(key);
                }
            } else {
                return false;
            }
        });
    }

    protected boolean checkCodeClear(String key, String code) {
        return this.redis.execute((redis) -> {
            String val = redis.get(key);
            if (!Strings.isNullOrEmpty(val) && StringUtils.equals(code, val)) {
                redis.del(key);
                return true;
            } else {
                return false;
            }
        });
    }
}
