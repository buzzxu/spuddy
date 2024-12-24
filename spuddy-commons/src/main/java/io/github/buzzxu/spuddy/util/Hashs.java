package io.github.buzzxu.spuddy.util;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.CRC32;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2019-10-08 20:12
 **/
public class Hashs {

    private static final int MURMUR_SEED = 1318007700;
    private static final ThreadLocal<MessageDigest> MD5_DIGEST = createThreadLocalMessageDigest("MD5");
    private static final ThreadLocal<MessageDigest> SHA_1_DIGEST = createThreadLocalMessageDigest("SHA-1");
    private static final SecureRandom random = new SecureRandom();

    public Hashs() {
    }

    private static ThreadLocal<MessageDigest> createThreadLocalMessageDigest(final String digest) {
        return ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance(digest);
            } catch (NoSuchAlgorithmException var2) {
                throw new RuntimeException("unexpected exception creating MessageDigest instance for [" + digest + ']', var2);
            }
        });
    }

    public static byte[] sha1(byte[] input) {
        return digest(input, get(SHA_1_DIGEST), null, 1);
    }

    public static byte[] sha1(String input) {
        return digest(input.getBytes(Charsets.UTF_8), get(SHA_1_DIGEST), null, 1);
    }
    public static String sha116(ByteBuffer input){
        MessageDigest md = get(SHA_1_DIGEST);
        md.update(input);
        return BaseEncoding.base16().lowerCase().encode(md.digest());
    }
    public static String sha116(byte[] input) {
        return sha116(ByteBuffer.wrap(input));
    }
    public static String sha116(String input) {
        return sha116(input.getBytes());
    }


    public static byte[] sha1(byte[] input,byte[] salt) {
        return digest(input, get(SHA_1_DIGEST), salt, 1);
    }

    public static byte[] sha1( String input, byte[] salt) {
        return digest(input.getBytes(Charsets.UTF_8), get(SHA_1_DIGEST), salt, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
        return digest(input, get(SHA_1_DIGEST), salt, iterations);
    }

    public static byte[] sha1(String input, byte[] salt, int iterations) {
        return digest(input.getBytes(Charsets.UTF_8), get(SHA_1_DIGEST), salt, iterations);
    }
    public static String sha256(String input) {
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }
    public static String sha256(byte[] input) {
        return Hashing.sha256().hashBytes(input).toString();
    }
    private static MessageDigest get(ThreadLocal<MessageDigest> messageDigest) {
        MessageDigest instance = messageDigest.get();
        instance.reset();
        return instance;
    }

    private static byte[] digest(byte[] input, MessageDigest digest, byte[] salt, int iterations) {
        if (salt != null) {
            digest.update(salt);
        }

        byte[] result = digest.digest(input);

        for(int i = 1; i < iterations; ++i) {
            digest.reset();
            result = digest.digest(result);
        }

        return result;
    }

    public static byte[] generateSalt(int numBytes) {
        Validate.isTrue(numBytes > 0, "numBytes argument must be a positive integer (1 or larger)", numBytes);
        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] sha1File(InputStream input) throws IOException {
        return digestFile(input, get(SHA_1_DIGEST));
    }

    public static byte[] md5File(InputStream input) throws IOException {
        return digestFile(input, get(MD5_DIGEST));
    }


    private static byte[] digestFile(InputStream input, MessageDigest messageDigest) throws IOException {
        int bufferLength = 8192;
        byte[] buffer = new byte[bufferLength];

        for(int read = input.read(buffer, 0, bufferLength); read > -1; read = input.read(buffer, 0, bufferLength)) {
            messageDigest.update(buffer, 0, read);
        }

        return messageDigest.digest();
    }

    public static int crc32AsInt(String input) {
        return crc32AsInt(input.getBytes(Charsets.UTF_8));
    }

    public static int crc32AsInt(byte[] input) {
        CRC32 crc32 = new CRC32();
        crc32.update(input);
        return (int)crc32.getValue();
    }

    public static long crc32AsLong(String input) {
        return crc32AsLong(input.getBytes(Charsets.UTF_8));
    }

    public static long crc32AsLong(byte[] input) {
        CRC32 crc32 = new CRC32();
        crc32.update(input);
        return crc32.getValue();
    }

    public static int murmur32AsInt(byte[] input) {
        return Hashing.murmur3_32_fixed(MURMUR_SEED).hashBytes(input).asInt();
    }

    public static int murmur32AsInt(String input) {
        return Hashing.murmur3_32_fixed(MURMUR_SEED).hashString(input, Charsets.UTF_8).asInt();
    }

    public static long murmur128AsLong(byte[] input) {
        return Hashing.murmur3_128(MURMUR_SEED).hashBytes(input).asLong();
    }

    public static long murmur128AsLong(String input) {
        return Hashing.murmur3_128(MURMUR_SEED).hashString(input, Charsets.UTF_8).asLong();
    }

    public static long crc32Code(byte[] bytes) {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }
}
