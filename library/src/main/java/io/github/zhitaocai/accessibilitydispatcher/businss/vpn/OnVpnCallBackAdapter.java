package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

/**
 * @author zhitao
 * @since 2017-03-23 16:51
 */
public class OnVpnCallBackAdapter implements OnVpnCallBack {
	
	/**
	 * 开始配置VPN信息时回调
	 *
	 * @param vpnConfig 当前在配置的vpn信息
	 */
	@Override
	public void onVpnConfigStart(VpnTarget.VpnConfig vpnConfig) {
		
	}
	
	/**
	 * 配置VPN信息结束时回调
	 *
	 * @param vpnConfig 当前在配置的vpn信息
	 */
	@Override
	public void onVpnConfigFinish(VpnTarget.VpnConfig vpnConfig) {
		
	}
	
	/**
	 * 开始配置用户信息时回调
	 *
	 * @param userConfig 当前在配置的vpn信息
	 */
	@Override
	public void onUserConfigStart(VpnTarget.UserConfig userConfig) {
		
	}
	
	/**
	 * 配置用户信息结束时回调
	 *
	 * @param userConfig 当前在配置的vpn信息
	 */
	@Override
	public void onUserConfigFinish(VpnTarget.UserConfig userConfig) {
		
	}
}
