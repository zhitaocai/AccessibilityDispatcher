package io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android;

/**
 * @author zhitao
 * @since 2017-04-05 10:56
 */
public class AndroidApkInstallASHandler601 extends AndroidApkInstallASHandler500 {
	
	/**
	 * 具体实现类的辅助功能所针对的应用包名
	 * <p>
	 * e.g.
	 * <p>
	 * 如果需要改动系统设置，那么这里的包名可能为 com.android.settings 或者 其他第三方系统所对应的包名
	 *
	 * @return 具体实现类的辅助功能所针对的应用包名
	 *
	 * @see #isUsingPkgName2TrackEvent()
	 */
	@Override
	protected String getSupportPkgName() {
		return "com.google.android.packageinstaller";
	}
	
	/**
	 * @return 是否在apk安装界面
	 */
	@Override
	protected boolean isInApkInstallPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"com.android.packageinstaller:id/app_icon",
				"com.android.packageinstaller:id/app_name",
				"com.android.packageinstaller:id/cancel_button",
				"com.android.packageinstaller:id/ok_button"
		);
	}
}
