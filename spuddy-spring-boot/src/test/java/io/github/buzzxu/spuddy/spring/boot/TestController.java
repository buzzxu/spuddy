package io.github.buzzxu.spuddy.spring.boot;


import io.github.buzzxu.spuddy.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 徐翔
 * @create 2021-08-25 15:07
 **/
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/of")
    public R<String> of() {
        return R.of("OK");
    }
}
