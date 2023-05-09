package org.omegat.plugins.baidu;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.gui.exttrans.MTConfigDialog;
import org.omegat.util.*;

import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

public class BaiduTranslate extends BaseTranslate {
    protected static final String PROPERTY_API_ID = "baidu.app.id";
    protected static final String PROPERTY_API_KEY = "baidu.app.key";
    protected static final String BASE_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    @Override
    protected String getPreferenceName() {
        return "allow_baidu_translate";
    }

    @Override
    public String getName() {
        return "Baidu translate";
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String trText = text.length() > 2000 ? text.substring(0, 1997) + "..." : text;
        String prev = getFromCache(sLang, tLang, trText);
        if (prev != null) {
            return prev;
        }

        String apiID = getCredential(PROPERTY_API_ID);
        String apiKey = getCredential(PROPERTY_API_KEY);

        if (apiKey == null || apiKey.isEmpty()) {
            return "apikey required";
        }

        if (apiID == null || apiID.isEmpty()) {
            return "APP ID required";
        }
        StringBuilder tr = new StringBuilder();
        try {
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder
                    = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder();
            urlBuilder.addQueryParameter(BaiduRequest.q, text);
            urlBuilder.addQueryParameter(BaiduRequest.from, formatLang(sLang));
            urlBuilder.addQueryParameter(BaiduRequest.to, formatLang(tLang));
            urlBuilder.addQueryParameter(BaiduRequest.appid, apiID);
            String salt = UUID.randomUUID().toString();
            urlBuilder.addQueryParameter(BaiduRequest.salt, salt);

            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((apiID + text + salt + apiKey).getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();
            String sign = DatatypeConverter
                    .printHexBinary(digest).toLowerCase();
            urlBuilder.addQueryParameter(BaiduRequest.sign, sign);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            assert response.body() != null;
            BaiduResponse baiduResponse = JSON.parseObject(response.body().string(), BaiduResponse.class);
            if (baiduResponse.getError_code() != 0) {
                return "error code: " + baiduResponse.getError_code();
            }
            int size = baiduResponse.getTrans_result().size();
            for (int i = 0; i < size; i++) {
                if (i < size - 1) {
                    tr.append(baiduResponse.getTrans_result().get(i).getDst()).append("\n");
                    continue;
                }
                tr.append(baiduResponse.getTrans_result().get(i).getDst());
            }
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
        if (tr.toString().equals("")) {
            return "";
        }
        putToCache(sLang, tLang, trText, tr.toString());
        return tr.toString();
    }

    private String formatLang(Language language) {
        String lang = language.getLanguage();
        if (lang.equalsIgnoreCase("zh-cn") || lang.equalsIgnoreCase("zh-hk") || lang.equalsIgnoreCase("zh-tw")) {
            return "zh";
        } else if (lang.equalsIgnoreCase("ja")) {
            return "jp";
        } else if (lang.equalsIgnoreCase("ko")) {
            return "kr";
        } else if (lang.contains("en")) {
            return "en";
        } else {
            return language.getLanguageCode().toLowerCase();
        }

    }

    public static void loadPlugins() {
        Core.registerMachineTranslationClass(BaiduTranslate.class);
    }

    public static void unloadPlugins() {
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void showConfigurationUI(Window parent) {

        MTConfigDialog dialog = new MTConfigDialog(parent, getName()) {
            @Override
            protected void onConfirm() {
                String appId = panel.valueField1.getText().trim();
                String appKey = panel.valueField2.getText().trim();
                boolean temporary = panel.temporaryCheckBox.isSelected();
                setCredential(PROPERTY_API_ID, appId, temporary);
                setCredential(PROPERTY_API_KEY, appKey, temporary);
            }
        };

        dialog.panel.valueLabel1.setText("APP ID");
        dialog.panel.valueField1.setText(getCredential(PROPERTY_API_ID));

        dialog.panel.valueLabel2.setText("APP KEY");
        dialog.panel.valueField2.setText(getCredential(PROPERTY_API_KEY));

        dialog.panel.temporaryCheckBox.setSelected(isCredentialStoredTemporarily(PROPERTY_API_ID));

        dialog.show();
    }
}
