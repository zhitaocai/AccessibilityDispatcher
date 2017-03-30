package io.github.zhitaocai.accessibilitydispatcher;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;
import java.util.concurrent.ExecutorService;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;
import io.github.zhitaocai.accessibilitydispatcher.utils.ClipboardManagerUtil;

/**
 * @author zhitao
 * @since 2017-03-23 15:09
 */
abstract class UtilASHandler {
	
	private AccessibilityService mAccessibilityService;
	
	/**
	 * 获取线程池，因为辅助功能提供的3个回调本质还是执行在UI线程中，所以耗时操作，就放在线程池中实现吧
	 *
	 * @return 异步线程池
	 */
	protected ExecutorService getExecutor() {
		return AccessibilityDispatcher.getSingleThreadExecutor();
	}
	
	/**
	 * 获取UIHandler
	 *
	 * @return UIHandler
	 */
	protected Handler getUIHandler() {
		return AccessibilityDispatcher.getUIHandler();
	}
	
	/**
	 * 设置辅助功能服务对象
	 *
	 * @param service 辅助功能服务对象
	 */
	public void setAccessibilityService(AccessibilityService service) {
		mAccessibilityService = service;
	}
	
	/**
	 * 获取辅助功能服务对象
	 *
	 * @return 辅助功能服务对象
	 */
	public AccessibilityService getAccessibilityService() {
		return mAccessibilityService;
	}
	
	/**
	 * 输入内容到EditText中
	 *
	 * @param viewId          EditText的viewId
	 * @param text            待输入的内容
	 * @param isFromStart     主要为在18，19，20，这三个api生效，主要是有些EditTextView的光标有时在最前面，有时在最后面，导致全选时需要判断一下
	 *                        <ul>
	 *                        <li>true：从EditText开头向后全选文字</li>
	 *                        <li>false：从EditText结尾向前开始全选文字</li>
	 *                        </ul>
	 * @param granularityType 主要为在18，19，20，这三个api生效，全选时是以什么间隔
	 *                        {@link AccessibilityNodeInfo#MOVEMENT_GRANULARITY_LINE}之类
	 *
	 * @return false 失败（可能没有找到对应的节点之类的） true 成功
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean performInputTextByViewIdFromRootInActiveWindow(@NonNull String viewId, @NonNull String text,
			boolean isFromStart, int granularityType) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return false;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		nodeInfo.recycle();
		DLog.i("viewId: %s 在 rootActiveWindow 中有%d个node", viewId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 只针对第一个找到的来进行处理
		inputText(list.get(0), text, isFromStart, granularityType);
		
		//		for (AccessibilityNodeInfo item : list) {
		//			AccessibilityNodeHelper.inputText(getAccessibilityService().getApplicationContext(), item, text);
		//		}
		return true;
	}
	
	/**
	 * 输入内容到EditText中
	 *
	 * @param nodeInfo        如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param viewId          EditText的viewId
	 * @param text            待输入的内容
	 * @param isFromStart     主要为在18，19，20，这三个api生效，主要是有些EditTextView的光标有时在最前面，有时在最后面，导致全选时需要判断一下
	 *                        <ul>
	 *                        <li>true：从EditText开头向后全选文字</li>
	 *                        <li>false：从EditText结尾向前开始全选文字</li>
	 *                        </ul>
	 * @param granularityType 主要为在18，19，20，这三个api生效，全选时是以什么间隔
	 *                        {@link AccessibilityNodeInfo#MOVEMENT_GRANULARITY_LINE}之类
	 *
	 * @return false 失败（可能没有找到对应的节点之类的） true 成功
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean performInputTextByViewIdFromNode(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull String viewId,
			@NonNull String text, boolean isFromStart, int granularityType) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		DLog.i("viewId: %s 在 node[%s] 中有%d个node", viewId, nodeInfo.getViewIdResourceName(), list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 只针对第一个找到的来进行处理
		inputText(list.get(0), text, isFromStart, granularityType);
		return true;
	}
	
	/**
	 * 输入内容到EditText中
	 *
	 * @param textId          EditText的中所显示的内容
	 * @param text            待输入的内容
	 * @param isFromStart     主要为在18，19，20，这三个api生效，主要是有些EditTextView的光标有时在最前面，有时在最后面，导致全选时需要判断一下
	 *                        <ul>
	 *                        <li>true：从EditText开头向后全选文字</li>
	 *                        <li>false：从EditText结尾向前开始全选文字</li>
	 *                        </ul>
	 * @param granularityType 主要为在18，19，20，这三个api生效，全选时是以什么间隔
	 *                        {@link AccessibilityNodeInfo#MOVEMENT_GRANULARITY_LINE}之类
	 *
	 * @return false 失败（可能没有找到对应的节点之类的） true 成功
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected boolean performInputTextByTextFromRootInActiveWindow(@NonNull String textId, @NonNull String text,
			boolean isFromStart, int granularityType) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return false;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		nodeInfo.recycle();
		DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", textId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 只针对第一个找到的来进行处理
		inputText(list.get(0), text, isFromStart, granularityType);
		return true;
	}
	
	/**
	 * 输入内容到EditText中
	 *
	 * @param nodeInfo        如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param textId          EditText的中所显示的内容
	 * @param text            待输入的内容
	 * @param isFromStart     主要为在18，19，20，这三个api生效，主要是有些EditTextView的光标有时在最前面，有时在最后面，导致全选时需要判断一下
	 *                        <ul>
	 *                        <li>true：从EditText开头向后全选文字</li>
	 *                        <li>false：从EditText结尾向前开始全选文字</li>
	 *                        </ul>
	 * @param granularityType 主要为在18，19，20，这三个api生效，全选时是以什么间隔
	 *                        {@link AccessibilityNodeInfo#MOVEMENT_GRANULARITY_LINE}之类
	 *
	 * @return false 失败（可能没有找到对应的节点之类的） true 成功
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected boolean performInputTextByTextFromNode(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull String textId,
			@NonNull String text, boolean isFromStart, int granularityType) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		if (Build.VERSION.SDK_INT >= 18) {
			DLog.i("textId: %s 在 node[%s] 中有%d个node", textId, nodeInfo.getViewIdResourceName(), list == null ? 0 : list.size());
		} else {
			DLog.i("textId: %s 在 node 中有%d个node", textId, list == null ? 0 : list.size());
		}
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 只针对第一个找到的来进行处理
		inputText(list.get(0), text, isFromStart, granularityType);
		return true;
	}
	
	/**
	 * 向指定的节点输入文字
	 *
	 * @param node            起始节点
	 * @param text            要查找的文字
	 * @param isFromStart     主要为在18，19，20，这三个api生效，主要是有些EditTextView的光标有时在最前面，有时在最后面，导致全选时需要判断一下
	 *                        <ul>
	 *                        <li>true：从EditText开头向后全选文字</li>
	 *                        <li>false：从EditText结尾向前开始全选文字</li>
	 *                        </ul>
	 * @param granularityType 主要为在18，19，20，这三个api生效，全选时是以什么间隔
	 *                        {@link AccessibilityNodeInfo#MOVEMENT_GRANULARITY_LINE}之类
	 *
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private boolean inputText(@NonNull AccessibilityNodeInfo node, @NonNull String text, boolean isFromStart,
			int granularityType) {
		if (Build.VERSION.SDK_INT >= 21) {
			//android>=21 = 5.0时可以用ACTION_SET_TEXT
			Bundle arg = new Bundle();
			arg.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
			return node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arg);
		} else if (Build.VERSION.SDK_INT >= 18) {
			
			// android>=18
			// 可以通过复制的手段，先确定焦点，再粘贴ACTION_PASTE
			// 选择一整页，然后粘贴我们的内容，算是一种绕路吧
			
			// 默认粘贴是仅仅append到EditText，所以我们需要清空原有的内容先，但是没有方法，所以我们只能绕路
			// 将当前所有的文字全选然后在粘贴，算是一种清空(替换)方案
			
			// 1. 全选节点中文字
			Bundle arguments = new Bundle();
			arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT, granularityType);
			arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN, true);
			if (isFromStart) {
				node.performAction(AccessibilityNodeInfo.ACTION_NEXT_AT_MOVEMENT_GRANULARITY, arguments);
			} else {
				node.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY, arguments);
			}
			
			// 2. 保存目标文字到剪切板
			ClipboardManagerUtil.setText(getAccessibilityService().getApplicationContext(), text);
			
			// 3. 最后将剪切板中的文字复制到节点中已经全选的文字
			node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
			return node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
		}
		return false;
	}
	
	/**
	 * 模拟点击,从当前激活的界面中开始查找
	 *
	 * @param viewId 组件的资源id
	 *
	 * @return 点击成功与否
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean performClickByViewIdFromRootActiveWindow(String viewId) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return false;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		nodeInfo.recycle();
		DLog.i("viewId: %s 在 rootActiveWindow 中有%d个node", viewId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return false;
		}
		
		// 只针对第一个找到的节点进行处理
		list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
		return true;
	}
	
	/**
	 * 模拟点击,从提供的event中的节点开始查找
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param viewId   组件的资源id
	 *
	 * @return 点击成功与否
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean performClickByViewIdFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String viewId) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		DLog.i("viewId: %s 在 rootActiveWindow 中有%d个node", viewId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return false;
		}
		
		// 只针对第一个找到的节点进行处理
		list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
		return true;
	}
	
	/**
	 * 模拟点击,从当前激活的界面中开始查找
	 *
	 * @param textId 要点击的按钮之类所用的文字
	 *
	 * @return 点击成功与否
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected boolean performClickByTextFromRootActiveWindow(String textId) {
		return performClickByTextFromRootActiveWindow(textId, null);
	}
	
	/**
	 * 模拟点击,从当前激活的界面中开始查找
	 *
	 * @param textId      要点击的按钮之类所用的文字
	 * @param sourceClass 指定最终的子节点的对象类型 (TextView.class 之类)
	 *
	 * @return 点击成功与否
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected boolean performClickByTextFromRootActiveWindow(String textId, Class sourceClass) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return false;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		nodeInfo.recycle();
		DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", textId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 只针对第一个找到的节点进行处理
		if (sourceClass != null) {
			if (list.get(0).getClassName().toString().equals(sourceClass.getName())) {
				list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		} else {
			list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
		}
		return true;
	}
	
	/**
	 * 模拟点击,从提供的event中的节点开始查找
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param textId   要点击的按钮之类所用的文字
	 *
	 * @return 点击成功与否
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected boolean performClickByTextFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String textId) {
		return performClickByTextFromNode(nodeInfo, textId, null);
	}
	
	/**
	 * 模拟点击,从提供的event中的节点开始查找
	 *
	 * @param nodeInfo    如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param textId      要点击的按钮之类所用的文字
	 * @param sourceClass 指定最终的子节点的对象类型 (TextView.class 之类)
	 *
	 * @return 点击成功与否
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected boolean performClickByTextFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String textId, Class sourceClass) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		if (Build.VERSION.SDK_INT >= 18) {
			DLog.i("textId: %s 在 node[%s] 中有%d个node", textId, nodeInfo.getViewIdResourceName(), list == null ? 0 : list.size());
		} else {
			DLog.i("textId: %s 在 node 中有%d个node", textId, list == null ? 0 : list.size());
		}
		if (list == null || list.isEmpty()) {
			return false;
		}
		// 只针对第一个找到的节点进行处理
		if (sourceClass != null) {
			if (list.get(0).getClassName().toString().equals(sourceClass.getName())) {
				list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
			}
		} else {
			list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
		}
		return true;
	}
	
	/**
	 * 获取指定viewId所在组件的txt
	 *
	 * @param viewId 要查找的View的ResourceId
	 *
	 * @return 获取到的文字
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected String getTextByViewIdFromRootActiveWindow(String viewId) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return null;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		nodeInfo.recycle();
		DLog.i("viewId: %s 在 rootActiveWindow 中有%d个node", viewId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return null;
		}
		// 只针对第一个找到的节点进行处理
		return list.get(0).getText() == null ? null : list.get(0).getText().toString();
	}
	
	/**
	 * 获取指定viewId所在组件的txt
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param viewId   要查找的View的ResourceId
	 *
	 * @return 获取到的文字
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected String getTextByViewIdFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String viewId) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		DLog.i("viewId: %s 在 node[%s] 中有%d个node", viewId, nodeInfo.getViewIdResourceName(), list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return null;
		}
		// 只针对第一个找到的节点进行处理
		return list.get(0).getText() == null ? null : list.get(0).getText().toString();
	}
	
	/**
	 * 获取指定text所在组件的txt
	 *
	 * @param textId 要查找的文字
	 *
	 * @return 获取到的文字
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected String getTextByTextFromRootActiveWindow(String textId) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return null;
		}
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		nodeInfo.recycle();
		DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", textId, list == null ? 0 : list.size());
		if (list == null || list.isEmpty()) {
			return null;
		}
		// 只针对第一个找到的节点进行处理
		return list.get(0).getText() == null ? null : list.get(0).getText().toString();
	}
	
	/**
	 * 获取指定text所在组件的txt
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param textId   要查找的文字
	 *
	 * @return 获取到的文字
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected String getTextByTextFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String textId) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		if (Build.VERSION.SDK_INT >= 18) {
			DLog.i("textId: %s 在 node[%s] 中有%d个node", textId, nodeInfo.getViewIdResourceName(), list == null ? 0 : list.size());
		} else {
			DLog.i("textId: %s 在 node 中有%d个node", textId, list == null ? 0 : list.size());
		}
		if (list == null || list.isEmpty()) {
			return null;
		}
		// 只针对第一个找到的节点进行处理
		return list.get(0).getText() == null ? null : list.get(0).getText().toString();
	}
	
	/**
	 * 在当前的RootActiveWindow中获取指定的viewId所对应的节点
	 *
	 * @param viewId 要查找的View的ResourceId
	 *
	 * @return 找到的列表
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@CheckResult
	protected List<AccessibilityNodeInfo> getNodeByViewIdFromRootInActiveWindow(String viewId) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return null;
		}
		List<AccessibilityNodeInfo> temp = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		DLog.i("viewId: %s 在 rootActiveWindow 中有%d个node", viewId, temp == null ? 0 : temp.size());
		nodeInfo.recycle();
		return temp;
	}
	
	/**
	 * 从提供的event节点中开始查找指定的viewId所对应的节点
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param viewId   要查找的View的ResourceId
	 *
	 * @return 找到的列表
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@CheckResult
	protected List<AccessibilityNodeInfo> getNodeByViewIdFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String viewId) {
		List<AccessibilityNodeInfo> temp = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
		DLog.i("viewId: %s 在 node[%s] 中有%d个node", viewId, nodeInfo.getViewIdResourceName(), temp == null ? 0 : temp.size());
		return temp;
	}
	
	/**
	 * 在当前的RootActiveWindow中获取指定的文字所对应的节点
	 *
	 * @param textId 要查找的文字
	 *
	 * @return 找到的列表
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@CheckResult
	protected List<AccessibilityNodeInfo> getNodeByTextFromRootInActiveWindow(String textId) {
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return null;
		}
		List<AccessibilityNodeInfo> temp = nodeInfo.findAccessibilityNodeInfosByText(textId);
		DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", textId, temp == null ? 0 : temp.size());
		nodeInfo.recycle();
		return temp;
	}
	
	/**
	 * 从提供的event节点中开始查找指定的文字所对应的节点
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param textId   要查找的文字
	 *
	 * @return 找到的列表
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@CheckResult
	protected List<AccessibilityNodeInfo> getNodeByTextFromNode(@NonNull AccessibilityNodeInfo nodeInfo, String textId) {
		List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(textId);
		if (Build.VERSION.SDK_INT >= 18) {
			DLog.i("textId: %s 在 node[%s] 中有%d个node", textId, nodeInfo.getViewIdResourceName(), list == null ? 0 : list.size());
		} else {
			DLog.i("textId: %s 在 node 中有%d个node", textId, list == null ? 0 : list.size());
		}
		return list;
	}
	
	/**
	 * 从根节点开始向下查找指定类名的组件（深度遍历），在找到一个符合之后就会结束
	 *
	 * @param cls 类（可多个），每进行一次节点的深度遍历，都会遍历一遍这里传入来的类的类名，找到了就立即返回
	 *
	 * @return 最后找到的节点
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected AccessibilityNodeInfo getNodeByClass(@NonNull Class... cls) {
		AccessibilityNodeInfo rootNodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (rootNodeInfo == null) {
			return null;
		}
		AccessibilityNodeInfo result = getNodeByClass(rootNodeInfo, cls);
		rootNodeInfo.recycle();
		return result;
	}
	
	/**
	 * 从指定的节点开始向下查找指定类名的组件（深度遍历），在找到一个符合之后就会结束
	 *
	 * @param nodeInfo 起始节点
	 * @param cls      类（可多个），每进行一次节点的深度遍历，都会遍历一遍这里传入来的类的类名，找到了就立即返回
	 *
	 * @return 最后找到的节点
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected AccessibilityNodeInfo getNodeByClass(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull Class... cls) {
		String[] className = new String[cls.length];
		for (int i = 0; i < cls.length; i++) {
			className[i] = cls[i].getName();
		}
		return getNodeByClassName(nodeInfo, className);
	}
	
	/**
	 * 从根节点节点开始向下查找指定类名的组件（深度遍历），在找到一个符合之后就会结束
	 *
	 * @param classNames 类名（可多个），每进行一次节点的深度遍历，都会遍历一遍这里传入来的类名，找到了就立即返回
	 *
	 * @return 最后找到的节点
	 */
	@Nullable
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected AccessibilityNodeInfo getNodeByClassName(@NonNull String... classNames) {
		AccessibilityNodeInfo rootNodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (rootNodeInfo == null) {
			return null;
		}
		AccessibilityNodeInfo result = getNodeByClassName(rootNodeInfo, classNames);
		rootNodeInfo.recycle();
		return result;
	}
	
	/**
	 * 从指定的节点开始向下查找指定类名的组件（深度遍历），在找到一个符合之后就会结束
	 *
	 * @param nodeInfo   起始节点
	 * @param classNames 类名（可多个），每进行一次节点的深度遍历，都会遍历一遍这里传入来的类名，找到了就立即返回
	 *
	 * @return 最后找到的节点
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected AccessibilityNodeInfo getNodeByClassName(@NonNull AccessibilityNodeInfo nodeInfo, @NonNull String... classNames) {
		if (nodeInfo.getChildCount() == 0) {
			return null;
		}
		for (int i = 0; i < nodeInfo.getChildCount(); i++) {
			AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
			if (DLog.isDebug()) {
				StringBuilder sb = new StringBuilder(classNames.length);
				for (String className : classNames) {
					sb.append(className).append(" ");
				}
				DLog.i("index: %d className: %s target: %s", i, childNodeInfo.getClassName().toString(), sb.toString());
			}
			for (String className : classNames) {
				if (childNodeInfo.getClassName().toString().equals(className)) {
					return childNodeInfo;
				}
			}
			AccessibilityNodeInfo switchOrCheckBoxNodeInfo = getNodeByClassName(childNodeInfo, classNames);
			if (switchOrCheckBoxNodeInfo != null) {
				return switchOrCheckBoxNodeInfo;
			}
		}
		return null;
	}
	
	/**
	 * 检查是否存在指定viewId的节点 (使用 {@link #isNodeExistInRootActiveWindowByViewIds(String...)}代替
	 *
	 * @param viewId 要找的View的ResourceId
	 *
	 * @return 存在结果
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Deprecated
	protected boolean isNodeExistInRootActiveWindowByViewId(String viewId) {
		List<AccessibilityNodeInfo> temp = getNodeByViewIdFromRootInActiveWindow(viewId);
		return temp != null && !temp.isEmpty();
	}
	
	/**
	 * 检查是否存在指定viewId的节点
	 *
	 * @param viewIds 要找的View的ResourceId
	 *
	 * @return 只要有一个不存在都返回false
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean isNodeExistInRootActiveWindowByViewIds(String... viewIds) {
		if (viewIds == null) {
			return false;
		}
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return false;
		}
		
		boolean result = true;
		for (String viewId : viewIds) {
			List<AccessibilityNodeInfo> temp = nodeInfo.findAccessibilityNodeInfosByViewId(viewId);
			DLog.i("viewId: %s 在 rootActiveWindow 中有%d个node", viewId, temp == null ? 0 : temp.size());
			if (temp == null || temp.isEmpty()) {
				result = false;
				break;
			}
		}
		nodeInfo.recycle();
		return result;
	}
	
	/**
	 * 检查是否存在指定textId的节点
	 *
	 * @param textIds 要查找的文字
	 *
	 * @return 只要有一个不存在都返回false
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean isNodeExistInRootActiveWindowByTextIds(String... textIds) {
		if (textIds == null) {
			return false;
		}
		AccessibilityNodeInfo nodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (nodeInfo == null) {
			return false;
		}
		
		boolean result = true;
		for (String textId : textIds) {
			List<AccessibilityNodeInfo> targetNodeInfos = nodeInfo.findAccessibilityNodeInfosByText(textId);
			DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", textId, targetNodeInfos == null ? 0 : targetNodeInfos.size());
			if (targetNodeInfos == null || targetNodeInfos.isEmpty()) {
				result = false;
				break;
			}
			boolean isFindTarget = false;
			for (AccessibilityNodeInfo targetNodeInfo : targetNodeInfos) {
				if (targetNodeInfo == null) {
					continue;
				}
				if (targetNodeInfo.getText() == null) {
					continue;
				}
				if (textId.equals(targetNodeInfo.getText().toString())) {
					isFindTarget = true;
					break;
				}
			}
			if (!isFindTarget) {
				result = false;
				break;
			}
		}
		nodeInfo.recycle();
		return result;
	}
	
	/**
	 * 检查是否存在指定viewId的节点
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param viewId   要找的View的ResourceId
	 *
	 * @return 查找结果
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	protected boolean isNodeExistInEventSourceByViewId(@NonNull AccessibilityNodeInfo nodeInfo, String viewId) {
		List<AccessibilityNodeInfo> temp = getNodeByViewIdFromNode(nodeInfo, viewId);
		return temp != null && !temp.isEmpty();
	}
	
	/**
	 * 检查是否存在指定textId的节点
	 *
	 * @param textId 要查找的文字
	 *
	 * @return 查找结果
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected boolean isNodeExistInRootActiveWindowByText(String textId) {
		List<AccessibilityNodeInfo> temp = getNodeByTextFromRootInActiveWindow(textId);
		return temp != null && !temp.isEmpty();
	}
	
	/**
	 * 检查是否存在指定textId的节点
	 *
	 * @param nodeInfo 如果是event 那么调用nodeInfo就有AccessibilityNodeInfo对象
	 * @param textId   要查找的文字
	 *
	 * @return 查找结果
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	protected boolean isNodeExistInEventSourceByText(@NonNull AccessibilityNodeInfo nodeInfo, String textId) {
		List<AccessibilityNodeInfo> temp = getNodeByTextFromNode(nodeInfo, textId);
		return temp != null && !temp.isEmpty();
	}
	
	/**
	 * 返回桌面
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void goHome() {
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			getAccessibilityService().startActivity(intent);
		} catch (Throwable e) {
			DLog.e(e);
			getAccessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
		}
	}
	
	/**
	 * 返回
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void goBack() {
		getAccessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
	}
	
	/**
	 * 打开最近应用列表
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	protected void goRecentsAppList() {
		getAccessibilityService().performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
	}
	
	/**
	 * 模拟按键
	 * <p>
	 * e.g.
	 * <p>
	 * 比如，模拟点击了软键盘中的搜索按键
	 * <p>
	 * ！！！需要root，需要在线程中使用，慎重！！！
	 *
	 * @param keycode see {@link KeyEvent}{@code .KEYCODE_*}
	 */
	@Deprecated
	protected void performKeyEvent(int keycode) {
		
		// 利用shell 执行input模拟按键命令
		// ShellUtils.su("input keyevent " + keycode);
		
		// 利用Instrument
		// Instrumentation instrumentation = new Instrumentation();
		// instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_HOME);
	}
	
}
