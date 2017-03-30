package io.github.zhitaocai.accessibilitydispatcher.androidsettings.security.unknownsources.android;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.androidsettings.security.AbsSecuritySettingsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 支持原生Android 4.4.4系统
 *
 * @author zhitao
 * @since 2017-03-30 15:19
 */
public class AndroidUnknownSourcesASHandler444 extends AbsSecuritySettingsASHandler {
	
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
	 * @return
	 */
	@Override
	protected boolean isInSecurityPage() {
		return isNodeExistInRootActiveWindowByViewIds(
				"android:id/action_bar_title",
				"android:id/prefs_frame",
				"android:id/prefs",
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
			
			// 找到item中的子标题
			List<AccessibilityNodeInfo> subTitleInfos = getNodeByViewIdFromNode(itemInfo, "android:id/summary");
			if (subTitleInfos == null || subTitleInfos.isEmpty()) {
				continue;
			}
			AccessibilityNodeInfo subTitleInfo = subTitleInfos.get(0);
			if (subTitleInfo == null) {
				continue;
			}
			String subTitle = subTitleInfo.getText().toString();
			
			// 找到item中的checkbox
			List<AccessibilityNodeInfo> checkBoxInfos = getNodeByViewIdFromNode(itemInfo, "android:id/checkbox");
			if (checkBoxInfos == null || checkBoxInfos.isEmpty()) {
				continue;
			}
			AccessibilityNodeInfo checkBoxInfo = checkBoxInfos.get(0);
			if (checkBoxInfo == null) {
				continue;
			}
			
			// 检查内容是否为"未知来源"，是的话，检查checkBox状态
			if (isUnknownSourcesItem(title, subTitle)) {
				boolean isChecked = checkBoxInfo.isChecked();
				if (!isChecked) {
					// 如果还没有选中就点击Item选择
					itemInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				} else {
					goBack();
				}
			}
		}
	}
	
	private boolean isUnknownSourcesItem(String title, String subTitle) {
		if (title.contains("未知来源")) {
			return true;
		} else if (title.contains("Unknown sources")) {
			return true;
		}
		return false;
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
	 * @return
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
	 *
	 * @return
	 */
	@Override
	protected void runLogicInUnknownSourcesTurnOnConfirmDialog() {
		performClickByViewIdFromRootActiveWindow("android:id/button1");
	}
}
