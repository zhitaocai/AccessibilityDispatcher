package io.github.zhitaocai.accessibilitydispatcher.apkinstall;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.OnApkInstallCallBack;

/**
 * @author zhitao
 * @since 2017-04-01 09:58
 */
public abstract class AbsApkInstallHandler extends AbsASHandler<ApkInstallTarget, OnApkInstallCallBack> {
	
	/**
	 * @return 是否在apk安装界面
	 */
	protected abstract boolean isInApkInstallPage();
	
	/**
	 * 执行在apk安装界面的逻辑
	 */
	protected abstract void runLogicInApkInstallPage();
	
	/**
	 * @return 是否在apk安装中界面
	 */
	protected abstract boolean isInApkInstallingPage();
	
	/**
	 * 执行在apk安装中界面的逻辑
	 */
	protected abstract void runLogicInApkInstallingPage();
	
	/**
	 * @return 是否在apk安装成功界面
	 */
	protected abstract boolean isInApkInstallSuccessPage();
	
	/**
	 * 执行在apk安装成功界面的逻辑
	 */
	protected abstract void runLogicInApkInstallSuccessPage();
	
	/**
	 * @return 是否在apk卸载界面
	 */
	protected abstract boolean isInAppUninstallPage();
	
	/**
	 * 执行在apk卸载界面的逻辑
	 */
	protected abstract void runLogicInAppUninstallPage();
	
	protected void handleApkInstall() {
		if (isInApkInstallPage()) {
			runLogicInApkInstallPage();
		}
	}
	
	protected void handleApkInstalling() {
		if (isInApkInstallingPage()) {
			runLogicInApkInstallingPage();
		}
	}
	
	protected void handleApkInstallSuccess() {
		if (isInApkInstallSuccessPage()) {
			runLogicInApkInstallSuccessPage();
		}
	}
	
	protected void handleAppUninstall() {
		if (isInAppUninstallPage()) {
			runLogicInAppUninstallPage();
		}
	}
	
	protected void callbackApkInstallBtnClick(final ApkInstallTarget target) {
		List<OnApkInstallCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnApkInstallCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onApkInstallBtnClick(target);
				}
			});
		}
	}
	
	protected void callbackApkInstalling(final ApkInstallTarget target) {
		List<OnApkInstallCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnApkInstallCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onApkInstalling(target);
				}
			});
		}
	}
	
	protected void callbackApkInstallFinish(final ApkInstallTarget target) {
		List<OnApkInstallCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnApkInstallCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onApkInstallFinish(target);
				}
			});
		}
	}
	
	protected void callbackApkInstallLaunch(final ApkInstallTarget target) {
		List<OnApkInstallCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnApkInstallCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onApkInstallLaunch(target);
				}
			});
		}
	}
	
	protected void callbackAppUninstallConfirm(final ApkInstallTarget target) {
		List<OnApkInstallCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnApkInstallCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onApkUninstallConfirm(target);
				}
			});
		}
	}
	
	protected void callbackAppUninstallCancel(final ApkInstallTarget target) {
		List<OnApkInstallCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnApkInstallCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onApkUninstallCancel(target);
				}
			});
		}
	}
	
}
