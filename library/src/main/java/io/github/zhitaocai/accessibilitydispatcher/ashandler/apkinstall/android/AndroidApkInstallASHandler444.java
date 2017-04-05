package io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android;

import android.view.accessibility.AccessibilityEvent;

import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.AbsApkInstallHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-04-01 10:35
 */
public class AndroidApkInstallASHandler444 extends AbsApkInstallHandler {
	
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
		return "com.android.packageinstaller";
	}
	
	@Override
	protected void onServiceConnected() {
		
	}
	
	@Override
	protected void onInterrupt() {
		
	}
	
	@Override
	protected void onAccessibilityEvent(AccessibilityEvent event) {
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
	
	/**
	 * @return 是否在apk安装界面
	 */
	@Override
	protected boolean isInApkInstallPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"com.android.packageinstaller:id/app_icon",
				"com.android.packageinstaller:id/app_name",
				"com.android.packageinstaller:id/install_confirm_panel",
				"com.android.packageinstaller:id/cancel_button",
				"com.android.packageinstaller:id/ok_button"
		);
	}
	
	/**
	 * 执行在apk安装界面的逻辑
	 */
	@Override
	protected void runLogicInApkInstallPage() {
		String appName = getTextByViewIdFromRootActiveWindow("com.android.packageinstaller:id/app_name");
		if (appName == null) {
			return;
		}
		
		DLog.i("进入应用[%s]的安装界面", appName);
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			if (!target.getAppName().equals(appName)) {
				continue;
			}
			
			if ((target.getAction() & ApkInstallTarget.ACTION_AUTO_INSTALL) != 0) {
				if (performClickByViewIdFromRootActiveWindow("com.android.packageinstaller:id/ok_button")) {
					callbackApkInstallBtnClick(target);
				}
			}
			break;
		}
	}
	
	/**
	 * @return 是否在apk安装中界面
	 */
	@Override
	protected boolean isInApkInstallingPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"com.android.packageinstaller:id/app_icon",
				"com.android.packageinstaller:id/app_name",
				"com.android.packageinstaller:id/center_text",
				"com.android.packageinstaller:id/progress_bar"
		);
	}
	
	/**
	 * 执行在apk安装中界面的逻辑
	 */
	@Override
	protected void runLogicInApkInstallingPage() {
		String appName = getTextByViewIdFromRootActiveWindow("com.android.packageinstaller:id/app_name");
		if (appName == null) {
			return;
		}
		DLog.i("应用[%s]安装中", appName);
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			if (!target.getAppName().equals(appName)) {
				continue;
			}
			
			if ((target.getAction() & ApkInstallTarget.ACTION_WAIT_INSTALLING) != 0) {
				callbackApkInstalling(target);
			} else {
				goBack();
			}
			break;
		}
	}
	
	/**
	 * @return 是否在apk安装成功界面
	 */
	@Override
	protected boolean isInApkInstallSuccessPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"com.android.packageinstaller:id/app_icon",
				"com.android.packageinstaller:id/app_name",
				"com.android.packageinstaller:id/center_text",
				"com.android.packageinstaller:id/launch_button",
				"com.android.packageinstaller:id/done_button"
		);
	}
	
	/**
	 * 执行在apk安装成功界面的逻辑
	 */
	@Override
	protected void runLogicInApkInstallSuccessPage() {
		String appName = getTextByViewIdFromRootActiveWindow("com.android.packageinstaller:id/app_name");
		if (appName == null) {
			return;
		}
		DLog.i("应用[%s]安装成功", appName);
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			if (!target.getAppName().equals(appName)) {
				continue;
			}
			
			if ((target.getAction() & ApkInstallTarget.ACTION_CLICK_FINISH) != 0) {
				if (performClickByViewIdFromRootActiveWindow("com.android.packageinstaller:id/done_button")) {
					callbackApkInstallFinish(target);
				}
			} else if ((target.getAction() & ApkInstallTarget.ACTION_CLICK_OPEN) != 0) {
				if (performClickByViewIdFromRootActiveWindow("com.android.packageinstaller:id/launch_button")) {
					callbackApkInstallLaunch(target);
				}
			}
			break;
		}
	}
	
	/**
	 * @return 是否在apk卸载界面
	 */
	@Override
	protected boolean isInAppUninstallPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"com.android.packageinstaller:id/app_icon",
				"com.android.packageinstaller:id/app_name",
				"com.android.packageinstaller:id/uninstall_confirm",
				"com.android.packageinstaller:id/ok_button",
				"com.android.packageinstaller:id/cancel_button"
		);
	}
	
	/**
	 * 执行在apk卸载界面的逻辑
	 */
	@Override
	protected void runLogicInAppUninstallPage() {
		String appName = getTextByViewIdFromRootActiveWindow("com.android.packageinstaller:id/app_name");
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
				if (performClickByViewIdFromRootActiveWindow("com.android.packageinstaller:id/ok_button")) {
					callbackAppUninstallConfirm(target);
				}
			} else if ((target.getAction() & ApkInstallTarget.ACTION_CAN_NOT_DELETE) != 0) {
				if (performClickByViewIdFromRootActiveWindow("com.android.packageinstaller:id/cancel_button")) {
					callbackAppUninstallCancel(target);
				}
			}
			break;
		}
	}
	
}
