package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;

/**
 * @author zhitao
 * @since 2017-03-23 11:10
 */
public final class VpnHelper extends AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> {
	
	/**
	 * 业务唯一标识
	 *
	 * @return 业务唯一表示，分发器依据此来识别不同的辅助业务
	 */
	@NonNull
	@Override
	public String getIdentify() {
		return "android.net.vpn.SETTINGS";
	}
	
	/**
	 * 获取默认的业务对象工厂
	 *
	 * @return 如果没有调用 {@link #withHandlerFactory(IHandlerFactory)} 方法设置工厂的话，那么就会调用这个方法来设置
	 *
	 * @see #withHandlerFactory(IHandlerFactory)
	 */
	@Override
	protected VpnHandlerFactory newDefaultHandlerFactory() {
		return new VpnHandlerFactory();
	}
	
}