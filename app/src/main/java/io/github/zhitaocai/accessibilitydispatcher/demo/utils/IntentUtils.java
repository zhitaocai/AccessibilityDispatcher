package io.github.zhitaocai.accessibilitydispatcher.demo.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-01-03 15:48
 */
public class IntentUtils {
	
	/**
	 * 判断是否有可以接受的Activity
	 * <p>
	 * e.g.
	 * <p>
	 * 比如在魅族早期一点的5.x.x版本上，是没有 "允许查看应用使用权限的应用" 的页面（应该是被阉割掉了）
	 * 所以如果通过常规的intent方法去打开该页面是会出现异常的，这时如果通过这个方法就可以先判断是否存在可达的页面
	 */
	public static boolean isIntentAvailable(@NonNull Context context, @NonNull Intent intent) {
		return !context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
	}
	
	/**
	 * 打开本应用在设置程序中的详情页
	 *
	 * @param context
	 *
	 * @return
	 */
	@Nullable
	public static Intent getAppDetailSettingsIntent(@NonNull Context context) {
		return getAppDetailSettingsIntent(context, context.getPackageName());
	}
	
	/**
	 * 打开指定包名app在设置程序中的详情页
	 *
	 * @param context
	 * @param pkgName
	 *
	 * @return
	 */
	@Nullable
	public static Intent getAppDetailSettingsIntent(@NonNull Context context, @NonNull String pkgName) {
		try {
			Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + pkgName));
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			return intent;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	/**
	 * 获取从外部相册选择照片的Intent
	 *
	 * @param context
	 *
	 * @return
	 */
	@Nullable
	public static Intent getSystemPhotoChooseIntent(@NonNull Context context) {
		//		Intent intent = new Intent(Intent.ACTION_PICK);
		//		intent.setType("image/*");
		try {
			
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/jpeg");
			return intent;
			//			if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT){
			//				startActivityForResult(intent, SELECT_PIC_KITKAT);
			//			}else{
			//				startActivityForResult(intent, SELECT_PIC);
			//			}
			
			//			Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			//			return intent;
			
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	/**
	 * 打开系统相机的Intent
	 *
	 * @param context
	 *
	 * @return
	 */
	@Nullable
	public static Intent getOpenSystemCameraIntent(@NonNull Context context) {
		try {
			return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	/**
	 * 打开app的应用详细设置页面
	 *
	 * @param context
	 * @param pkgName
	 *
	 * @return
	 */
	public static void openAppDetailSettingPage(@NonNull Context context, @NonNull String pkgName) {
		try {
			context.startActivity(getAppDetailSettingsIntent(context, pkgName));
		} catch (ActivityNotFoundException e) {
			DLog.w("没有该activity,转为打开应用管理页面");
			//e.printStackTrace();
			//Open the generic Apps page:
			Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
			context.startActivity(intent);
		}
	}
	
	/**
	 * 打开指定应用
	 *
	 * @param context
	 * @param packageName
	 *
	 * @return
	 */
	public static boolean startActivityByPackageName(Context context, String packageName) {
		return startActivityByPackageName(context, packageName, -1);
	}
	
	/**
	 * 打开指定应用
	 *
	 * @param context
	 * @param packageName
	 * @param flags
	 *
	 * @return
	 */
	public static boolean startActivityByPackageName(Context context, String packageName, int flags) {
		try {
			PackageManager pm = context.getPackageManager();
			if (pm != null) {
				Intent intent = pm.getLaunchIntentForPackage(packageName);
				if (intent != null) {
					if (flags >= 0) {
						intent.addFlags(flags);
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					return true;
				}
			}
			
		} catch (Throwable e) {
			DLog.i(e);
		}
		return false;
	}
	
	/**
	 * 通过Uri的方式打开Activity，如果存在多个匹配对象就会有选择对话框
	 *
	 * @param context
	 * @param uri
	 * @param flags
	 * @param title
	 *
	 * @return
	 */
	public static boolean startActivityByUriWithChooser(Context context, String uri, int flags, String title) {
		try {
			Intent intent = Intent.parseUri(uri, flags);
			if (intent == null) {
				return false;
			}
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Intent startIntent = Intent.createChooser(intent, title);
			startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startIntent);
			return true;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 通过Uri的方式打开Activity
	 *
	 * @param context
	 * @param uri
	 * @param flags
	 *
	 * @return
	 */
	public static boolean startActivityByUri(Context context, String uri, int flags) {
		try {
			
			Intent intent = Intent.parseUri(uri, flags);
			if (intent == null) {
				return false;
			}
			
			PackageManager pm = context.getPackageManager();
			List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
			if (list == null) {
				return false;
			}
			
			if (list.size() <= 0) {
				return false;
			}
			
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 通过Uri启动Service
	 *
	 * @param context
	 * @param uri
	 * @param flags
	 *
	 * @return
	 */
	public static boolean startServiceByUri(Context context, String uri, int flags) {
		try {
			Intent intent = Intent.parseUri(uri, flags);
			if (intent == null) {
				return false;
			}
			context.startService(intent);
			return true;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 通过Uri关闭Service
	 *
	 * @param context
	 * @param uri
	 * @param flags
	 *
	 * @return
	 */
	public static boolean stopServiceByUri(Context context, String uri, int flags) {
		try {
			Intent intent = Intent.parseUri(uri, flags);
			if (intent == null) {
				return false;
			}
			return context.stopService(intent);
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 通过uri发送广播
	 *
	 * @param context
	 * @param uri
	 * @param flags
	 * @param receiverPermission
	 *
	 * @return
	 */
	public static boolean sendBroadcastByUri(Context context, String uri, int flags, String receiverPermission) {
		try {
			Intent intent = Intent.parseUri(uri, flags);
			if (intent == null) {
				return false;
			}
			if (receiverPermission != null) {
				context.sendBroadcast(intent, receiverPermission);
				return true;
			} else {
				context.sendBroadcast(intent);
				return true;
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 通过uri发送广播
	 *
	 * @param context
	 * @param uri
	 * @param flags
	 *
	 * @return
	 */
	public static boolean sendBroadcastByUri(Context context, String uri, int flags) {
		return sendBroadcastByUri(context, uri, flags, null);
	}
	
	/**
	 * 删除指定应用的桌面快捷方式
	 *
	 * @param context
	 */
	public static boolean uninstallShortcut(Context context, String pkgName) {
		
		try {
			
			Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
			
			PackageManager pm = context.getApplicationContext().getPackageManager();
			String title = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)).toString();
			Intent shortcutIntent = pm.getLaunchIntentForPackage(pkgName);
			
			// 快捷方式名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			// 获取需要删除的快捷方式的启动Intent
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			
			context.sendBroadcast(shortcut);
			return true;
		} catch (Exception e) {
			DLog.e(e);
			return false;
		}
		
	}
	
	/**
	 * 获取打开应用安装界面的Intent
	 *
	 * @param context
	 * @param fileProviderAuthority Android N上需要说需要配置的FileProvider的 authority
	 * @param file                  需要安装的apk文件
	 *
	 * @return
	 */
	public static Intent getIntentForInstallApk(@NonNull Context context, @NonNull String fileProviderAuthority,
			@NonNull File file) {
		if (!file.exists()) {
			return null;
		}
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				uri = FileProvider.getUriForFile(context, fileProviderAuthority, file);
				intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			} else {
				uri = Uri.fromFile(file);
			}
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			return intent;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return null;
	}
	
	/**
	 * 打开apk安装界面，安装apk
	 *
	 * @param context
	 * @param fileProviderAuthority
	 * @param file                  要安装的apk文件
	 * @param flags                 add intent flags
	 */
	public static boolean startActivity2InstallApk(@NonNull Context context, @NonNull String fileProviderAuthority,
			@NonNull File file, int flags) {
		try {
			Intent intent = getIntentForInstallApk(context, fileProviderAuthority, file);
			intent.addFlags(flags);
			if (intent != null) {
				context.startActivity(intent);
				return true;
			}
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
		
	}
	
	/**
	 * 打开apk安装界面，安装apk
	 *
	 * @param context
	 * @param fileProviderAuthority
	 * @param file                  要安装的apk文件
	 */
	public static void startActivity2InstallApk(@NonNull Context context, @NonNull String fileProviderAuthority,
			@NonNull File file) {
		startActivity2InstallApk(context, fileProviderAuthority, file, Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	/**
	 * 打开apk安装界面，安装apk
	 *
	 * @param context
	 * @param fileProviderAuthority
	 * @param apkFilePath           要安装的apk文件
	 * @param flags                 add intent flags
	 */
	public static boolean startActivity2InstallApk(@NonNull Context context, @NonNull String fileProviderAuthority,
			@NonNull String apkFilePath, int flags) {
		return startActivity2InstallApk(context, fileProviderAuthority, new File(apkFilePath), flags);
	}
	
	/**
	 * 打开apk安装界面，安装apk
	 *
	 * @param context
	 * @param fileProviderAuthority
	 * @param apkFilePath           要安装的apk文件
	 */
	public static void startActivity2InstallApk(@NonNull Context context, @NonNull String fileProviderAuthority,
			@NonNull String apkFilePath) {
		startActivity2InstallApk(context, fileProviderAuthority, apkFilePath, Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	/**
	 * 打开apk卸载界面，进行卸载apk
	 *
	 * @param context
	 * @param pkgName
	 */
	public static void startActivity2UninstallApk(@NonNull Context context, String pkgName) {
		startActivity2UninstallApk(context, pkgName, Intent.FLAG_ACTIVITY_NEW_TASK);
	}
	
	/**
	 * 打开apk卸载界面，进行卸载apk
	 *
	 * @param context
	 * @param pkgName
	 */
	public static void startActivity2UninstallApk(@NonNull Context context, String pkgName, int flags) {
		try {
			Intent intent = getDeleteApkIntent(pkgName);
			intent.addFlags(flags);
			context.getApplicationContext().startActivity(intent);
		} catch (Throwable e) {
			DLog.e(e);
		}
	}
	
	public static Intent getDeleteApkIntent(@NonNull String pkgName) {
		Uri uri = Uri.parse("package:" + pkgName);
		return new Intent(Intent.ACTION_DELETE, uri);
		
	}
	
	/**
	 * 回到桌面
	 *
	 * @param context
	 *
	 * @return
	 */
	public static boolean goHome(Context context) {
		try {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			context.startActivity(intent);
			return true;
		} catch (Throwable e) {
			DLog.e(e);
		}
		return false;
	}
	
	/**
	 * 进入设置-安全界面
	 *
	 * @param context 上下文
	 * @param flag    -1为不添加额外的flag
	 */
	public static void startActivity2SecuritySettings(@NonNull Context context, int flag) {
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		if (flag != -1) {
			intent.addFlags(flag);
		}
		context.startActivity(intent);
	}
	
	/**
	 * 进入设置-安装界面
	 *
	 * @param context 上下文
	 */
	public static void startActivity2SecuritySettings(@NonNull Context context) {
		startActivity2SecuritySettings(context, -1);
	}
	
}
