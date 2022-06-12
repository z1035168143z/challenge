package io.zzr.nio.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import java.io.IOException;

/**
 * @author zrzhao
 * @date 2022/6/11
 */
public class Base64ToMultipart {

    public static MultipartFile base64ToMultipart(String base64) {
        if (StringUtils.isBlank(base64)) {
            return null;
        }
        try {
            String[] baseArr = base64.split(",");
            if (baseArr.length < 2) {
                return null;
            }

            BASE64Decoder decoder = new BASE64Decoder();
            byte[] b = decoder.decodeBuffer(baseArr[1]);

            for(int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return new BASE64DecodedMultipartFile(b, baseArr[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
