package io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android;

import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 继承Android 500 版本是因为大部分逻辑和id相同，所以就直接继承，然后只重写不同部分
 *
 * @author zhitao
 * @since 2017-04-05 10:47
 */
public class AndroidApkInstallASHandler501 extends AndroidApkInstallASHandler500 {
	
	/**
	 * 执行在apk卸载界面的逻辑
	 */
	@Override
	protected void runLogicInAppUninstallPage() {
		// 三星5.0.1系统上的卸载界面的标题不是app名字，我们需要从正文信息中获取文字判断是否包含我们要卸载的app的名字
		String message = getTextByViewIdFromRootActiveWindow("android:id/message");
		if (message == null) {
			return;
		}
		
		DLog.i("进入应用[%s]的卸载界面", message);
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			if (!message.contains(target.getAppName())) {
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
