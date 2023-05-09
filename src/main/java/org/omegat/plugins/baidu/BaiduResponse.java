package org.omegat.plugins.baidu;

import java.util.List;

public class BaiduResponse {

    private String from;
    private String to;
    private List<TransResult> trans_result;
    private int error_code;
    private String src_tts;
    private String dst_tts;
    private String dict;

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

    public List<TransResult> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TransResult> trans_result) {
        this.trans_result = trans_result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getSrc_tts() {
        return src_tts;
    }

    public void setSrc_tts(String src_tts) {
        this.src_tts = src_tts;
    }

    public String getDst_tts() {
        return dst_tts;
    }

    public void setDst_tts(String dst_tts) {
        this.dst_tts = dst_tts;
    }

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }
}
