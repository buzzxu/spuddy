package io.github.buzzxu.spuddy.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.errors.SecurityException;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.security.exceptions.TokenAuthException;
import io.github.buzzxu.spuddy.security.exceptions.TokenExpiredException;
import io.github.buzzxu.spuddy.security.exceptions.TokenGenerateException;
import io.github.buzzxu.spuddy.security.objects.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.auth0.jwt.RegisteredClaims.SUBJECT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static io.github.buzzxu.spuddy.util.Dates.asDate;

public class JWTs {
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired(required = false)
    private HttpServletRequest request;


    public Long id(String token) throws TokenAuthException {
        try {
            if(Strings.isNullOrEmpty(token)){
                throw new TokenAuthException("无法获取令牌,请检查令牌是否存在");
            }
            DecodedJWT jwt = JWT.decode(token);
            Claim id = jwt.getClaim(SUBJECT);
            if(id.isNull()){
                throw new TokenAuthException("无法获取Token内容,请验证Token合法性");
            }
            return id.as(Long.class);
        }catch (TokenAuthException ex){
            throw ex;
        }catch (JWTDecodeException ex){
            throw new TokenAuthException("登录已失效");
        }

    }
    public  int region(String token) {
        return parse(token, "region", Integer.class);
    }
    public <T> T parse(String token,String name,Class<T> clazz) throws TokenAuthException {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim claim =jwt.getClaim(name);
            if(claim.isNull()){
                throw new TokenAuthException("无法获取Token内容,请验证Token合法性");
            }
            return  claim.as(clazz);
        }catch (JWTDecodeException ex){
            throw new TokenAuthException("登录已失效");
        }

    }

    public <T> Optional<T> get(String token, String name, Function<Claim,T> function) throws SecurityException {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim claim = jwt.getClaim(name);
            return !claim.isNull() ? Optional.of(function.apply(claim)) : Optional.empty();
        }catch (ApplicationException e) {
            throw e;
        }catch (JWTDecodeException ex){
            throw new io.github.buzzxu.spuddy.errors.SecurityException("登录已失效",401);
        }

    }

    public <U extends UserInfo> String create(U user, Consumer<JWTCreator.Builder> consumer, Supplier<String> secretKey) throws TokenGenerateException {
        return generateToken(String.valueOf(user.getId()),consumer,secretKey);
    }


    public boolean verify(String token,String secretKey) throws TokenAuthException, TokenExpiredException {
        return verify(token,null,secretKey);
    }

    public boolean  verify(String token,Consumer<DecodedJWT> consumer,String secretKey) throws TokenAuthException, TokenExpiredException {
        checkNotNull(token, "Token is null");
        checkArgument(!Strings.isNullOrEmpty(secretKey),"Token 签名值不能为空");
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC512(secretKey))
                    .withIssuer(host())
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if(consumer != null){
                consumer.accept(jwt);
            }
        } catch (ApplicationException e) {
            throw e;
        }catch (com.auth0.jwt.exceptions.TokenExpiredException ex){
            throw new TokenExpiredException();
        } catch (IllegalArgumentException| JWTVerificationException e) {
            throw new TokenAuthException();
        }catch (Exception e) {
            throw new TokenAuthException();
        }
        return true;
    }

    public String generateToken(String sub, Consumer<JWTCreator.Builder> consumer, Supplier<String> secretKey) throws TokenGenerateException {
        checkArgument(!Strings.isNullOrEmpty(sub),"客户端唯一标识不能为空");
        JWTCreator.Builder jwt = JWT.create();
        jwt.withIssuer(host());
        jwt.withSubject(sub);
        LocalDateTime now = LocalDateTime.now();
        //创建时间
        jwt.withIssuedAt(asDate(now));
        //过期时间
        jwt.withExpiresAt(asDate(now.plusDays(jwtConfig.getExpiration())));
        try {
            if(consumer != null){
                consumer.accept(jwt);
            }
            return jwt.sign(Algorithm.HMAC512(secretKey.get()));
        } catch (ApplicationException e) {
            throw e;
        }catch (IllegalArgumentException | JWTCreationException e) {
            throw new TokenGenerateException();
        }catch(Exception ex){
            throw ApplicationException.raise(ex);
        }
    }

    private String host(){
        return jwtConfig.isDef() ? jwtConfig.getIssuer() : request.getRemoteHost();
    }
}
