package io.github.zhitaocai.accessibilitydispatcher.androidsettings.security;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.androidsettings.AndroidSettingsCompat;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.OnSecurityCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;

/**
 * @author zhitao
 * @since 2017-03-30 11:55
 */
public abstract class AbsSecuritySettingsASHandler extends AbsASHandler<SecurityTarget, OnSecurityCallBack> {
	
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
		return AndroidSettingsCompat.ANDROID_PKGNAME;
	}
	
	protected void handleInSecurityPage() {
		if (isInSecurityPage()) {
			runLogicInSecurityPage();
		}
	}
	
	protected void handleInUnknownSourcesTurnOnConfirmDialog() {
		if (isInUnknownSourcesTurnOnConfirmDialog()) {
			runLogicInUnknownSourcesTurnOnConfirmDialog();
		}
	}
	
	protected void handleScrollInSecurityPage() {
		if (isInSecurityPage()) {
			scrollInSecurityPage();
		}
	}
	
	protected void callBackOnUnknownSourceItemClick() {
		List<OnSecurityCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnSecurityCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onUnknownSourceItemClick();
				}
			});
		}
	}
	
	protected void callBackOnUnknownSourceDialogConfirm() {
		List<OnSecurityCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnSecurityCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onUnknownSourceDialogConfirm();
				}
			});
		}
	}
	
	/**
	 * 是否在安全设置页面中
	 *
	 * @return
	 */
	protected abstract boolean isInSecurityPage();
	
	/**
	 * 处理界面在安全设置页面中的逻辑
	 */
	protected abstract void runLogicInSecurityPage();
	
	/**
	 * 目标可能在下面，所以需要先滑动listview
	 */
	protected abstract void scrollInSecurityPage();
	
	/**
	 * 是否在点击允许安装未知来源的开关之后弹出来的确认对话框
	 *
	 * @return
	 */
	protected abstract boolean isInUnknownSourcesTurnOnConfirmDialog();
	
	/**
	 * 处理界面在点击允许安装未知来源的开关之后弹出来的确认对话框页面的逻辑
	 *
	 * @return
	 */
	protected abstract void runLogicInUnknownSourcesTurnOnConfirmDialog();
}
