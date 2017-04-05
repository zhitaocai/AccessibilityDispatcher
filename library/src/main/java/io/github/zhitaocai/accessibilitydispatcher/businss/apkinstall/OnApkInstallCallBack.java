package io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall;

import io.github.zhitaocai.accessibilitydispatcher.businss.OnCallBack;

/**
 * @author zhitao
 * @since 2017-04-01 09:46
 */
public interface OnApkInstallCallBack extends OnCallBack {
	
	/**
	 * 点击了apk安装界面中的"下一步"或者"安装"按钮
	 *
	 * @param target 目标应用
	 */
	void onApkInstallBtnClick(ApkInstallTarget target);
	
	/**
	 * 进入安装中的界面
	 *
	 * @param target 目标应用
	 */
	void onApkInstalling(ApkInstallTarget target);
	
	/**
	 * 点击了apk安装成功界面中的"完成"按钮
	 *
	 * @param target 目标应用
	 */
	void onApkInstallFinish(ApkInstallTarget target);
	
	/**
	 * 点击了apk安装成功界面中的"打开"按钮
	 *
	 * @param target 目标应用
	 */
	void onApkInstallLaunch(ApkInstallTarget target);
	
	/**
	 * 点击了apk卸载界面中"确认"按钮
	 *
	 * @param target 目标应用
	 */
	void onApkUninstallConfirm(ApkInstallTarget target);
	
	/**
	 * 点击了apk卸载界面中"取消"按钮
	 *
	 * @param target 目标应用
	 */
	void onApkUninstallCancel(ApkInstallTarget target);
	
}
