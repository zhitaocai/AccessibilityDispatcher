package io.github.zhitaocai.accessibilitydispatcher.androidsettings.vpn.android;

import android.view.accessibility.AccessibilityEvent;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.androidsettings.AndroidSettingsCompat;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;

/**
 * @author zhitao
 * @since 2017-03-23 10:58
 */
public class AndroidVpnSettingsASHandler extends AbsAndroidVpnSettingsASHandler {
	
	/**
	 * 具体实现类的辅助功能所针对的应用
	 *
	 * @return
	 */
	@Override
	public String getSupportPkgName() {
		return AndroidSettingsCompat.ANDROID_PKGNAME;
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
			handleInVpnListPage();
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			switch (event.getClassName().toString()) {
			case "com.android.settings.SubSettings":
			case "com.android.settings.Settings$VpnSettingsActivity":
				handleInVpnListPage();
				//handleScrollListPage();
				break;
			case "android.app.AlertDialog":
				handleInVpnConfigDialog();
				handleInUserConfigDialog();
				handleInVpnProfileEditDialog();
				break;
			default:
				break;
			}
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			switch (event.getClassName().toString()) {
			case "android.widget.ScrollView":
				handleInVpnListPage();
				//handleScrollListPage();
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
	 * 是否在VPN列表页
	 *
	 * @return
	 */
	@Override
	protected boolean isInVpnListPage() {
		return true;
	}
	
	/**
	 * 执行在VPN列表页的逻辑
	 */
	@Override
	protected void runLogicInVpnListPage() {
		List<VpnTarget> targets = getTargets();
		if (targets == null || targets.isEmpty()) {
			return;
		}
		callBackEnterVpnConfigDialog(targets.get(0).getVpnConfig());
	}
	
	/**
	 * 是否在VPN配置的对话框中
	 *
	 * @return
	 */
	@Override
	protected boolean isInVpnConfigDialog() {
		return false;
	}
	
	/**
	 * 执行在VPN配置的对话框中的逻辑
	 *
	 * @return
	 */
	@Override
	protected void runLogicInVpnConfigDialog() {
		
	}
	
	/**
	 * 是否在用户配置的对话框中
	 *
	 * @return
	 */
	@Override
	protected boolean isInUserConfigDialog() {
		return false;
	}
	
	/**
	 * 执行在用户配置的对话框中的逻辑
	 */
	@Override
	protected void runLogicInUserConfigDialog() {
		
	}
	
	/**
	 * 是否在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中
	 *
	 * @return
	 */
	@Override
	protected boolean isInVpnProfileEditDialog() {
		return false;
	}
	
	/**
	 * 执行在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中的逻辑
	 *
	 * @return
	 */
	@Override
	protected boolean runLogicInVpnProfileEditDialog() {
		return false;
	}
}
