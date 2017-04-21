package io.github.zhitaocai.accessibilitydispatcher;

import android.content.Intent;
import android.provider.Settings;
import android.util.SparseArray;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallHandlerFactory;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.OnApkInstallCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.OnSecurityCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityHandlerFactory;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnHandlerFactory;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;

/**
 * @author zhitao
 * @since 2017-04-21 10:31
 */
public class AccessibilityHelper {
	
	private static final SparseArray<AbsHelper> sAbsHelperSparseArray = new SparseArray<>();
	
	private final static String IDENTIFY_VPN = "android.net.vpn.SETTINGS";
	
	private final static String IDENTIFY_SECURITY = Settings.ACTION_SECURITY_SETTINGS;
	
	private final static String IDENTIFY_APK_INSTALL = Intent.ACTION_INSTALL_PACKAGE;
	
	/**
	 * 创建一个VPN自动配置的辅助功能助手
	 *
	 * @return 一个VPN自动配置的辅助功能助手对象
	 */
	public static AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory> vpnHelper() {
		synchronized (sAbsHelperSparseArray) {
			AbsHelper absHelper = sAbsHelperSparseArray.get(IDENTIFY_VPN.hashCode());
			if (absHelper == null) {
				absHelper = new AbsHelper<VpnTarget, OnVpnCallBack, VpnHandlerFactory>("android.net.vpn.SETTINGS") {
					
					/**
					 * 创建一个默认的工厂对象，如果没有调用 {@link #withHandlerFactory(IHandlerFactory)} 方法设置工厂，那么就会使用这个方法创建的默认工厂
					 */
					@Override
					protected VpnHandlerFactory newDefaultHandlerFactory() {
						return new VpnHandlerFactory();
					}
				};
				sAbsHelperSparseArray.put(IDENTIFY_VPN.hashCode(), absHelper);
			}
			return absHelper;
		}
	}
	
	/**
	 * 创建一个系统设置安全界面的自动点击辅助功能助手
	 *
	 * @return 一个系统设置安全界面的自动点击辅助功能助手对象
	 */
	public static AbsHelper<SecurityTarget, OnSecurityCallBack, SecurityHandlerFactory> securityHelper() {
		synchronized (sAbsHelperSparseArray) {
			
			AbsHelper absHelper = sAbsHelperSparseArray.get(IDENTIFY_SECURITY.hashCode());
			if (absHelper == null) {
				absHelper = new AbsHelper<SecurityTarget, OnSecurityCallBack, SecurityHandlerFactory>(IDENTIFY_SECURITY) {
					
					/**
					 * 创建一个默认的工厂对象，如果没有调用 {@link #withHandlerFactory(IHandlerFactory)} 方法设置工厂，那么就会使用这个方法创建的默认工厂
					 */
					@Override
					protected SecurityHandlerFactory newDefaultHandlerFactory() {
						return new SecurityHandlerFactory();
					}
				};
			}
			return absHelper;
		}
	}
	
	/**
	 * 创建一个APK安装/APP卸载的自动点击辅助功能助手
	 *
	 * @return 一个APK安装/APP卸载的自动点击辅助功能助手
	 */
	public static AbsHelper<ApkInstallTarget, OnApkInstallCallBack, ApkInstallHandlerFactory> apkInstallHelper() {
		synchronized (sAbsHelperSparseArray) {
			AbsHelper absHelper = sAbsHelperSparseArray.get(IDENTIFY_APK_INSTALL.hashCode());
			if (absHelper == null) {
				absHelper =
						new AbsHelper<ApkInstallTarget, OnApkInstallCallBack, ApkInstallHandlerFactory>(IDENTIFY_APK_INSTALL) {
							
							/**
							 * 创建一个默认的工厂对象，如果没有调用 {@link #withHandlerFactory(IHandlerFactory)} 方法设置工厂，那么就会使用这个方法创建的默认工厂
							 */
							@Override
							protected ApkInstallHandlerFactory newDefaultHandlerFactory() {
								return new ApkInstallHandlerFactory();
							}
						};
			}
			return absHelper;
		}
	}
	
}
