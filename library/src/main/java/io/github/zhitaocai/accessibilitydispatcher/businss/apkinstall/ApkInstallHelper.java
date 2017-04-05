package io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall;

import android.content.Intent;
import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;

/**
 * @author zhitao
 * @since 2017-04-01 09:34
 */
public class ApkInstallHelper extends AbsHelper<ApkInstallTarget, OnApkInstallCallBack, ApkInstallHandlerFactory> {
	
	private static ApkInstallHelper sInstance;
	
	private ApkInstallHelper() {
		super();
		initHandlerFactory(new ApkInstallHandlerFactory());
	}
	
	public static ApkInstallHelper getInstance() {
		if (sInstance == null) {
			sInstance = new ApkInstallHelper();
		}
		return sInstance;
	}
	
	/**
	 * 业务唯一标识
	 *
	 * @return 业务唯一表示，分发器依据此来识别不同的辅助业务
	 */
	@NonNull
	@Override
	public String getIdentify() {
		return Intent.ACTION_INSTALL_PACKAGE;
	}
}
