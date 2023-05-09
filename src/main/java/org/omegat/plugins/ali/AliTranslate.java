package org.omegat.plugins.ali;


import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.gui.exttrans.MTConfigDialog;
import org.omegat.util.Language;

import java.awt.*;
import java.util.Collections;

public class AliTranslate extends BaseTranslate {
    protected static final String PROPERTY_KI = "ali.app.keyid";
    protected static final String PROPERTY_KS = "ali.app.keyscret";

    @Override
    protected String getPreferenceName() {
        return "allow_ali_translate";
    }

    @Override
    public String getName() {
        return "Ali translate";
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String trText = text.length() > 2000 ? text.substring(0, 1997) + "..." : text;
        String prev = getFromCache(sLang, tLang, trText);
        if (prev != null) {
            return prev;
        }

        String ki = getCredential(PROPERTY_KI);
        String ks = getCredential(PROPERTY_KS);

        if (ki == null || ki.isEmpty()) {
            return "key id required";
        }

        if (ks == null || ks.isEmpty()) {
            return "key secret required";
        }
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // AccessKey ID
                .setAccessKeyId(ki)
                // AccessKey Secret
                .setAccessKeySecret(ks);

        String result = null;
        // 访问的域名
        config.endpoint = "mt.cn-hangzhou.aliyuncs.com";
        com.aliyun.alimt20181012.Client client = new com.aliyun.alimt20181012.Client(config);
        com.aliyun.alimt20181012.models.TranslateGeneralRequest translateGeneralRequest = new com.aliyun.alimt20181012.models.TranslateGeneralRequest()
                .setFormatType("text")
                .setSourceLanguage(formatLang(sLang))
                .setTargetLanguage(formatLang(tLang))
                .setSourceText(text)
                .setScene("general");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            com.aliyun.alimt20181012.models.TranslateGeneralResponse response = client.translateGeneralWithOptions(translateGeneralRequest, runtime);
            if (response.getBody().code != 200) {
                return String.valueOf(response.getBody().code);
            }
            if (response.body == null) {
                return "body == null";
            }
            if (response.body.data == null) {
                return "body.data == null";
            }
            result = response.body.data.translated;
        } catch (Exception _error) {
            _error.printStackTrace();
        }

        if (result == null) {
            return "";
        }
        return result;
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

    public static void loadPlugins() {
        Core.registerMachineTranslationClass(org.omegat.plugins.ali.AliTranslate.class);
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
                setCredential(PROPERTY_KI, appId, temporary);
                setCredential(PROPERTY_KS, appKey, temporary);
            }
        };

        dialog.panel.valueLabel1.setText("key id");
        dialog.panel.valueField1.setText(getCredential(PROPERTY_KI));

        dialog.panel.valueLabel2.setText("key secret");
        dialog.panel.valueField2.setText(getCredential(PROPERTY_KS));

        dialog.panel.temporaryCheckBox.setSelected(isCredentialStoredTemporarily(PROPERTY_KI));

        dialog.show();
    }
}

