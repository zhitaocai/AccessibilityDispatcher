package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.OnCallBack;

/**
 * @author zhitao
 * @since 2017-03-23 11:10
 */
public final class VpnHelper extends AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> {
	
	private static VpnHelper sVpnHelper;
	
	private VpnHelper() {
		super();
		initHandlerFactory(new VpnHandlerFactory());
	}
	
	public static VpnHelper getInstance() {
		if (sVpnHelper == null) {
			sVpnHelper = new VpnHelper();
		}
		return sVpnHelper;
	}
	
	/**
	 * 业务唯一标识
	 *
	 * @return 业务唯一表示，分发器依据此来识别不同的辅助业务
	 */
	@NonNull
	@Override
	public String getIdentify() {
		return "android.net.vpn.SETTINGS";
	}
	
	@Override
	@Deprecated
	public AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> setCallBacks(ArrayList<OnVpnCallBack> callBacks) {
		return super.setCallBacks(callBacks);
	}
	
	@Override
	@Deprecated
	public AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> setTargets(ArrayList<VpnTarget> targets) {
		return super.setTargets(targets);
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
	 * @return 自身对象
	 */
	@Deprecated
	@Override
	public AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> addTargets(VpnTarget... targets) {
		return super.addTargets(targets);
	}
	
	/**
	 * 添加回调监听器(请记得在适当的位置调用 {@link #removeCallBack(OnCallBack[])} 释放监听)
	 * <p>
	 * e.g.
	 * <p>
	 * 如果你需要知道在安装界面中，是否点击了 "下一步"  或者 "安装" 时，那么可以通过设置回调监听器知道，以此来做一点额外的逻辑，比如统计点击了多少次下一步之类的
	 *
	 * @param callBacks 回调监听器
	 *
	 * @return 自身对象
	 *
	 * @see #removeCallBack(OnCallBack[])
	 */
	@Override
	@Deprecated
	public AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> addCallBacks(OnVpnCallBack... callBacks) {
		return super.addCallBacks(callBacks);
	}
	
}