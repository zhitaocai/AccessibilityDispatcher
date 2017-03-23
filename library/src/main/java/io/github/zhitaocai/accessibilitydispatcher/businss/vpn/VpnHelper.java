package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;

/**
 * @author zhitao
 * @since 2017-03-23 11:10
 */
public final class VpnHelper extends AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> {
	
	private static VpnHelper sVpnHelper;
	
	protected VpnHelper() {
		super();
		initHandlerFactory(new VpnHandlerFactory());
	}
	
	public static VpnHelper getInstance() {
		if (sVpnHelper == null) {
			sVpnHelper = new VpnHelper();
		}
		return sVpnHelper;
	}
	
	/**
	 * 业务唯一标识
	 *
	 * @return
	 */
	@NonNull
	@Override
	protected String getIdentify() {
		return "android.net.vpn.SETTINGS";
	}
	
}