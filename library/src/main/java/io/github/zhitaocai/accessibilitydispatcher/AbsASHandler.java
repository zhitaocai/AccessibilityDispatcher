package io.github.zhitaocai.accessibilitydispatcher;

import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.businss.ITarget;
import io.github.zhitaocai.accessibilitydispatcher.businss.OnCallBack;

/**
 * 辅助功能抽象类，提供各种节点操作，查找方法，子类继承本类并实现还没有实现的接口即可
 * <p>
 * ps:
 * <p>
 * AS 为AccessibilityService 的缩写
 *
 * @author zhitao
 * @since 2016-08-04 14:57
 */
public abstract class AbsASHandler<T extends ITarget, C extends OnCallBack> extends UtilASHandler {
	
	private boolean mIsEnable = true;
	
	private ArrayList<C> mCallBacks;
	
	private ArrayList<T> mTargets;
	
	/**
	 * 设置是否开启本辅助功能
	 *
	 * @param enable
	 */
	void setEnable(boolean enable) {
		mIsEnable = enable;
	}
	
	/**
	 * 当前是否允许本辅助功能开启
	 */
	boolean isEnable() {
		return mIsEnable;
	}
	
	protected ArrayList<C> getCallBacks() {
		return mCallBacks;
	}
	
	protected void setCallBacks(ArrayList<C> callBacks) {
		mCallBacks = callBacks;
	}
	
	protected ArrayList<T> getTargets() {
		return mTargets;
	}
	
	protected void setTargets(ArrayList<T> targets) {
		mTargets = targets;
	}
	
	void handlerOnServiceConnected() {
		if (isEnable()) {
			onServiceConnected();
		}
	}
	
	void handlerOnInterrupt() {
		if (isEnable()) {
			onInterrupt();
		}
	}
	
	void handlerOnAccessibilityEvent(AccessibilityEvent event) {
		if (isEnable()) {
			onAccessibilityEvent(event);
		}
	}
	
	protected abstract void onServiceConnected();
	
	protected abstract void onInterrupt();
	
	protected abstract void onAccessibilityEvent(AccessibilityEvent event);
	
	@Override
	public int hashCode() {
		return getSupportPkgName().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && hashCode() == obj.hashCode();
	}
	
	/**
	 * 具体实现类的辅助功能所针对的应用包名，
	 * <p>
	 * e.g.
	 * <p>
	 * 如果需要改动系统设置，那么这里的包名可能为 com.android.settings 或者 其他第三方系统所对应的包名
	 *
	 * @return
	 *
	 * @see #isUsingPkgName2TrackEvent()
	 */
	public abstract String getSupportPkgName();
	
	/**
	 * 是否根据包名去定位事件，并交由指定包名的自动点击类处理
	 * <p>
	 * 如果能确定包名的话，当然是最好
	 * 但是如果不能确定包名的话，那么这里就要返回false
	 * <p>
	 * e.g.
	 * <p>
	 * 安装apk的系统应用包名在不同系统上都不太相同，暂时我们可能采取文字搜索来定位 "下一步" "安装" 等等的按钮来点击
	 * （毕竟即便包名不一样，但是给用户显示的界面一般都有"下一步" "安装"这些按钮文字），如果我们采取了这种根据文字搜索的方法来处理的话
	 * 那么我们就不需要包名来定位了，所以这种时候就需要返回false了
	 *
	 * @return
	 *
	 * @see #getSupportPkgName()
	 */
	protected boolean isUsingPkgName2TrackEvent() {
		return true;
	}
	
}
