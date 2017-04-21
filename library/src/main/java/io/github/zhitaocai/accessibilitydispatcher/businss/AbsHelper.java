package io.github.zhitaocai.accessibilitydispatcher.businss;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.AccessibilityDispatcher;

/**
 * @author zhitao
 * @since 2017-03-23 11:10
 */
public abstract class AbsHelper<T extends ITarget, C extends OnCallBack, H extends IHandlerFactory> {
	
	private boolean mIsEnable;
	
	@Nullable private ArrayList<C> mCallBacks;
	
	@Nullable private ArrayList<T> mTargets;
	
	@Nullable private H mHandlerFactory;
	
	@NonNull private String mIdentify;
	
	/**
	 * @param identify 助手的唯一标识
	 */
	protected AbsHelper(@NonNull String identify) {
		mIdentify = identify;
	}
	
	@Override
	public int hashCode() {
		return mIdentify.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && hashCode() == obj.hashCode();
	}
	
	/**
	 * 重置所有参数
	 *
	 * @return this
	 */
	public AbsHelper<T, C, H> reset() {
		mIsEnable = false;
		mTargets = null;
		mCallBacks = null;
		return this;
	}
	
	/**
	 * 创建一个默认的工厂对象，如果没有调用 {@link #withHandlerFactory(IHandlerFactory)} 方法设置工厂，那么就会使用这个方法创建的默认工厂
	 */
	protected abstract H newDefaultHandlerFactory();
	
	/**
	 * 创建需要的业务对象的工厂
	 *
	 * @return 具体业务创建的工厂，工厂依据一定的规则来生成对应的辅助业务
	 */
	public H getHandlerFactory() {
		return mHandlerFactory;
	}
	
	/**
	 * 初始化生成对应辅助点击的逻辑工具类的工厂
	 * <p>
	 * 不如在魅族手机上，工厂需要生成适配Flyme系统的自动点击工具类，而不需要其他系统的自动点击类
	 *
	 * @param handlerFactory 具体业务创建的工厂
	 *
	 * @return this
	 */
	public AbsHelper<T, C, H> withHandlerFactory(H handlerFactory) {
		mHandlerFactory = handlerFactory;
		return this;
	}
	
	/**
	 * 是否激活这个业务
	 *
	 * @param enable 是否激活
	 *
	 * @return this
	 */
	public AbsHelper<T, C, H> enable(boolean enable) {
		mIsEnable = enable;
		return this;
	}
	
	/**
	 * @return 当前是否激活这个业务
	 */
	public boolean isEnable() {
		return mIsEnable;
	}
	
	/**
	 * @return 获取回调监听器列表
	 */
	public ArrayList<C> getCallBacks() {
		return mCallBacks;
	}
	
	/**
	 * 添加回调监听器(请记得在适当的位置调用 {@link #removeCallBacks(OnCallBack[])} 释放监听)
	 * <p>
	 * e.g.
	 * <p>
	 * 如果你需要知道在安装界面中，是否点击了 "下一步"  或者 "安装" 时，那么可以通过设置回调监听器知道，以此来做一点额外的逻辑，比如统计点击了多少次下一步之类的
	 *
	 * @param callBacks 监听器
	 *
	 * @return this
	 *
	 * @see #removeCallBacks(OnCallBack[])
	 */
	public AbsHelper<T, C, H> withCallBacks(C... callBacks) {
		if (callBacks == null || callBacks.length == 0) {
			return this;
		}
		if (mCallBacks == null) {
			mCallBacks = new ArrayList<>();
		}
		for (C callBack : callBacks) {
			if (!mCallBacks.contains(callBack)) {
				mCallBacks.add(callBack);
			}
		}
		return this;
	}
	
	/**
	 * 移除回调监听器
	 * <p>
	 * 你应该在适当的实际移除监听器，比如自动安装业务完毕之后应该移除监听器
	 *
	 * @param callBacks 监听器
	 *
	 * @return this
	 */
	public AbsHelper<T, C, H> removeCallBacks(C... callBacks) {
		if (callBacks == null || callBacks.length == 0) {
			return this;
		}
		if (mCallBacks == null || mCallBacks.isEmpty()) {
			return this;
		}
		for (C callBack : callBacks) {
			mCallBacks.remove(callBack);
		}
		return this;
	}
	
	/**
	 * @return 获取当前的业务目标
	 */
	public ArrayList<T> getTargets() {
		return mTargets;
	}
	
	/**
	 * 添加业务目标
	 * <p>
	 * 在不同业务中，"目标" 这个定义可能不同，但是大同小异
	 * <p>
	 * e.g.
	 * <pre>
	 * 1. 比如在安装/卸载业务中
	 *      目标可能为 自动安装某个应用  或者 不能卸载某个应用 （应用名 : 对应的action[自动安装，还是防卸载等]）
	 * 2. 比如在安全设置界面业务中
	 *      目标可能为 开启位置来源
	 * </pre>
	 *
	 * @param targets 业务目标
	 *
	 * @return this
	 */
	public AbsHelper<T, C, H> withTargets(T... targets) {
		if (targets == null || targets.length == 0) {
			return this;
		}
		if (mTargets == null) {
			mTargets = new ArrayList<>();
		}
		for (T target : targets) {
			if (!mTargets.contains(target)) {
				mTargets.add(target);
			}
		}
		return this;
	}
	
	/**
	 * 移除指定的业务目标
	 * <p>
	 * 比如当你已经完成了一个应用的自动安装操作，那么应该及时从自动安装业务中移除这个目标及其行为，防止多开业务的时候出现一些问题
	 * <p>
	 * 在不同业务中，"目标" 这个定义可能不同，但是大同小异
	 * <p>
	 * e.g.
	 * <pre>
	 * 1. 比如在安装/卸载业务中
	 *      目标可能为 自动安装某个应用  或者 不能卸载某个应用 （应用名 : 对应的action[自动安装，还是防卸载等]）
	 * 2. 比如在安全设置界面业务中
	 *      目标可能为 开启位置来源
	 * </pre>
	 *
	 * @param targets 业务目标
	 *
	 * @return this
	 */
	public AbsHelper<T, C, H> removeTargets(T... targets) {
		if (targets == null || targets.length == 0) {
			return this;
		}
		if (mTargets == null || mTargets.isEmpty()) {
			return this;
		}
		for (T target : targets) {
			mTargets.remove(target);
		}
		return this;
	}
	
	/**
	 * 将配置加入到 {@link io.github.zhitaocai.accessibilitydispatcher.AccessibilityDispatcher} 中
	 * <p>
	 * 注意：配置完毕之后，必须要调用这个方法，才会真的令辅助功能服务生效，不然你只是在瞎逼逼 ^_^
	 */
	public void active() {
		if (mHandlerFactory == null) {
			mHandlerFactory = newDefaultHandlerFactory();
		}
		AccessibilityDispatcher.updateHelper(this);
	}
	
}
