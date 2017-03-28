package io.github.zhitaocai.accessibilitydispatcher;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2016-08-03 17:47
 */
public class AccessibilityServiceUtils {
	
	/**
	 * 触发系统rebind通知监听服务
	 *
	 * @return
	 */
	public static void toggleAccessibilityService(Context context, Class serviceClass) {
		PackageManager pm = context.getPackageManager();
		pm.setComponentEnabledSetting(
				new ComponentName(context, serviceClass),
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP
		);
		pm.setComponentEnabledSetting(
				new ComponentName(context, serviceClass),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP
		);
	}
	
	/**
	 * 判断当前应用的辅助功能服务是否开启
	 *
	 * @param context
	 */
	public static boolean isAccessibilityServiceOn(@NonNull Context context) {
		return isAccessibilityServiceOn(context, context.getPackageName());
	}
	
	/**
	 * 判断指定的应用的辅助功能是否开启
	 *
	 * @param context
	 * @param pkgName 要检查的服务所对应的app包名
	 *
	 * @return
	 */
	public static boolean isAccessibilityServiceOn(@NonNull Context context, @NonNull String pkgName) {
		int accessibilityEnabled = 0;
		try {
			accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
		} catch (Exception e) {
			DLog.e(e);
		}
		
		if (accessibilityEnabled == 1) {
			String services =
					Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (services != null) {
				return services.toLowerCase().contains(pkgName.toLowerCase());
			}
		}
		return false;
	}
	
	//	/**
	//	 * 判断指定的应用的辅助功能是否开启，不能判断自身的辅助功能服务是否开启
	//	 *
	//	 * @param context
	//	 * @param name    应用包名?应用服务名？
	//	 *
	//	 * @return
	//	 */
	//	@Deprecated
	//	public static boolean isAccessibilityServiceOn(@NonNull Context context, @NonNull String name) {
	//		AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
	//		List<AccessibilityServiceInfo> serviceInfos =
	//				am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
	//		List<AccessibilityServiceInfo> installedAccessibilityServiceList = am.getInstalledAccessibilityServiceList();
	//		for (AccessibilityServiceInfo info : installedAccessibilityServiceList) {
	//			if (name.equals(info.getId())) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	
}
