package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

/**
 * @author zhitao
 * @since 2017-03-23 16:51
 */
public class OnVpnCallBackAdapter implements OnVpnCallBack {
	
	/**
	 * 开始配置VPN信息时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	@Override
	public void onVpnConfigStart(VpnTarget vpnTarget) {
		
	}
	
	/**
	 * 配置VPN信息结束时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	@Override
	public void onVpnConfigFinish(VpnTarget vpnTarget) {
		
	}
	
	/**
	 * 开始配置用户信息时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	@Override
	public void onUserConfigStart(VpnTarget vpnTarget) {
		
	}
	
	/**
	 * 配置用户信息结束时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	@Override
	public void onUserConfigFinish(VpnTarget vpnTarget) {
		
	}
}
