//package io.github.zhitaocai.accessibilitydispatcher.apkinstall.samsung;
//
//import android.view.accessibility.AccessibilityEvent;
//
//import io.github.zhitaocai.accessibilitydispatcher.apkinstall.AbsApkInstallHandler;
//
//import static android.content.Intent.ACTION_DELETE;
//import static io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget.ACTION_CAN_NOT_DELETE;
//
///**
// * @author zhitao
// * @since 2017-04-01 09:58
// */
//public class SamsungApkInstallASHandler500 extends AbsApkInstallHandler{
//
//	/**
//	 * 具体实现类的辅助功能所针对的应用包名
//	 * <p>
//	 * e.g.
//	 * <p>
//	 * 如果需要改动系统设置，那么这里的包名可能为 com.android.settings 或者 其他第三方系统所对应的包名
//	 *
//	 * @return 具体实现类的辅助功能所针对的应用包名
//	 *
//	 * @see #isUsingPkgName2TrackEvent()
//	 */
//	@Override
//	protected String getSupportPkgName() {
//		return "com.sec.android.app.launcher";
//	}
//
//	@Override
//	protected void onServiceConnected() {
//
//	}
//
//	@Override
//	protected void onInterrupt() {
//
//	}
//
//	@Override
//	protected void onAccessibilityEvent(AccessibilityEvent event) {
//		switch (event.getEventType()) {
//		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
//			switch (event.getClassName().toString()) {
//			// 卸载界面
//			case "android.app.AlertDialog":
//				handleAppUninstall();
//				break;
//			default:
//				break;
//			}
//			break;
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * @return 是否在apk安装界面
//	 */
//	@Override
//	protected boolean isInApkInstallPage() {
//		return false;
//	}
//
//	/**
//	 * 执行在apk安装界面的逻辑
//	 */
//	@Override
//	protected void runLogicInApkInstallPage() {
//
//	}
//
//	/**
//	 * @return 是否在apk安装中界面
//	 */
//	@Override
//	protected boolean isInApkInstallingPage() {
//		return false;
//	}
//
//	/**
//	 * 执行在apk安装中界面的逻辑
//	 */
//	@Override
//	protected void runLogicInApkInstallingPage() {
//
//	}
//
//	/**
//	 * @return 是否在apk安装成功界面
//	 */
//	@Override
//	protected boolean isInApkInstallSuccessPage() {
//		return false;
//	}
//
//	/**
//	 * 执行在apk安装成功界面的逻辑
//	 */
//	@Override
//	protected void runLogicInApkInstallSuccessPage() {
//
//	}
//
//	/**
//	 * @return 是否在apk卸载界面
//	 */
//	@Override
//	protected boolean isInAppUninstallPage() {
//		return isNodeExistInRootActiveWindowByViewIds(
//				"android:id/content",
//				"android:id/parentPanel",
//				"android:id/topPanel",
//				"android:id/title_template",
//				"android:id/alertTitle",
//				"android:id/contentPanel",
//				"android:id/scrollView",
//				"android:id/message",
//				"android:id/buttonPanel",
//				"android:id/button2",
//				"android:id/button1"
//		);
//	}
//
//	/**
//	 * 执行在apk卸载界面的逻辑
//	 */
//	@Override
//	protected void runLogicInAppUninstallPage() {
//		// 三星5.0.1系统上的卸载界面的标题不是app名字
//		String message = getTextByViewIdFromRootActiveWindow("android:id/message");
//		if (message == null) {
//			return;
//		}
//		if (mApkInstallTargetApps == null) {
//			return;
//		}
//		for (ApkInstallTargetApp app : mApkInstallTargetApps) {
//			if (message.contains(app.appName)) {
//				if ((app.action & ACTION_DELETE) != 0) {
//					if (performClickByViewIdFromRootActiveWindow("android:id/button1")) {
//						callbackAppUninstallConfirm(app.appName);
//					}
//					continue;
//				}
//
//				if ((app.action & ACTION_CAN_NOT_DELETE) != 0) {
//					if (performClickByViewIdFromRootActiveWindow("android:id/button2")) {
//						callbackAppUninstallCancel(app.appName);
//					}
//					continue;
//				}
//			}
//		}
//	}
//}
