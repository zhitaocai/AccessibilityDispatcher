package io.github.zhitaocai.accessibilitydispatcher.businss.security;

import android.provider.Settings;
import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;

/**
 * @author zhitao
 * @since 2017-03-30 11:46
 */
public class SecurityHelper extends AbsHelper<SecurityTarget, OnSeurityCallBack, SecurityHandlerFactory> {
	
	private static SecurityHelper sSecurityHelper;
	
	private SecurityHelper() {
		super();
		initHandlerFactory(new SecurityHandlerFactory());
	}
	
	public static SecurityHelper getInstance() {
		if (sSecurityHelper == null) {
			sSecurityHelper = new SecurityHelper();
		}
		return sSecurityHelper;
	}
	
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
}

