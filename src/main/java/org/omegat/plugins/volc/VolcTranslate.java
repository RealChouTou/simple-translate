package org.omegat.plugins.volc;

import com.alibaba.fastjson.JSON;
import com.volcengine.model.request.translate.TranslateTextRequest;
import com.volcengine.model.response.translate.TranslateTextResponse;
import com.volcengine.service.translate.ITranslateService;
import com.volcengine.service.translate.impl.TranslateServiceImpl;
import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseTranslate;
import org.omegat.gui.exttrans.MTConfigDialog;
import org.omegat.util.Language;

import java.awt.*;
import java.util.Collections;

public class VolcTranslate extends BaseTranslate {
    protected static final String PROPERTY_AK = "volc.app.ak";
    protected static final String PROPERTY_SK = "volc.app.sk";

    @Override
    protected String getPreferenceName() {
        return "allow_volc_translate";
    }

    @Override
    public String getName() {
        return "Volc translate";
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String trText = text.length() > 2000 ? text.substring(0, 1997) + "..." : text;
        String prev = getFromCache(sLang, tLang, trText);
        if (prev != null) {
            return prev;
        }

        String ak = getCredential(PROPERTY_AK);
        String sk = getCredential(PROPERTY_SK);

        if (ak == null || ak.isEmpty()) {
            return "access key required";
        }

        if (sk == null || sk.isEmpty()) {
            return "secret key required";
        }

        ITranslateService translateService = TranslateServiceImpl.getInstance();

        translateService.setAccessKey(ak);
        translateService.setSecretKey(sk);

        String result = null;
        // translate text
        try {
            TranslateTextRequest translateTextRequest = new TranslateTextRequest();
            translateTextRequest.setSourceLanguage(formatLang(sLang)); // 不设置表示自动检测
            translateTextRequest.setTargetLanguage(formatLang(tLang));
            translateTextRequest.setTextList(Collections.singletonList(text));

            TranslateTextResponse translateText = translateService.translateText(translateTextRequest);
            System.out.println(JSON.toJSONString(translateText));
            if (translateText.getTranslationList().size() > 0) {
                result = translateText.getTranslationList().get(0).getTranslation();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            return "zh-Hant";
        } else if (lang.contains("en")) {
            return "en";
        } else {
            return language.getLanguageCode().toLowerCase();
        }

    }

    public static void loadPlugins() {
        Core.registerMachineTranslationClass(VolcTranslate.class);
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
                setCredential(PROPERTY_AK, appId, temporary);
                setCredential(PROPERTY_SK, appKey, temporary);
            }
        };

        dialog.panel.valueLabel1.setText("access key");
        dialog.panel.valueField1.setText(getCredential(PROPERTY_AK));

        dialog.panel.valueLabel2.setText("secret key");
        dialog.panel.valueField2.setText(getCredential(PROPERTY_SK));

        dialog.panel.temporaryCheckBox.setSelected(isCredentialStoredTemporarily(PROPERTY_AK));

        dialog.show();
    }
}

