package io.github.zhitaocai.accessibilitydispatcher.demo.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBackAdapter;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;
import io.github.zhitaocai.accessibilitydispatcher.demo.R;

/**
 * @author zhitao
 * @since 2017-03-30 14:17
 */
public class AutoVpnConfigFragment extends BaseFragment {
	
	private final static int REQ_AUTO_CONFIG_VPN = 100;
	
	private Unbinder mUnBinder;
	
	@Override
	public String getFragmentTitle() {
		return "VPN配置";
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_config_vpn, container, false);
		mUnBinder = ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mUnBinder.unbind();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_AUTO_CONFIG_VPN:
			
			// 不管配置是否成功，建议都取消自动配置，这样子，如果用户自己从设置中打开VPN配置的话，我们的自动点击程序就不会影响到用户的操作了
			//if (resultCode == RESULT_OK) { }
			VpnHelper.getInstance().reset().active();
			break;
		default:
			break;
		}
	}
	
	@OnClick(R.id.btn_create_l2tp)
	protected void createL2TP() {
		
		// 在打开VPN界面之前，设置我们需要自动操作的内容
		VpnHelper.getInstance()
		
		         // 创建一个新的或者更新现有的一个L2TP连接
		         .setTarget(new VpnTarget.Builder().setVpnName("L2TP VPN Demo")
		                                           .setAction(VpnTarget.ACTION_CREATE_VPN_CONFIG |
		                                                      VpnTarget.ACTION_UPDATE_VPN_CONFIG |
		                                                      VpnTarget.ACTION_INPUT_USER_CONFIG)
		                                           .setVpnType(VpnTarget.VpnType.L2TP_IPSec_PSK)
		                                           .setVpnServerAddr("1.2.3.4")
		                                           .setL2TPSecret("L2TPSecret-test")
		                                           .setIPSecIdentifier("IPSecIdentifier-test")
		                                           .setIPSecPreSharedKey("IPSecPreSharedKey-test")
		                                           .setDnsSearchDomain("8.8.8.8")
		                                           .setDnsServers("8.8.8.8")
		                                           .setForwardingRoutes("10.0.0.0/8")
		                                           .setUserName("username")
		                                           .setPassword("password")
		                                           .setSaveAccountInfo(true)
		                                           .build())
		
		         // 设置VPN回调过程中的监听Adapter(相比起listener，adapter可以实现你感兴趣的回调而不用全部实现所有接口)
		         .setCallBack(new OnVpnCallBackAdapter() {
			         /**
			          * 开始配置VPN信息时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onVpnConfigStart(VpnTarget vpnTarget) {
				         toast("%1$tH:%1$tM:%1$tS 进入VPN配置，当前配置\n%s", System.currentTimeMillis(), vpnTarget.toString());
			         }
		         })
		
		         // 设置是否自动操作
		         .setEnable(true)
		
		         // 将本次设置的内容加入到辅助功能服务，不然你只是在瞎逼逼
		         .active();
		
		startActivity2VpnSettings();
	}
	
	@OnClick(R.id.btn_create_pptp)
	protected void createPPTP() {
		
		// 在打开VPN界面之前，设置我们需要自动操作的内容
		VpnHelper.getInstance()
		
		         // 创建一个新的或者更新现有的一个PPTP连接
		         .setTarget(new VpnTarget.Builder().setVpnName("PPTP VPN Demo")
		                                           .setAction(VpnTarget.ACTION_CREATE_VPN_CONFIG |
		                                                      VpnTarget.ACTION_UPDATE_VPN_CONFIG |
		                                                      VpnTarget.ACTION_INPUT_USER_CONFIG)
		                                           .setVpnType(VpnTarget.VpnType.PPTP)
		                                           .setVpnServerAddr("1.2.3.4")
		                                           .setDnsSearchDomain("8.8.8.8")
		                                           .setDnsServers("8.8.8.8")
		                                           .setForwardingRoutes("10.0.0.0/8")
		                                           .setUserName("username")
		                                           .setPassword("password")
		                                           .setSaveAccountInfo(true)
		                                           .build())
		         // 设置VPN回调过程中的监听Listener
		         .setCallBack(new OnVpnCallBack() {
			         /**
			          * 开始配置VPN信息时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onVpnConfigStart(VpnTarget vpnTarget) {
				         toast("%1$tH:%1$tM:%1$tS 进入VPN配置", System.currentTimeMillis());
			         }
			
			         /**
			          * 配置VPN信息结束时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onVpnConfigFinish(VpnTarget vpnTarget) {
				         toast("%1$tH:%1$tM:%1$tS 完成VPN配置", System.currentTimeMillis());
			         }
			
			         /**
			          * 开始配置用户信息时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onUserConfigStart(VpnTarget vpnTarget) {
				         toast("%1$tH:%1$tM:%1$tS 进入用户配置", System.currentTimeMillis());
			         }
			
			         /**
			          * 配置用户信息结束时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onUserConfigFinish(VpnTarget vpnTarget) {
				         toast("%1$tH:%1$tM:%1$tS 完成用户配置", System.currentTimeMillis());
			         }
		         })
		
		         // 设置是否自动操作
		         .setEnable(true)
		
		         // 将本次设置的内容加入到辅助功能服务，不然你只是在瞎逼逼
		         .active();
		
		startActivity2VpnSettings();
	}
	
	private void startActivity2VpnSettings() {
		Intent intent;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
			intent = new Intent(Settings.ACTION_VPN_SETTINGS);
		} else {
			intent = new Intent();
			intent.setAction("android.net.vpn.SETTINGS");
		}
		startActivityForResult(intent, REQ_AUTO_CONFIG_VPN);
	}
}
