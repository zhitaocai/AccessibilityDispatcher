package io.github.zhitaocai.accessibilitydispatcher.businss.security;

import android.provider.Settings;
import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;

/**
 * @author zhitao
 * @since 2017-03-30 11:46
 */
public class SecurityHelper extends AbsHelper<SecurityTarget, OnSecurityCallBack, SecurityHandlerFactory> {
	
	/**
	 * 业务唯一标识
	 *
	 * @return 业务唯一表示，分发器依据此来识别不同的辅助业务
	 */
	@NonNull
	@Override
	public String getIdentify() {
		return Settings.ACTION_SECURITY_SETTINGS;
	}
	
	/**
	 * 获取默认的业务对象工厂
	 *
	 * @return 如果没有调用 {@link #withHandlerFactory(IHandlerFactory)} 方法设置工厂的话，那么就会调用这个方法来设置
	 *
	 * @see #withHandlerFactory(IHandlerFactory)
	 */
	@Override
	protected SecurityHandlerFactory newDefaultHandlerFactory() {
		return new SecurityHandlerFactory();
	}
}

