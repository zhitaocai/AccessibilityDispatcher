package io.github.zhitaocai.accessibilitydispatcher.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zhitaocai.accessibilitydispatcher.AccessibilityDispatcher;
import io.github.zhitaocai.accessibilitydispatcher.AccessibilityServiceUtils;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBackAdapter;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;

public class MainActivity extends AppCompatActivity {
	
	private final static int REQ_ACCESSIBILITY_SERVICE_TURN_ON = 1;
	
	private final static int REQ_AUTO_CONFIG_VPN = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		
		// 开启调试log
		AccessibilityDispatcher.debugLog(true).withEventSourceLog(false).withClassNameInTag(false);
		
		Toast.makeText(this,
				String.format(Locale.getDefault(),
						"辅助功能%s",
						AccessibilityServiceUtils.isAccessibilityServiceOn(this) ? "已经开启" : "还没有开启"
				),
				Toast.LENGTH_SHORT
		).show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_AUTO_CONFIG_VPN:
			
			// 不管配置是否成功，建议都取消自动配置，这样子，如果用户自己从设置中打开VPN配置的话，我们的自动点击程序就不会影响到用户的操作了
			//if (resultCode == RESULT_OK) { }
			VpnHelper.getInstance().reset().active();
			break;
		case REQ_ACCESSIBILITY_SERVICE_TURN_ON:
			Toast.makeText(this,
					String.format(Locale.getDefault(),
							"辅助功能%s",
							AccessibilityServiceUtils.isAccessibilityServiceOn(this) ? "已经开启" : "还没有开启"
					),
					Toast.LENGTH_SHORT
			).show();
			
			break;
		default:
			break;
		}
	}
	
	@OnClick(R.id.btn_open_accessibility)
	protected void openAccessibility() {
		startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQ_ACCESSIBILITY_SERVICE_TURN_ON);
	}
	
	private void startActivity2VPNSettings() {
		Intent intent;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
			intent = new Intent(Settings.ACTION_VPN_SETTINGS);
		} else {
			intent = new Intent();
			intent.setAction("android.net.vpn.SETTINGS");
		}
		startActivityForResult(intent, REQ_AUTO_CONFIG_VPN);
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
				         Toast.makeText(MainActivity.this, String.format(
						         Locale.getDefault(),
						         "%1$tH:%1$tM:%1$tS 进入VPN配置，当前配置\n%s",
						         System.currentTimeMillis(),
						         vpnTarget.toString()
				         ), Toast.LENGTH_SHORT).show();
			         }
		         })
		
		         // 设置是否自动操作
		         .setEnable(true)
		
		         // 将本次设置的内容加入到辅助功能服务，不然你只是在瞎逼逼
		         .active();
		
		startActivity2VPNSettings();
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
				         Toast.makeText(
						         MainActivity.this,
						         String.format(Locale.getDefault(), "%1$tH:%1$tM:%1$tS 进入VPN配置", System.currentTimeMillis()),
						         Toast.LENGTH_SHORT
				         ).show();
			         }
			
			         /**
			          * 配置VPN信息结束时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onVpnConfigFinish(VpnTarget vpnTarget) {
				         Toast.makeText(
						         MainActivity.this,
						         String.format(Locale.getDefault(), "%1$tH:%1$tM:%1$tS 完成VPN配置", System.currentTimeMillis()),
						         Toast.LENGTH_SHORT
				         ).show();
			         }
			
			         /**
			          * 开始配置用户信息时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onUserConfigStart(VpnTarget vpnTarget) {
				         Toast.makeText(
						         MainActivity.this,
						         String.format(Locale.getDefault(), "%1$tH:%1$tM:%1$tS 进入用户配置", System.currentTimeMillis()),
						         Toast.LENGTH_SHORT
				         ).show();
			         }
			
			         /**
			          * 配置用户信息结束时回调
			          *
			          * @param vpnTarget 当前在配置的VPN
			          */
			         @Override
			         public void onUserConfigFinish(VpnTarget vpnTarget) {
				         Toast.makeText(
						         MainActivity.this,
						         String.format(Locale.getDefault(), "%1$tH:%1$tM:%1$tS 完成用户配置", System.currentTimeMillis()),
						         Toast.LENGTH_SHORT
				         ).show();
			         }
		         })
		
		         // 设置是否自动操作
		         .setEnable(true)
		
		         // 将本次设置的内容加入到辅助功能服务，不然你只是在瞎逼逼
		         .active();
		
		startActivity2VPNSettings();
	}
	
}

