package io.github.buzzxu.spuddy.objects;

import lombok.*;

/**
 * @author xux
 * @date 2023年06月13日 17:50:13
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RealnameVerified {
    /**
     * 真实姓名
     */
    private String realName;
    /**
     * 证件类型
     */
    @Builder.Default
    private int identityType = 0;
    /**
     * 身份证号
     */
    private String identityNo;
    /**
     * 手机号
     */
    private String phone;

}
