package org.omegat.plugins.niu;

import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.gui.exttrans.MTConfigDialog;
import org.omegat.util.Language;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class NiuTranslate extends BaseTranslate {
    protected static final String PROPERTY_API_KEY = "niu.app.key";
    protected static final String BASE_URL = "https://api.niutrans.com/NiuTransServer/translation";

    @Override
    protected String getPreferenceName() {
        return "allow_niu_translate";
    }

    @Override
    public String getName() {
        return "Niu translate";
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String trText = text.length() > 2000 ? text.substring(0, 1997) + "..." : text;
        String prev = getFromCache(sLang, tLang, trText);
        if (prev != null) {
            return prev;
        }

        String apiKey = getCredential(PROPERTY_API_KEY);

        if (apiKey == null || apiKey.isEmpty()) {
            return "apikey required";
        }
        String result = null;
        try {
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder
                    = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder();
            urlBuilder.addQueryParameter(NiuRequest.src_text, text);
            urlBuilder.addQueryParameter(NiuRequest.from, formatLang(sLang));
            urlBuilder.addQueryParameter(NiuRequest.to, formatLang(tLang));
            urlBuilder.addQueryParameter(NiuRequest.apikey, apiKey);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            Response response = call.execute();
            assert response.body() != null;
            NiuResponse niuResponse = JSON.parseObject(response.body().string(), NiuResponse.class);
            if (niuResponse.getError_code() != null) {
                return "error code: " + niuResponse.getError_code();
            }
            result = niuResponse.getTgt_text();
        } catch (IOException e) {
            return e.getLocalizedMessage();
        }
        if (result == null) {
            return "";
        }
        putToCache(sLang, tLang, trText, result);
        return result;
    }

    private String formatLang(Language language) {
        String lang = language.getLanguage();
        if (lang.equalsIgnoreCase("zh-cn")) {
            return "zh";
        } else if (lang.equalsIgnoreCase("zh-hk") || lang.equalsIgnoreCase("zh-tw")) {
            return "cht";
        } else if (lang.contains("en")) {
            return "en";
        } else {
            return language.getLanguageCode().toLowerCase();
        }
    }

    public static void loadPlugins() {
        Core.registerMachineTranslationClass(NiuTranslate.class);
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
                String apikey = panel.valueField1.getText().trim();
                boolean temporary = panel.temporaryCheckBox.isSelected();
                setCredential(PROPERTY_API_KEY, apikey, temporary);
            }
        };
        dialog.panel.valueLabel1.setText("apikey");
        dialog.panel.valueField1.setText(getCredential(PROPERTY_API_KEY));
        dialog.panel.valueLabel2.setVisible(false);
        dialog.panel.valueField2.setVisible(false);
        dialog.panel.temporaryCheckBox.setSelected(isCredentialStoredTemporarily(PROPERTY_API_KEY));
        dialog.show();
    }
}

