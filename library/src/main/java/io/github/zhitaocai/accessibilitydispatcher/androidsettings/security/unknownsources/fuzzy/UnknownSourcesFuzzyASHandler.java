package io.github.zhitaocai.accessibilitydispatcher.androidsettings.security.unknownsources.fuzzy;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Switch;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.R;
import io.github.zhitaocai.accessibilitydispatcher.androidsettings.security.AbsSecuritySettingsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 根据文字定位的自动处理
 *
 * @author zhitao
 * @since 2017-03-30 15:25
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class UnknownSourcesFuzzyASHandler extends AbsSecuritySettingsASHandler {
	
	@Override
	public void onServiceConnected() {
		
	}
	
	@Override
	public void onInterrupt() {
		
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			handleInSecurityPage();
			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			handleInSecurityPage();
			handleInUnknownSourcesTurnOnConfirmDialog();
			handleScrollInSecurityPage();
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			handleInSecurityPage();
			handleScrollInSecurityPage();
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
		// 因为是模糊搜索，所以这里直接返回true，不进行页面精确判断
		return true;
	}
	
	/**
	 * 处理界面在安全设置页面中的逻辑
	 */
	@Override
	protected void runLogicInSecurityPage() {
		
		for (SecurityTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			
			// 根据文字模糊搜索 未知来源，找到item的话就点击
			String unknownSourceStr = getAccessibilityService().getResources()
			                                                   .getString(R.string
					                                                   .accessibility_dispatcher_settings_security_unknown_source);
			List<AccessibilityNodeInfo> unknownSourceNodes = getNodeByTextFromRootInActiveWindow(unknownSourceStr);
			if (unknownSourceNodes == null || unknownSourceNodes.isEmpty()) {
				return;
			}
			for (AccessibilityNodeInfo unknownSourceNode : unknownSourceNodes) {
				if (unknownSourceNode == null || unknownSourceNode.getText() == null) {
					continue;
				}
				DLog.i("* text: %s", unknownSourceNode.getText().toString());
				if (!unknownSourceNode.getText().toString().equals(unknownSourceStr)) {
					continue;
				}
				DLog.i("找到允许安装位置来源的View：%s", unknownSourceNode.getText().toString());
				
				// 根据文字定位到node之后还不能直接点击，因为有些系统是不能点击的，这里要做个循环，如果当前node不能点击就找到能点击的父node
				AccessibilityNodeInfo clickNode = unknownSourceNode;
				// 定义20次，防止死循环，应该没有布局找了20次之后还没有尽的吧 - -！
				int count = 0;
				boolean isClickable = clickNode.isClickable();
				while (!isClickable && count++ < 20) {
					clickNode = clickNode.getParent();
					if (clickNode == null) {
						break;
					}
					isClickable = clickNode.isClickable();
				}
				if (clickNode != null && isClickable) {
					// 找到可以点击的布局之后先，还要在确定一下是否和我们的action一致
					// 比如我们是需要打开的话，如果本身就关闭聊，就没必要点击
					// 因此我们需要继续从这个可以点击node开始向下查找Checkbox或者Switch控件，检查状态
					AccessibilityNodeInfo nodeInfo = getNodeByClass(clickNode, CheckBox.class, Switch.class);
					if (nodeInfo == null) {
						break;
					}
					
					// 找到了就判断
					// 1. 如果需要开启，但是还没有开启就点击
					// 2. 或者需要关闭，但是还没有关闭就点击
					if (((target.getAction() & SecurityTarget.ACTION_TURN_ON_UNKNOWNSOURCES) != 0 && !nodeInfo.isChecked()) ||
					    ((target.getAction() & SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES) != 0 && nodeInfo.isChecked())) {
						nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					} else {
						goBack();
					}
				}
				break;
			}
		}
	}
	
	/**
	 * 目标可能在下面，所以需要先滑动listview
	 */
	@Override
	protected void scrollInSecurityPage() {
		// 因为不同系统版本有的用ListView或者用RecyclerView，所以我们一次找两个
		// 找到ListView 或者 RecyclerView 然后滑动他
		AccessibilityNodeInfo listNodeInfo =
				getNodeByClassName(ListView.class.getName(), "android.support.v7.widget.RecyclerView");
		if (listNodeInfo == null) {
			return;
		}
		listNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
	}
	
	/**
	 * 是否在点击允许安装未知来源的开关之后弹出来的确认对话框
	 *
	 * @return
	 */
	@Override
	protected boolean isInUnknownSourcesTurnOnConfirmDialog() {
		// 因为是模糊搜索，所以这里直接返回true，不进行页面精确判断
		return true;
	}
	
	/**
	 * 处理界面在点击允许安装未知来源的开关之后弹出来的确认对话框页面的逻辑
	 *
	 * @return
	 */
	@Override
	protected void runLogicInUnknownSourcesTurnOnConfirmDialog() {
		
		// 根据文字模糊搜索 确定，找到item的话就点击
		String confirmStr = getAccessibilityService().getResources()
		                                             .getString(R.string
				                                             .accessibility_dispatcher_settings_security_unknown_source_confirm);
		List<AccessibilityNodeInfo> confirmNodes = getNodeByTextFromRootInActiveWindow(confirmStr);
		if (confirmNodes == null || confirmNodes.isEmpty()) {
			return;
		}
		for (AccessibilityNodeInfo confirmNode : confirmNodes) {
			if (confirmNode == null || confirmNode.getText() == null) {
				continue;
			}
			DLog.i("* text: %s", confirmNode.getText().toString());
			if (!confirmNode.getText().toString().equals(confirmStr)) {
				continue;
			}
			DLog.i("找到对话框 确定 的View：%s", confirmNode.getText().toString());
			
			// 根据文字定位到node之后还不能直接点击，因为有些系统是不能点击的，这里要做个循环，如果当前node不能点击就找到能点击的父node
			AccessibilityNodeInfo clickNode = confirmNode;
			// 定义20次，防止死循环，应该没有布局找了20次之后还没有尽的吧 - -！
			int count = 0;
			boolean isClickable = clickNode.isClickable();
			while (!isClickable && count++ < 20) {
				clickNode = clickNode.getParent();
				if (clickNode == null) {
					break;
				}
				isClickable = clickNode.isClickable();
			}
			
			if (clickNode != null && isClickable) {
				clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
			break;
		}
	}
}