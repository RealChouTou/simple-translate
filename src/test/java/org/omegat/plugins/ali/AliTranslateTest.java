package org.omegat.plugins.ali;

import junit.framework.TestCase;
import org.junit.Test;
import org.omegat.util.Language;

public class AliTranslateTest extends TestCase {
    @Test
    public void test() throws Exception {

        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // AccessKey ID
                .setAccessKeyId("id")
                // AccessKey Secret
                .setAccessKeySecret("secret");

        String result = null;
        // 访问的域名
        config.endpoint = "mt.cn-hangzhou.aliyuncs.com";
        com.aliyun.alimt20181012.Client client = new com.aliyun.alimt20181012.Client(config);
        com.aliyun.alimt20181012.models.TranslateGeneralRequest translateGeneralRequest = new com.aliyun.alimt20181012.models.TranslateGeneralRequest()
                .setFormatType("text")
                .setSourceLanguage("zh")
                .setTargetLanguage("en")
                .setSourceText("开心")
                .setScene("general");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            com.aliyun.alimt20181012.models.TranslateGeneralResponse response = client.translateGeneralWithOptions(translateGeneralRequest, runtime);
            System.out.println(com.aliyun.teautil.Common.toJSONString(response));
            if (response.getBody().code != 200) {
                System.out.println(response.body);
            }
            if (response.body == null) {
                System.out.println("body == null");
            }
            if (response.body.data == null) {
                System.out.println("data == null");
            }
            result = response.body.data.translated;
        } catch (Exception _error) {
            _error.printStackTrace();
        }
        System.out.println("result: " + result);
    }

    private String formatLang(Language language) {
        String lang = language.getLanguage();
        if (lang.equalsIgnoreCase("zh-cn")) {
            return "zh";
        } else if (lang.equalsIgnoreCase("zh-hk") || lang.equalsIgnoreCase("zh-tw")) {
            return "zh-tw";
        } else if (lang.contains("en")) {
            return "en";
        } else {
            return language.getLanguageCode().toLowerCase();
        }

    }
}