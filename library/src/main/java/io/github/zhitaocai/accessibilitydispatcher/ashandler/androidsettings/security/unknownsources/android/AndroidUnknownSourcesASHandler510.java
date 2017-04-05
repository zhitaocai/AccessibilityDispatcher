package io.github.zhitaocai.accessibilitydispatcher.ashandler.androidsettings.security.unknownsources.android;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.R;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.androidsettings.security.AbsSecuritySettingsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 支持原生Android 5.1.0系统
 *
 * @author zhitao
 * @since 2017-03-30 15:23
 */
public class AndroidUnknownSourcesASHandler510 extends AbsSecuritySettingsASHandler {
	
	@Override
	protected void onServiceConnected() {
		
	}
	
	@Override
	protected void onInterrupt() {
		
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			handleInSecurityPage();
			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			switch (event.getClassName().toString()) {
			// 通过Intent方式进入安全设置界面
			case "com.android.settings.Settings$SecuritySettingsActivity":
				handleInSecurityPage();
				handleScrollInSecurityPage();
				break;
			// 二次确认对话框
			case "android.app.AlertDialog":
				handleInUnknownSourcesTurnOnConfirmDialog();
				break;
			default:
				break;
			}
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			switch (event.getClassName().toString()) {
			case "android.widget.ListView":
				handleInSecurityPage();
				handleScrollInSecurityPage();
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
	 * 是否在安全设置页面中
	 *
	 * @return 是否在安全设置页面中
	 */
	@Override
	protected boolean isInSecurityPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"android:id/action_bar",
				"com.android.settings:id/main_content",
				"com.android.settings:id/container_material",
				"android:id/list",
				"android:id/title"
		);
	}
	
	/**
	 * 处理界面在安全设置页面中的逻辑
	 */
	@Override
	protected void runLogicInSecurityPage() {
		// 找到ListView
		List<AccessibilityNodeInfo> listViewNodeInfos = getNodeByViewIdFromRootInActiveWindow("android:id/list");
		if (listViewNodeInfos == null || listViewNodeInfos.isEmpty()) {
			return;
		}
		AccessibilityNodeInfo listViewNodeInfo = listViewNodeInfos.get(0);
		if (listViewNodeInfo == null) {
			return;
		}
		
		for (SecurityTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			if (((target.getAction() & SecurityTarget.ACTION_TURN_ON_UNKNOWNSOURCES) != 0) ||
			    ((target.getAction() & SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES) != 0)) {
				
				// 遍历ListView的item, 找到"未知来源" item
				for (int i = 0; i < listViewNodeInfo.getChildCount(); i++) {
					DLog.i("=============查找第%d个item=============", i);
					// 找到item
					AccessibilityNodeInfo itemInfo = listViewNodeInfo.getChild(i);
					if (itemInfo == null) {
						continue;
					}
					
					// 找到item中的标题
					List<AccessibilityNodeInfo> titleInfos = getNodeByViewIdFromNode(itemInfo, "android:id/title");
					if (titleInfos == null || titleInfos.isEmpty()) {
						continue;
					}
					AccessibilityNodeInfo titleInfo = titleInfos.get(0);
					if (titleInfo == null) {
						continue;
					}
					String title = titleInfo.getText().toString();
					DLog.i("title: %s", title);
					
					// 找到item中的switchWidget
					List<AccessibilityNodeInfo> switchInfos = getNodeByViewIdFromNode(itemInfo, "android:id/switchWidget");
					if (switchInfos == null || switchInfos.isEmpty()) {
						continue;
					}
					AccessibilityNodeInfo switchInfo = switchInfos.get(0);
					if (switchInfo == null) {
						continue;
					}
					
					String unknownSourceStr = getAccessibilityService().getResources()
					                                                   .getString(R.string
							                                                   .accessibility_dispatcher_settings_security_unknown_source);
					if (unknownSourceStr.equals(title)) {
						// 找到了就判断
						// 1. 如果需要开启，但是还没有开启就点击
						// 2. 或者需要关闭，但是还没有关闭就点击
						if (((target.getAction() & SecurityTarget.ACTION_TURN_ON_UNKNOWNSOURCES) != 0 &&
						     !switchInfo.isChecked()) ||
						    ((target.getAction() & SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES) != 0 &&
						     switchInfo.isChecked())) {
							// CheckboxNode不一定能点击，但是item一般都设置能点击，所以用item的node来点击
							if (itemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
								callBackOnUnknownSourceItemClick();
							}
						} else {
							goBack();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 目标可能在下面，所以需要先滑动listview
	 */
	@Override
	protected void scrollInSecurityPage() {
		// 找到ListView
		List<AccessibilityNodeInfo> listViewNodeInfos = getNodeByViewIdFromRootInActiveWindow("android:id/list");
		if (listViewNodeInfos == null || listViewNodeInfos.isEmpty()) {
			return;
		}
		AccessibilityNodeInfo listViewNodeInfo = listViewNodeInfos.get(0);
		if (listViewNodeInfo == null) {
			return;
		}
		listViewNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
	}
	
	/**
	 * 是否在点击允许安装未知来源的开关之后弹出来的确认对话框
	 *
	 * @return 是否在点击允许安装未知来源的开关之后弹出来的确认对话框
	 */
	@Override
	protected boolean isInUnknownSourcesTurnOnConfirmDialog() {
		return isNodeExistInRootActiveWindowByViewIds(
				"android:id/content",
				"android:id/parentPanel",
				"android:id/contentPanel",
				"android:id/message",
				"android:id/buttonPanel",
				"android:id/button2",
				"android:id/button1"
		);
	}
	
	/**
	 * 处理界面在点击允许安装未知来源的开关之后弹出来的确认对话框页面的逻辑
	 */
	@Override
	protected void runLogicInUnknownSourcesTurnOnConfirmDialog() {
		for (SecurityTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			if (((target.getAction() & SecurityTarget.ACTION_TURN_ON_UNKNOWNSOURCES) != 0) ||
			    ((target.getAction() & SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES) != 0)) {
				if (performClickByViewIdFromRootActiveWindow("android:id/button1")) {
					callBackOnUnknownSourceDialogConfirm();
				}
			}
		}
	}
}