package org.omegat.plugins.niu;

import org.omegat.plugins.baidu.TransResult;

import java.util.List;

public class NiuResponse {

    private String from;
    private String to;
    private String apikey;
    private String src_text;
    private String tgt_text;
    private String error_code;
    private String error_msg;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getSrc_text() {
        return src_text;
    }

    public void setSrc_text(String src_text) {
        this.src_text = src_text;
    }

    public String getTgt_text() {
        return tgt_text;
    }

    public void setTgt_text(String tgt_text) {
        this.tgt_text = tgt_text;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }
}
