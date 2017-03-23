package io.github.zhitaocai.accessibilitydispatcher.demo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zhitaocai.accessibilitydispatcher.AccessibilityDispatcher;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBackAdapter;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;

public class MainActivity extends AppCompatActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		
		// 开启调试log
		AccessibilityDispatcher.debugLog(true).withEventSourceLog(true);
	}
	
	@OnClick(R.id.btn_open_accessibility)
	protected void openAccessibility() {
		startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
	}
	
	@OnClick(R.id.btn_open_vpn_settings)
	protected void openVpnSettings() {
		
		// 在打开VPN界面之前，设置我们需要自动操作的内容
		//
		// e.g.
		//
		// 需要自动创建两个VPN配置并且输入用户信息，其中，
		//
		// VPN1 为新建VPN配置，如果存在在不处理
		// VPN2 为新建或者更新VPN配置，如果之前已经存在这个VPN配置的话，就更新它的VPN配置和用户信息
		VpnHelper.getInstance().addTarget(
				new VpnTarget.Builder().setAction(VpnTarget.ACTION_CREATE_VPN_CONFIG | VpnTarget.ACTION_CREATE_USER_CONFIG)
				                       .setVpnName("VPN1")
				                       .setVpnType(VpnTarget.VpnType.PPTP)
				                       .setVpnServerAddr("1.1.1.1")
				                       .setPPPEncryption(true)
				                       .setDnsSearchDomain("8.8.8.8")
				                       .setUserName("username1")
				                       .setPassword("123456")
				                       .setSaveAccountInfo(true)
				                       .build(),
				new VpnTarget.Builder().setAction(VpnTarget.ACTION_CREATE_VPN_CONFIG | VpnTarget.ACTION_UPDATE_VPN_CONFIG |
				                                  VpnTarget.ACTION_CREATE_USER_CONFIG | VpnTarget.ACTION_UPDATE_USER_CONFIG)
				                       .setVpnName("VPN2")
				                       .setVpnType(VpnTarget.VpnType.PPTP)
				                       .setVpnServerAddr("2.2.2.2")
				                       .setPPPEncryption(true)
				                       .setDnsSearchDomain("8.8.8.8")
				                       .setUserName("username2-" + System.currentTimeMillis())
				                       .setPassword("123456")
				                       .setSaveAccountInfo(true)
				                       .build()
		).addCallBack(new OnVpnCallBackAdapter() {
			/**
			 * 开始配置VPN信息时回调
			 *
			 * @param vpnConfig 当前在配置的vpn信息
			 */
			@Override
			public void onVpnConfigStart(VpnTarget.VpnConfig vpnConfig) {
				Toast.makeText(MainActivity.this, "开始配置VPN" + System.currentTimeMillis(), Toast.LENGTH_SHORT).show();
				VpnHelper.getInstance().removeCallBack(this).active();
			}
		}).setEnable(true).active();
		
		Intent intent;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
			intent = new Intent(Settings.ACTION_VPN_SETTINGS);
		} else {
			intent = new Intent();
			intent.setAction("android.net.vpn.SETTINGS");
		}
		startActivity(intent);
	}
	
}
