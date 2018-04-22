package com.eds.ma.bis.message.vo;

public abstract class MessageContent {

    /**
     * 推送事件 {@link com.eds.ma.bis.message.TmplEvent}
     */
    private String tmplEvent;

    public String getTmplEvent() {
        return tmplEvent;
    }

    public void setTmplEvent(String tmplEvent) {
        this.tmplEvent = tmplEvent;
    }
}
