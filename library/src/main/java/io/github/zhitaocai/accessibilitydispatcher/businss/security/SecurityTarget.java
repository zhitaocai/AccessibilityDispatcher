package io.github.zhitaocai.accessibilitydispatcher.businss.security;

import io.github.zhitaocai.accessibilitydispatcher.businss.ITarget;

/**
 * @author zhitao
 * @since 2017-03-30 11:48
 */
public class SecurityTarget implements ITarget {
	
	/**
	 * 打开允许安装位置来源的开关
	 */
	public final static int ACTION_TURN_ON_UNKNOWNSOURCES = 1;
	
	/**
	 * 关闭允许安装位置来源的开关
	 */
	public final static int ACTION_TURN_OFF_UNKNOWNSOURCES = 2;
	
	private int mAction;
	
	private SecurityTarget() {
		super();
	}
	
	public int getAction() {
		return mAction;
	}
	
	public void setAction(int action) {
		mAction = action;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SecurityTarget{");
		sb.append("\n  mAction=").append(mAction);
		sb.append("\n}");
		return sb.toString();
	}
	
	/**
	 * 建立一些规则来判断每个传入来的目标是否有效
	 *
	 * @return 目标是否有效
	 */
	@Override
	public boolean isValid() {
		if (getAction() < 0) {
			return false;
		}
		// 不能同时设置 开启和关闭
		if (((getAction() & ACTION_TURN_ON_UNKNOWNSOURCES) != 0) && ((getAction() & ACTION_TURN_OFF_UNKNOWNSOURCES) != 0)) {
			return false;
		}
		return true;
	}
	
	public static class Builder {
		
		private SecurityTarget mSecurityTarget = new SecurityTarget();
		
		public Builder setAction(int action) {
			mSecurityTarget.setAction(action);
			return this;
		}
		
		public SecurityTarget build() {
			return mSecurityTarget;
		}
	}
	
}
