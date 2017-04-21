package io.github.zhitaocai.accessibilitydispatcher;

import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnHelper;

/**
 * @author zhitao
 * @since 2017-04-21 10:31
 */
public class AccessibilityHelper {
	
	/**
	 * 创建一个VPN自动配置的辅助功能助手
	 *
	 * @return 一个VPN自动配置的辅助功能助手对象
	 */
	public static VpnHelper newVpnHelper() {
		return new VpnHelper();
	}
	
	/**
	 * 创建一个系统设置安全界面的自动点击辅助功能助手
	 *
	 * @return 一个系统设置安全界面的自动点击辅助功能助手对象
	 */
	public static SecurityHelper newSecurityHelper() {
		return new SecurityHelper();
	}
	
	/**
	 * 创建一个APK安装/APP卸载的自动点击辅助功能助手
	 *
	 * @return 一个APK安装/APP卸载的自动点击辅助功能助手
	 */
	public static ApkInstallHelper newApkInstallHelper() {
		return new ApkInstallHelper();
	}
}
