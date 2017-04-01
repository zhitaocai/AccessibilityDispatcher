package io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall;

import android.text.TextUtils;

import io.github.zhitaocai.accessibilitydispatcher.businss.ITarget;

/**
 * @author zhitao
 * @since 2017-04-01 09:35
 */
public class ApkInstallTarget implements ITarget {
	
	/**
	 * 自动点击安装
	 */
	public final static int ACTION_AUTO_INSTALL = 1;
	
	/**
	 * 是否等待安装完成
	 * <p>
	 * <ul>
	 * <li>如果存在此ACTION，那么点击安装之后就会等待安装完成</li>
	 * <li>如果不存在此ACTION，那么点击安装之后就会自动返回</li>
	 * </ul>
	 */
	public final static int ACTION_WAIT_INSTALLING = 2;
	
	/**
	 * 安装完成时的界面，点击完成
	 */
	public final static int ACTION_CLICK_FINISH = 2;
	
	/**
	 * 安装完成时的界面，点击打开
	 */
	public final static int ACTION_CLICK_OPEN = 4;
	
	/**
	 * 自动卸载
	 */
	public final static int ACTION_AUTO_DELETE = 8;
	
	/**
	 * 不能卸载
	 */
	public final static int ACTION_CAN_NOT_DELETE = 16;
	
	/**
	 * 安装的应用名字（不是包名是因为界面上不会显示包名）
	 */
	public String mAppName;
	
	/**
	 * ACTION
	 */
	public int action;
	
	private ApkInstallTarget() {
		super();
	}
	
	public String getAppName() {
		return mAppName;
	}
	
	public void setAppName(String appName) {
		mAppName = appName;
	}
	
	public int getAction() {
		return action;
	}
	
	public void setAction(int action) {
		this.action = action;
	}
	
	/**
	 * 建立一些规则来判断每个传入来的目标是否有效
	 *
	 * @return 目标是否有效
	 */
	@Override
	public boolean isValid() {
		if (TextUtils.isEmpty(getAppName())) {
			return false;
		}
		if (getAction() < 0) {
			return false;
		}
		return true;
	}
	
	public static class Builder {
		
		private ApkInstallTarget mApkInstallTarget = new ApkInstallTarget();
		
		public Builder setAppName(String appName) {
			mApkInstallTarget.setAppName(appName);
			return this;
		}
		
		public Builder setAction(int action) {
			mApkInstallTarget.setAction(action);
			return this;
		}
		
		public ApkInstallTarget build() {
			return mApkInstallTarget;
		}
	}
	
}
