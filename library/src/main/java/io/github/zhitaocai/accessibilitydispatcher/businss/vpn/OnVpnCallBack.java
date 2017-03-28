package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import io.github.zhitaocai.accessibilitydispatcher.businss.OnCallBack;

/**
 * 配置VPN过程中的回调信息
 * <p>
 * TODO 加入超时限制，以及加入是否成功配置完毕的回调参数
 *
 * @author zhitao
 * @since 2017-03-23 11:26
 */
public interface OnVpnCallBack extends OnCallBack {
	
	/**
	 * 开始配置VPN信息时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	void onVpnConfigStart(VpnTarget vpnTarget);
	
	/**
	 * 配置VPN信息结束时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	void onVpnConfigFinish(VpnTarget vpnTarget);
	
	/**
	 * 开始配置用户信息时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	void onUserConfigStart(VpnTarget vpnTarget);
	
	/**
	 * 配置用户信息结束时回调
	 *
	 * @param vpnTarget 当前在配置的VPN
	 */
	void onUserConfigFinish(VpnTarget vpnTarget);
}
