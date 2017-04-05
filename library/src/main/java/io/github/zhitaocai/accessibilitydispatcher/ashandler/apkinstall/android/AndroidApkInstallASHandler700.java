package io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android;

import android.view.accessibility.AccessibilityEvent;

/**
 * @author zhitao
 * @since 2017-04-05 11:00
 */
public class AndroidApkInstallASHandler700 extends AndroidApkInstallASHandler500 {
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			handleApkInstallSuccess();
			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			switch (event.getClassName().toString()) {
			// 安装界面
			case "com.android.packageinstaller.PackageInstallerActivity":
				handleApkInstall();
				break;
			// 安装中界面，同时也是安装成功的界面，但是安装成功的界面不是这个事件触发的
			case "com.android.packageinstaller.InstallAppProgress":
				handleApkInstalling();
				break;
			// 卸载界面
			case "com.android.packageinstaller.UninstallerActivity":
				handleAppUninstall();
				break;
			default:
				break;
			}
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			switch (event.getClassName().toString()) {
			case "android.widget.ScrollView":
				// 安装界面点击下一步之后会触发这里，要不断点击，直到开始安装
				handleApkInstall();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
	
}
