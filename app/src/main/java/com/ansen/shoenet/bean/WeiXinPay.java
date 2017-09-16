package com.ansen.shoenet.bean;

/**
 * @author ansen
 * @create time 2017-09-15
 */
public class WeiXinPay{
    private String noncestr="";//随机字符串，不长于32位。推荐随机数生成算法
    private String package_value="";//暂填写固定值Sign=WXPay
    private String partnerid="";//微信支付分配的商户号
    private String prepayid="";//微信返回的支付交易会话ID
    private String timestamp="";//时间戳
    private String sign="";//签名

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPackage_value() {
        return package_value;
    }

    public void setPackage_value(String package_value) {
        this.package_value = package_value;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
