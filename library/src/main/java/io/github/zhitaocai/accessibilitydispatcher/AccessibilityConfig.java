package io.github.zhitaocai.accessibilitydispatcher;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-03-22 22:15
 */
public final class AccessibilityConfig {
	
	private boolean mIsShowDebugLog;
	
	private boolean mIsShowEventSourceLog;
	
	private boolean mIsShowClassNameInTag;
	
	AccessibilityConfig() {
		super();
		mIsShowDebugLog = false;
		mIsShowEventSourceLog = false;
		mIsShowClassNameInTag = false;
	}
	
	boolean isShowClassNameInTag() {
		return mIsShowClassNameInTag;
	}
	
	/**
	 * 调试的Log显示时，是否显示log调用时所在的类名
	 *
	 * @param showClassNameInTag
	 *
	 * @return
	 */
	public AccessibilityConfig withClassNameInTag(boolean showClassNameInTag) {
		mIsShowClassNameInTag = showClassNameInTag;
		return this;
	}
	
	boolean isShowEventSourceLog() {
		return mIsShowEventSourceLog;
	}
	
	/**
	 * 调试的Log显示时，是否显示时间源输出
	 *
	 * @param isShowEventSourceLog
	 *
	 * @return
	 */
	public AccessibilityConfig withEventSourceLog(boolean isShowEventSourceLog) {
		mIsShowEventSourceLog = isShowEventSourceLog;
		return this;
	}
	
	boolean isShowDebugLog() {
		return mIsShowDebugLog;
	}
	
	AccessibilityConfig debugLog(boolean isShowDebugLog) {
		mIsShowDebugLog = isShowDebugLog;
		DLog.setIsDebug(isShowDebugLog);
		return this;
	}
}
