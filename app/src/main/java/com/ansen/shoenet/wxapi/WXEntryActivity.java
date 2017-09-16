package com.ansen.shoenet.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ansen.shoenet.bean.WeiXin;
import com.ansen.shoenet.utils.Constant;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

/**
 * 微信登陆分享回调Activity
 * @author 安辉
 * @create time 2015-05-25
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI wxAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wxAPI = WXAPIFactory.createWXAPI(this,Constant.WECHAT_APPID,true);
        wxAPI.registerApp(Constant.WECHAT_APPID);
        wxAPI.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        wxAPI.handleIntent(getIntent(),this);
        Log.i("ansen","WXEntryActivity onNewIntent");
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.i("ansen","WXEntryActivity onReq:"+arg0);
    }

    @Override
    public void onResp(BaseResp resp){
        if(resp.getType()== ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX){//分享
            Log.i("ansen","微信分享操作.....");
            WeiXin weiXin=new WeiXin(2,resp.errCode,"");
            EventBus.getDefault().post(weiXin);
        }else if(resp.getType()==ConstantsAPI.COMMAND_SENDAUTH){//登陆
            Log.i("ansen", "微信登录操作.....");
            SendAuth.Resp authResp = (SendAuth.Resp) resp;
            WeiXin weiXin=new WeiXin(1,resp.errCode,authResp.code);
            EventBus.getDefault().post(weiXin);
        }
        finish();
    }
}
