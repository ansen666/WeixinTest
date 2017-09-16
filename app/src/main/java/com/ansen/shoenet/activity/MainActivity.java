package com.ansen.shoenet.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.RequestDataCallback;
import com.ansen.shoenet.R;
import com.ansen.shoenet.bean.WeiXin;
import com.ansen.shoenet.bean.WeiXinInfo;
import com.ansen.shoenet.bean.WeiXinPay;
import com.ansen.shoenet.bean.WeiXinToken;
import com.ansen.shoenet.utils.Constant;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private IWXAPI wxAPI;
    private TextView tvNickname,tvAge;
    public static final int IMAGE_SIZE=32768;//微信分享图片大小限制

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);//注册
        wxAPI = WXAPIFactory.createWXAPI(this,Constant.WECHAT_APPID,true);
        wxAPI.registerApp(Constant.WECHAT_APPID);

        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_share_friend_circle).setOnClickListener(this);
        findViewById(R.id.btn_share_friend).setOnClickListener(this);
        findViewById(R.id.btn_pay).setOnClickListener(this);

        tvNickname= (TextView) findViewById(R.id.tv_nickname);
        tvAge=(TextView) findViewById(R.id.tv_age);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login://微信登录
                login();
                break;
            case R.id.btn_share_friend_circle://微信分享到朋友圈
                share(true);
                break;
            case R.id.btn_share_friend://微信分享给朋友
                share(false);
                break;
            case R.id.btn_pay://微信支付
//              先去服务器获取支付信息，返回一个WeiXinPay对象，然后调用pay方法
                showToast("微信支付需要服务器支持");
                break;
        }
    }

    /**
     * 这里用到的了EventBus框架
     * 博客教程:http://blog.csdn.net/lmj623565791/article/details/40920453
     * @param weiXin
     */
    @Subscribe
    public void onEventMainThread(WeiXin weiXin){
        Log.i("ansen","收到eventbus请求 type:"+weiXin.getType());
        if(weiXin.getType()==1){//登录
            getAccessToken(weiXin.getCode());
        }else if(weiXin.getType()==2){//分享
            switch (weiXin.getErrCode()){
                case BaseResp.ErrCode.ERR_OK:
                    Log.i("ansen", "微信分享成功.....");
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://分享取消
                    Log.i("ansen", "微信分享取消.....");
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://分享被拒绝
                    Log.i("ansen", "微信分享被拒绝.....");
                    break;
            }
        }else if(weiXin.getType()==3){//微信支付
            if(weiXin.getErrCode()==BaseResp.ErrCode.ERR_OK){//成功
                Log.i("ansen", "微信支付成功.....");
            }
        }
    }

    /**
     * 微信登陆(三个步骤)
     * 1.微信授权登陆
     * 2.根据授权登陆code 获取该用户token
     * 3.根据token获取用户资料
     */
    public void login(){
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = String.valueOf(System.currentTimeMillis());
        wxAPI.sendReq(req);
    }

    public void getAccessToken(String code){
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid="+Constant.WECHAT_APPID+"&secret="+Constant.WECHAT_SECRET+
                "&code="+code+"&grant_type=authorization_code";
        HTTPCaller.getInstance().get(WeiXinToken.class, url, null, new RequestDataCallback<WeiXinToken>() {
            @Override
            public void dataCallback(WeiXinToken obj) {
                if(obj.getErrcode()==0){//请求成功
                    getWeiXinUserInfo(obj);
                }else{//请求失败
                    showToast(obj.getErrmsg());
                }
            }
        });
    }

    public void getWeiXinUserInfo(WeiXinToken weiXinToken){
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+
                weiXinToken.getAccess_token()+"&openid="+weiXinToken.getOpenid();
        HTTPCaller.getInstance().get(WeiXinInfo.class, url, null, new RequestDataCallback<WeiXinInfo>() {
            @Override
            public void dataCallback(WeiXinInfo obj) {
                tvNickname.setText("昵称:"+obj.getNickname());
                tvAge.setText("年龄:"+obj.getAge());
                Log.i("ansen","头像地址:"+obj.getHeadimgurl());
            }
        });
    }

    /**
     * 微信分享
     * @param friendsCircle  是否分享到朋友圈
     */
    public void share(boolean friendsCircle){
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "www.baidu.com";//分享url
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = "分享标题";
        msg.description = "分享描述";
        msg.thumbData =getThumbData();//封面图片byte数组

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = friendsCircle ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        wxAPI.sendReq(req);
    }

    /**
     * 获取分享封面byte数组 我们这边取的是软件启动icon
     * @return
     */
    private  byte[] getThumbData() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=2;
        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher,options);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        int quality = 100;
        while (output.toByteArray().length > IMAGE_SIZE && quality != 10) {
            output.reset(); // 清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, output);// 这里压缩options%，把压缩后的数据存放到baos中
            quality -= 10;
        }
        bitmap.recycle();
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发起支付
     * @param weiXinPay
     */
    public void pay(WeiXinPay weiXinPay){
        PayReq req = new PayReq();
        req.appId = Constant.WECHAT_APPID;//appid
        req.nonceStr=weiXinPay.getNoncestr();//随机字符串，不长于32位。推荐随机数生成算法
        req.packageValue=weiXinPay.getPackage_value();//暂填写固定值Sign=WXPay
        req.sign=weiXinPay.getSign();//签名
        req.partnerId=weiXinPay.getPartnerid();//微信支付分配的商户号
        req.prepayId=weiXinPay.getPrepayid();//微信返回的支付交易会话ID
        req.timeStamp=weiXinPay.getTimestamp();//时间戳

        wxAPI.registerApp(Constant.WECHAT_APPID);
        wxAPI.sendReq(req);
    }

    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }
}
