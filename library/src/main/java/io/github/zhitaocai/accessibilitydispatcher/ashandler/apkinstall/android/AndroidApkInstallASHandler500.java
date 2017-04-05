package io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android;

import android.view.accessibility.AccessibilityEvent;

import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 继承Android 444 版本是因为大部分逻辑和id相同，所以就直接继承，然后只重写不同部分
 *
 * @author zhitao
 * @since 2017-04-05 10:41
 */
public class AndroidApkInstallASHandler500 extends AndroidApkInstallASHandler444 {
	
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
			case "android.app.AlertDialog":
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
	
	/**
	 * @return 是否在apk卸载界面
	 */
	@Override
	protected boolean isInAppUninstallPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"android:id/content",
				"android:id/parentPanel",
				"android:id/topPanel",
				"android:id/title_template",
				"android:id/alertTitle",
				"android:id/contentPanel",
				"android:id/scrollView",
				"android:id/message",
				"android:id/buttonPanel",
				"android:id/button2",
				"android:id/button1"
		);
	}
	
	/**
	 * 执行在apk卸载界面的逻辑
	 */
	@Override
	protected void runLogicInAppUninstallPage() {
		String appName = getTextByViewIdFromRootActiveWindow("android:id/alertTitle");
		if (appName == null) {
			return;
		}
		DLog.i("进入应用[%s]的卸载界面", appName);
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			if (!target.getAppName().equals(appName)) {
				continue;
			}
			
			if ((target.getAction() & ApkInstallTarget.ACTION_AUTO_DELETE) != 0) {
				if (performClickByViewIdFromRootActiveWindow("android:id/button1")) {
					callbackAppUninstallConfirm(target);
				}
			} else if ((target.getAction() & ApkInstallTarget.ACTION_CAN_NOT_DELETE) != 0) {
				if (performClickByViewIdFromRootActiveWindow("android:id/button2")) {
					callbackAppUninstallCancel(target);
				}
			}
			break;
		}
	}
}