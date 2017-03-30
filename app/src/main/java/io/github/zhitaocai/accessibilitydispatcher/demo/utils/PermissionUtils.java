package io.github.zhitaocai.accessibilitydispatcher.demo.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-03-30 15:59
 */
public class PermissionUtils {

	/**
	 * 检查自身应用是否已经允许某个权限 support api 23+ @since 2015-12-09
	 *
	 * @param context
	 * @param permission see {@link android.Manifest}
	 *
	 * @return
	 */
	public static boolean isPermissionGranted(Context context, String permission) {
		return isPermissionGranted(context, context.getPackageName(), permission);
	}

	/**
	 * 检查某个应用是否已经允许某个权限 support api 23+ @since 2015-12-09
	 *
	 * @param context
	 * @param pkgName
	 * @param permission see {@link android.Manifest}
	 *
	 * @return
	 */
	public static boolean isPermissionGranted(Context context, String pkgName, String permission) {
		try {
			return PackageManager.PERMISSION_GRANTED == context.getPackageManager().checkPermission(permission, pkgName);
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}

	/**
	 * 获取开发者在AndroidManifest.xml文件中声明的所有权限信息，注意：仅仅是获取是否有没有在AndroidManifest.xml中配置，并不是是否已经被允许了
	 * {@link #isPermissionGranted(Context, String, String)} 方法是不能判断到下面这种权限的存在的
	 * <pre>
	 * <uses-permission
	 *     android:name="android.permission.PACKAGE_USAGE_STATS"
	 *     tools:ignore="ProtectedPermissions" />
	 * </pre>
	 * 因此就存在了这种一次性获取所有权限的，然后进行contains的方法来进行判断是否拥有某个权限的方法
	 *
	 * @param context
	 *
	 * @return
	 */
	public static List<String> getAllPermissionsDeclarateInAndroidManifest(Context context, String pkgName) {
		try {
			return Arrays.asList(context.getPackageManager()
			                            .getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS).requestedPermissions);
		} catch (PackageManager.NameNotFoundException e) {
			DLog.e(e);
		}
		return Collections.emptyList();
	}

	/**
	 * 是否开启了 未知来源
	 *
	 * @param context
	 *
	 * @return
	 */
	public static boolean isOpenUnknownSources(Context context) {
		boolean isOpenUnknownSources = false;
		try {
			isOpenUnknownSources =
					Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return isOpenUnknownSources;
	}

}
