package io.github.zhitaocai.accessibilitydispatcher.androidsettings.vpn.android;

import java.util.ArrayList;
import java.util.HashMap;
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
	 */
	protected abstract void runLogicInVpnConfigDialog();
	
	/**
	 * 是否在VPN配置的对话框中选择VPN类型的Spinner中
	 *
	 * @return
	 */
	protected abstract boolean isInVpnConfigDialogSpinnerWindow();
	
	/**
	 * 执行在VPN配置的对话框中选择VPN类型的Spinner中的逻辑
	 */
	protected abstract void runLogicInVpnConfigDialogSpinnerWindow();
	
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
	 */
	protected abstract void runLogicInVpnProfileEditDialog();
	
	/**
	 * 滑动列表页的VPN的ListView
	 */
	protected abstract void scrollListViewInVpnListPage();
	
	/**
	 * 滑动VPN列表页的ListView，寻找是否已经存在指定的VPN
	 */
	protected void handleScrollListPage() {
		if (isInVpnListPage()) {
			scrollListViewInVpnListPage();
		}
	}
	
	/**
	 * 处理在VPN列表页时的逻辑
	 */
	protected void handleInVpnListPage() {
		if (isInVpnListPage()) {
			runLogicInVpnListPage();
		}
	}
	
	/**
	 * 处理在VPN配置对话框的逻辑
	 */
	protected void handleInVpnConfigDialog() {
		if (isInVpnConfigDialog()) {
			runLogicInVpnConfigDialog();
		}
	}
	
	/**
	 * 处理在VPN配置的对话框中选择VPN类型的Spinner中的逻辑
	 */
	protected void handleInVpnConfigDialogSpinnerWindow() {
		if (isInVpnConfigDialogSpinnerWindow()) {
			runLogicInVpnConfigDialogSpinnerWindow();
		}
	}
	
	/**
	 * 处理在用户配置对话框的逻辑
	 */
	protected void handleInUserConfigDialog() {
		if (isInUserConfigDialog()) {
			runLogicInUserConfigDialog();
		}
	}
	
	/**
	 * 处理在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中的逻辑
	 */
	protected void handleInVpnProfileEditDialog() {
		if (isInVpnProfileEditDialog()) {
			runLogicInVpnProfileEditDialog();
		}
	}
	
	protected void callBackEnterVpnConfigDialog(final VpnTarget vpnTarget) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onVpnConfigStart(vpnTarget);
				}
			});
		}
	}
	
	protected void callBackExitVpnConfigDialog(final VpnTarget vpnTarget) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onVpnConfigFinish(vpnTarget);
				}
			});
		}
	}
	
	protected void callBackEnterUserConfigDialog(final VpnTarget vpnTarget) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onUserConfigStart(vpnTarget);
				}
			});
		}
	}
	
	protected void callBackExitUserConfigDialog(final VpnTarget vpnTarget) {
		List<OnVpnCallBack> list = getCallBacks();
		if (list == null || list.isEmpty()) {
			return;
		}
		for (final OnVpnCallBack callBack : list) {
			getUIHandler().post(new Runnable() {
				@Override
				public void run() {
					callBack.onUserConfigFinish(vpnTarget);
				}
			});
		}
	}
	
	/**
	 * 记录目标要配置的VPN信息当前已经完成到什么步骤了
	 */
	private HashMap<VpnTarget, Integer> mVpnTargetCacheHashMap;
	
	/**
	 * 点击了创建VPN的按钮
	 */
	protected final static int STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE = 1;
	
	/**
	 * 点击了选择VPN类型
	 */
	protected final static int STEP_VPN_CONFIG_HAS_CHOOSE_VPN_TYPE = 2;
	
	/**
	 * 在VPN配置窗口中已经完成了文字方面的信息输入
	 */
	protected final static int STEP_VPN_CONFIG_FINISH_INPUT = 4;
	
	/**
	 * 点击了目标VPN进行输入用户配置信息
	 */
	protected final static int STEP_USER_CONFIG_START_INPUT = 8;
	
	/**
	 * 在用户信息配置窗口中已经完成了文字方面的信息输入
	 */
	protected final static int STEP_USER_CONFIG_FINISH_INPUT = 16;
	
	@Override
	protected void setTargets(ArrayList<VpnTarget> targets) {
		super.setTargets(targets);
		mVpnTargetCacheHashMap = null;
		if (targets == null || targets.isEmpty()) {
			return;
		}
		for (VpnTarget vpnTarget : targets) {
			if (vpnTarget == null || !vpnTarget.isValid()) {
				continue;
			}
			if (mVpnTargetCacheHashMap == null) {
				mVpnTargetCacheHashMap = new HashMap<>();
			}
			mVpnTargetCacheHashMap.put(vpnTarget, 0);
		}
	}
	
	/**
	 * 获取缓存VPN目标信息当前进行到什么步骤的Hashmap
	 *
	 * @return
	 */
	protected HashMap<VpnTarget, Integer> getVpnTargetCacheHashMap() {
		return mVpnTargetCacheHashMap;
	}
}
