package io.github.zhitaocai.accessibilitydispatcher.androidsettings.vpn.android;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.androidsettings.vpn.AbsVpnSettingsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;

/**
 * 原生系统4.4.4配置VPN大概就差不多4个页面
 *
 * @author zhitao
 * @since 2017-03-23 17:38
 */
abstract class AbsAndroidVpnSettingsASHandler extends AbsVpnSettingsASHandler {
	
	/**
	 * 是否在VPN列表页
	 *
	 * @return
	 */
	protected abstract boolean isInVpnListPage();
	
	/**
	 * 执行在VPN列表页的逻辑
	 */
	protected abstract void runLogicInVpnListPage();
	
	/**
	 * 是否在VPN配置的对话框中
	 *
	 * @return
	 */
	protected abstract boolean isInVpnConfigDialog();
	
	/**
	 * 执行在VPN配置的对话框中的逻辑
	 *
	 * @return
	 */
	protected abstract void runLogicInVpnConfigDialog();
	
	/**
	 * 是否在用户配置的对话框中
	 *
	 * @return
	 */
	protected abstract boolean isInUserConfigDialog();
	
	/**
	 * 执行在用户配置的对话框中的逻辑
	 */
	protected abstract void runLogicInUserConfigDialog();
	
	/**
	 * 是否在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中
	 *
	 * @return
	 */
	protected abstract boolean isInVpnProfileEditDialog();
	
	/**
	 * 执行在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中的逻辑
	 *
	 * @return
	 */
	protected abstract boolean runLogicInVpnProfileEditDialog();
	
	/**
	 * 处理在VPN列表页时的逻辑
	 */
	protected void handleInVpnListPage() {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				if (isInVpnListPage()) {
					runLogicInVpnListPage();
				}
			}
		});
	}
	
	/**
	 * 处理在VPN配置对话框的逻辑
	 */
	protected void handleInVpnConfigDialog() {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				if (isInVpnConfigDialog()) {
					runLogicInVpnConfigDialog();
				}
			}
		});
	}
	
	/**
	 * 处理在用户配置对话框的逻辑
	 */
	protected void handleInUserConfigDialog() {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				if (isInUserConfigDialog()) {
					runLogicInUserConfigDialog();
				}
			}
		});
	}
	
	/**
	 * 处理在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中的逻辑
	 */
	protected void handleInVpnProfileEditDialog() {
		getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				if (isInVpnProfileEditDialog()) {
					runLogicInVpnProfileEditDialog();
				}
			}
		});
	}
	
	protected void callBackEnterVpnConfigDialog(final VpnTarget.VpnConfig vpnConfig) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onVpnConfigStart(vpnConfig);
				}
			});
		}
	}
	
	protected void callBackExitVpnConfigDialog(final VpnTarget.VpnConfig vpnConfig) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onVpnConfigFinish(vpnConfig);
				}
			});
		}
	}
	
	protected void callBackEnterUserConfigDialog(final VpnTarget.UserConfig userConfig) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onUserConfigStart(userConfig);
				}
			});
		}
	}
	
	protected void callBackExitUserConfigDialog(final VpnTarget.UserConfig userConfig) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onUserConfigFinish(userConfig);
				}
			});
		}
	}
	
}
