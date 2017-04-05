package io.github.zhitaocai.accessibilitydispatcher.demo.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 运用 linux 内核中的 oom-killer机制来判断前台进程包名
 *
 * @author zhitao
 * @since 2015-09-23 19:27
 */
public class PackageUtils {
	
	/**
	 * 所有已经安装的应用
	 */
	public final static int FLAG_INSTALLAPP_APP = 0;
	
	/**
	 * 系统应用
	 */
	public final static int FLAG_INSTALLAPP_SYSTEM_APP = 1;
	
	/**
	 * 第三方应用
	 */
	public final static int FLAG_INSTALLAPP_THIRD_APP = 2;
	
	/**
	 * 安装在sd卡的应用
	 */
	public final static int FLAG_INSTALLAPP_SDCARD_APP = 3;
	
	/**
	 * 获取已安装应用信息
	 *
	 * @param context
	 * @param flag    <ul>
	 *                <li>{@link #FLAG_INSTALLAPP_APP}</li>
	 *                <li>{@link #FLAG_INSTALLAPP_SYSTEM_APP}</li>
	 *                <li>{@link #FLAG_INSTALLAPP_THIRD_APP}</li>
	 *                <li>{@link #FLAG_INSTALLAPP_SDCARD_APP}</li>
	 *                <p/>
	 *                </ul>
	 *
	 * @return
	 */
	public static List<ApplicationInfo> getInstallApp(Context context, int flag) {
		try {
			PackageManager pm = context.getPackageManager();
			// 查询所有已经安装的应用程序
			List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			// 排序
			Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));
			// 保存过滤查到的AppInfo
			List<ApplicationInfo> list = new ArrayList<>();
			
			switch (flag) {
			// 所有应用
			case FLAG_INSTALLAPP_APP:
				for (ApplicationInfo app : listAppcations) {
					list.add(app);
				}
				break;
			// 系统应用
			case FLAG_INSTALLAPP_SYSTEM_APP:
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
						list.add(app);
					}
				}
				break;
			// 非系统应用
			case FLAG_INSTALLAPP_THIRD_APP:
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
						list.add(app);
					}
					//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
					else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
						list.add(app);
					}
				}
				break;
			// 安装在SD卡的应用
			case FLAG_INSTALLAPP_SDCARD_APP:
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) <= 0) {
						list.add(app);
					}
				}
				break;
			default:
				break;
			}
			return list;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	/**
	 * 获取已安装应用包名列表
	 *
	 * @param context
	 * @param flag    <ul>
	 *                <li>{@link #FLAG_INSTALLAPP_APP}</li>
	 *                <li>{@link #FLAG_INSTALLAPP_SYSTEM_APP}</li>
	 *                <li>{@link #FLAG_INSTALLAPP_THIRD_APP}</li>
	 *                <li>{@link #FLAG_INSTALLAPP_SDCARD_APP}</li>
	 *                <p/>
	 *                </ul>
	 *
	 * @return
	 */
	public static List<String> getInstallAppPkgNames(Context context, int flag) {
		try {
			PackageManager pm = context.getPackageManager();
			// 查询所有已经安装的应用程序
			List<ApplicationInfo> listAppcations = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			// 排序
			Collections.sort(listAppcations, new ApplicationInfo.DisplayNameComparator(pm));
			// 保存过滤查到的AppInfo
			List<String> list = new ArrayList<>();
			
			switch (flag) {
			// 所有应用
			case FLAG_INSTALLAPP_APP:
				for (ApplicationInfo app : listAppcations) {
					list.add(app.packageName);
				}
				break;
			// 系统应用
			case FLAG_INSTALLAPP_SYSTEM_APP:
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
						list.add(app.packageName);
					}
				}
				break;
			// 非系统应用
			case FLAG_INSTALLAPP_THIRD_APP:
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
						list.add(app.packageName);
					}
					//本来是系统程序，被用户手动更新后，该系统程序也成为第三方应用程序了
					else if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
						list.add(app.packageName);
					}
				}
				break;
			// 安装在SD卡的应用
			case FLAG_INSTALLAPP_SDCARD_APP:
				for (ApplicationInfo app : listAppcations) {
					if ((app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) <= 0) {
						list.add(app.packageName);
					}
				}
				break;
			default:
				break;
			}
			return list;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	/**
	 * 获取指定包名应用的版本号
	 *
	 * @param context
	 * @param pkgName
	 *
	 * @return
	 */
	public static int getPackageVersionCode(@NonNull Context context, @NonNull String pkgName) {
		try {
			return context.getPackageManager().getPackageInfo(pkgName, 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}
	
	/**
	 * 获取指定包名应用的版本名
	 *
	 * @param context
	 * @param pkgName
	 *
	 * @return
	 */
	public static String getPackageVersionName(@NonNull Context context, @NonNull String pkgName) {
		try {
			return context.getPackageManager().getPackageInfo(pkgName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			return null;
		}
	}
	
	/**
	 * 获取当前App的名字
	 *
	 * @param context
	 *
	 * @return
	 */
	public static String getCurrentAppName(@NonNull Context context) {
		try {
			return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
		} catch (Throwable e) {
			DLog.e(e);
			return null;
		}
	}
	
	/**
	 * 判断包名是否已经安装好
	 *
	 * @param context
	 * @param packageName
	 *
	 * @return
	 */
	public static boolean isPakcageInstall(@NonNull Context context, @NonNull String packageName) {
		try {
			return getPackageInfo(context, packageName) != null;
		} catch (Throwable e) {
			//DLog.w(e);
			return false;
		}
	}
	
	/**
	 * 从apk文件中获取包名信息
	 *
	 * @param context
	 * @param filePath
	 *
	 * @return
	 */
	public static PackageInfo getPackageInfoFromFilePath(@NonNull Context context, @NonNull String filePath) {
		try {
			return context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
		} catch (Throwable e) {
			DLog.e(e);
			return null;
		}
	}
	
	/**
	 * 获取指定包名的包名信息
	 *
	 * @param context
	 * @param packageName
	 *
	 * @return
	 */
	public static PackageInfo getPackageInfo(@NonNull Context context, @NonNull String packageName) {
		try {
			return context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (Throwable e) {
			//DLog.w(e, "这个 warning 可以忽略，仅仅是判断是否有安装这个包名");
			return null;
		}
	}
	
	//	public static String getTopPkgNameAboveAndroidLThroughUsageStatsManagerByReflect(Context context, long time_ms) {
	//
	//		// 通过Android 5.0 之后提供的新api来获取最近一段时间内的应用的相关信息
	//		String topPackageName = null;
	//
	//		if (Build.VERSION.SDK_INT < 21) {
	//			return null;
	//		}
	//		try {
	//
	//			long time = System.currentTimeMillis();
	//			Class UsageStatsManagerClass = Class.forName("android.app.usage.UsageStatsManager");
	//			Object UsageStatsManagerObject = context.getSystemService("usagestats");
	//			Method queryUsageStatsMethod = UsageStatsManagerClass.getMethod("queryUsageStats", int.class, long.class, long
	//					.class);
	//			List list = (List) queryUsageStatsMethod.invoke(UsageStatsManagerObject, 4, time - time_ms, time);
	//			if (DLog.isUtilLog) {
	//				DLog.td(DLog.mUtilTag, Util_System_Package.class, "反射结果:获取最近 %d ms内的应用信息有%d个", time_ms,
	//						list == null ? 0 : list.size());
	//			}
	//			if (list != null && !list.isEmpty()) {
	//
	//				Class UsageStatsClass = Class.forName("android.app.usage.UsageStats");
	//				Method getLastTimeUsedMethod = UsageStatsClass.getMethod("getLastTimeUsed");
	//				Method getPackageNameMethod = UsageStatsClass.getMethod("getPackageName");
	//
	//				SortedMap<Long, Object> runningTask = new TreeMap<Long, Object>();
	//				for (Object obj : list) {
	//					try {
	//						Object temp = getLastTimeUsedMethod.invoke(obj);
	//						if (temp == null) {
	//							continue;
	//						}
	//						runningTask.put(Long.valueOf(temp.toString()), obj);
	//					} catch (Throwable e) {
	//						if (DLog.isUtilLog) {
	//							DLog.te(DLog.mUtilTag, Util_System_Package.class, e);
	//
	//						}
	//					}
	//
	//				}
	//				if (!runningTask.isEmpty()) {
	//
	//					Object temp = getPackageNameMethod.invoke(runningTask.get(runningTask.lastKey()));
	//					topPackageName = temp == null ? null : temp.toString();
	//					if (DLog.isUtilLog) {
	//						DLog.td(DLog.mUtilTag, Util_System_Package.class, "##(反射方法获取)当前顶端应用包名:%s", topPackageName);
	//					}
	//				}
	//			}
	//		} catch (Throwable e) {
	//			if (DLog.isUtilLog) {
	//				DLog.te(DLog.mUtilTag, Util_System_Package.class, e);
	//
	//			}
	//		}
	//
	//		return topPackageName;
	//	}
	public static boolean canGetTopPkgNameByUsageStatsManager(Context context) {
		try {
			String pkgName = getTopPkgNameAboveAndroidLThroughUsageStatsManager(context, 100 * 1000);
			if (!TextUtils.isEmpty(pkgName)) {
				return true;
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
		
	}
	
	/**
	 * 获取当前在顶端运行的应用包名(适用于Andriod 5.0以上的机器)
	 * <p>
	 * 通过AndroidL的新API——UsageStatsManager来进行获取，获取到的结果十分准确，但是需要配置权限和需要用户允许获取
	 * </p>
	 * <p/>
	 * 使用本方法之前需要配置下面内容
	 * <pre>
	 * 1. 权限配置
	 *   a. 需要在AndroidManifest.xml中配置权限
	 *
	 * 	    < uses-permission
	 * 	        android:name="android.permission.PACKAGE_USAGE_STATS"
	 * 	        tools:ignore="ProtectedPermissions" />
	 *
	 *
	 *   b. 然后还要在AndroidManifest.xml中的manifest标签中配置
	 * 	    xmlns:tools="http://schemas.android.com/tools"
	 *
	 * 2. 需要用户允许这个应用能获取用户数据统计信息的权限
	 * 		Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
	 * 		startActivity(intent);
	 * </pre>
	 *
	 * @param time_ms 从${time_ms}内找出最近显示在顶端的包名<p>
	 *                这个时间不建议设置太短，假设设置为5秒的话，那么如果你在玩一个应用超过5s，那么之后的时间（如第6秒  第30秒 获取到的顶端包名会为空）
	 *
	 * @return
	 */
	public static String getTopPkgNameAboveAndroidLThroughUsageStatsManager(Context context, long time_ms) {
		
		// 通过Android 5.0 之后提供的新api来获取最近一段时间内的应用的相关信息
		String topPackageName = null;
		
		if (Build.VERSION.SDK_INT >= 21) {
			
			try {
				
				// 根据最近5秒内的应用统计信息进行排序获取当前顶端的包名
				long time = System.currentTimeMillis();
				UsageStatsManager usage = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
				List<UsageStats> usageStatsList = usage.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - time_ms, time);
				if (usageStatsList != null && usageStatsList.size() > 0) {
					SortedMap<Long, UsageStats> runningTask = new TreeMap<>();
					for (UsageStats usageStats : usageStatsList) {
						runningTask.put(usageStats.getLastTimeUsed(), usageStats);
					}
					if (runningTask.isEmpty()) {
						return null;
					}
					topPackageName = runningTask.get(runningTask.lastKey()).getPackageName();
					DLog.i("##当前顶端应用包名:%s", topPackageName);
				}
				
			} catch (Throwable e) {
				DLog.e(e);
			}
		}
		return topPackageName;
	}
	
}
