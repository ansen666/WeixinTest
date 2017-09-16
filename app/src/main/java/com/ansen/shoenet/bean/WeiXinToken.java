package com.ansen.shoenet.bean;

/**
 * Created by xx on 16/6/25.
 */
public class WeiXinToken {
    private String access_token;// 接口调用凭证
    private String expires_in;// access_token接口调用凭证超时时间，单位（秒）
    private String refresh_token;// 用户刷新access_token
    private String openid;// 授权用户唯一标识
    private String scope;// 用户授权的作用域，使用逗号（,）分隔
    private String unionid;// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。

    // 错误返回样例
    private int errcode=0;// 错误码
    private String errmsg;// 错误说明

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
