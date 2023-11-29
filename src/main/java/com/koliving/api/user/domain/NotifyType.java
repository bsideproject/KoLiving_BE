package com.koliving.api.user.domain;

/**
 * author : haedoang date : 2023/11/05 description :
 */
public enum NotifyType {
    ALL,
    SEND,
    RECEIVE;

    public boolean isAll() { return this == ALL; }
    public boolean isSend() {
        return this == SEND;
    }
    public boolean isReceive() {
        return this == RECEIVE;
    }
}
