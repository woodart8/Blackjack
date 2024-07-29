package com.blackjack.request;

public class LoginForm {

    private String memId;
    private String memPwd;

    public LoginForm(String memId, String memPwd) {
        this.memId = memId;
        this.memPwd = memPwd;
    }

    public String getMemId() {
        return memId;
    }

    public void setMemId(String memId) {
        this.memId = memId;
    }

    public String getMemPwd() {
        return memPwd;
    }

    public void setMemPwd(String memPwd) {
        this.memPwd = memPwd;
    }

    @Override
    public String toString() {
        return "LoginForm{" +
                "memId='" + memId + '\'' +
                ", memPwd='" + memPwd + '\'' +
                '}';
    }
}
