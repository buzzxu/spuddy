package io.github.buzzxu.spuddy.util;

import com.google.common.base.Strings;
import io.github.buzzxu.spuddy.exceptions.ApplicationException;
import io.github.buzzxu.spuddy.objects.Pair;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.FilterFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.EncoderHelper;
import org.patchca.word.AdaptiveRandomWordFactory;
import org.patchca.word.WordFactory;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @program:
 * @description:
 * @author: 徐翔
 * @create: 2018-08-15 21:21
 **/
public class Captcha {

    private final ConfigurableCaptchaService captchaService;
    private final String format;


    private Captcha(ConfigurableCaptchaService captchaService, String format) {
        this.captchaService = captchaService;
        this.format = format;
    }

    public Pair<String, String> generator() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            String valiCode = EncoderHelper.getChallangeAndWriteImage(captchaService, format, bos);
            String image = Base64.getEncoder().encodeToString(bos.toByteArray());
            return Pair.of(valiCode,image);
        } catch (IOException e) {
            throw ApplicationException.raise(e);
        }
    }
    public String generator(String fileName){
        try (FileOutputStream fos = new FileOutputStream(fileName+"."+format)){
            return EncoderHelper.getChallangeAndWriteImage(captchaService, format, fos);
        } catch (IOException e) {
            throw ApplicationException.raise(e);
        }
    }
    public static class Builder {

        private int maxLength;
        private int minLength;
        private Color color;
        private FilterFactory filterFactory;
        private WordFactory wordFactory;
        private String format;

        public Builder max(int max){
            this.maxLength = max;
            return this;
        }
        public Builder min(int min){
            this.maxLength = min;
            return this;
        }
        public Builder color(String colorHex){
            this.color = Colors.hex2Rgb(colorHex);
            return this;
        }
        public Builder filterFactory(FilterFactory filterFactory){
            this.filterFactory = filterFactory;
            return this;
        }

        /**
         * 验证码生成
         * @param wordFactory
         * @return
         */
        public Builder wordFactory(WordFactory wordFactory){
            this.wordFactory = wordFactory;
            return this;
        }
        public Builder format(String format){
            this.format = format;
            return this;
        }

        public Captcha build(){
            ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
            AdaptiveRandomWordFactory word = new AdaptiveRandomWordFactory();
            if(minLength == 0){
                minLength = 4;
            }
            if(maxLength == 0){
                maxLength = 4;
            }
            word.setMinLength(minLength);
            word.setMaxLength(maxLength);
            cs.setWordFactory(word);

            if(color ==null){
                color = new Color(25, 60, 170);
            }
            cs.setColorFactory(new SingleColorFactory(color));
            if(filterFactory == null){
                filterFactory = new CurvesRippleFilterFactory(cs.getColorFactory());
            }
            if(wordFactory != null){
                cs.setWordFactory(wordFactory);
            }
            cs.setFilterFactory(filterFactory);
            return new Captcha(cs,!Strings.isNullOrEmpty(format)? format : "png");
        }

    }
    public static void main(String[] args) throws Exception {
        Captcha captcha = new Builder().wordFactory(() -> Random.numeric(4)).filterFactory(new WobbleRippleFilterFactory()).color("#E2215D").build();
//        System.out.println(captcha.generator());
        System.out.println(captcha.generator("demo_captcha"));
    }
}
