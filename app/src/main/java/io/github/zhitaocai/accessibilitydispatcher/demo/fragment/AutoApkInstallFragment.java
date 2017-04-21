package io.github.zhitaocai.accessibilitydispatcher.demo.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.zhitaocai.accessibilitydispatcher.AccessibilityHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.OnApkInstallCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.OnApkInstallCallBackAdapter;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.OnSecurityCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.OnSecurityCallBackAdapter;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;
import io.github.zhitaocai.accessibilitydispatcher.demo.BuildConfig;
import io.github.zhitaocai.accessibilitydispatcher.demo.R;
import io.github.zhitaocai.accessibilitydispatcher.demo.utils.FileUtils;
import io.github.zhitaocai.accessibilitydispatcher.demo.utils.PermissionUtils;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

import static io.github.zhitaocai.accessibilitydispatcher.demo.utils.PackageUtils.isPakcageInstall;

/**
 * @author zhitao
 * @since 2017-03-30 14:29
 */
public class AutoApkInstallFragment extends BaseFragment {
	
	private final static int REQ_SECURITY_UNKNOWN_SOURCES = 200;
	
	private final static int REQ_APK_INSTALL = 201;
	
	private final static String TEST_APP_NAME = "TargetApk";
	
	private final static String TEST_APP_PKG_NAME = "io.github.zhitaocai.accessibilitydispatcher.targetapk";
	
	private final static String TEST_APP_FILE_NAME_IN_ASSETS = "targetapk-debug.apk";
	
	private Unbinder mUnBinder;
	
	@Override
	public String getFragmentTitle() {
		return "自动安装";
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_install, container, false);
		mUnBinder = ButterKnife.bind(this, view);
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mUnBinder.unbind();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_SECURITY_UNKNOWN_SOURCES:
			AccessibilityHelper.securityHelper().reset().active();
			break;
		case REQ_APK_INSTALL:
			AccessibilityHelper.apkInstallHelper().reset().active();
			break;
		default:
			break;
		}
	}
	
	@OnClick(R.id.btn_turn_on_unknownsource)
	public void turnOnUnKnownSources() {
		if (PermissionUtils.isOpenUnknownSources(getActivity())) {
			toast("已经开启了允许安装位置来源的开关");
			return;
		}
		
		AccessibilityHelper.securityHelper()
		                   .withTargets(new SecurityTarget.Builder().setAction(SecurityTarget.ACTION_TURN_ON_UNKNOWNSOURCES)
		                                                            .build())
		                   .withCallBacks(new OnSecurityCallBack() {
			                   @Override
			                   public void onUnknownSourceItemClick() {
				                   toast("%1$tH:%1$tM:%1$tS 点击了\"未知来源\"所在的item", System.currentTimeMillis());
			                   }
			
			                   @Override
			                   public void onUnknownSourceDialogConfirm() {
				                   toast("%1$tH:%1$tM:%1$tS 点击了开启\"未知来源\"时对话框中的确认按钮", System.currentTimeMillis());
			                   }
		                   })
		                   .enable(true)
		                   .active();
		
		startActivity2SecuritySettings();
	}
	
	@OnClick(R.id.btn_turn_off_unknownsource)
	public void turnOffUnKnownSources() {
		if (!PermissionUtils.isOpenUnknownSources(getActivity())) {
			toast("已经关闭了允许安装位置来源的开关");
			return;
		}
		
		AccessibilityHelper.securityHelper()
		                   .withTargets(new SecurityTarget.Builder().setAction(SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES)
		                                                            .build())
		                   .withCallBacks(new OnSecurityCallBackAdapter() {
			                   /**
			                    * 点击了 未知来源 所在的item时的回调
			                    */
			                   @Override
			                   public void onUnknownSourceItemClick() {
				                   toast("%1$tH:%1$tM:%1$tS 点击了\"未知来源\"所在的item", System.currentTimeMillis());
			                   }
		                   })
		                   .enable(true)
		                   .active();
		
		startActivity2SecuritySettings();
	}
	
	private void startActivity2SecuritySettings() {
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, REQ_SECURITY_UNKNOWN_SOURCES);
	}
	
	@OnClick({
			         R.id.btn_auto_install_apk_untill_installing, R.id.btn_auto_install_apk_untill_finish,
			         R.id.btn_auto_install_apk_untill_open
	         })
	public void autoInstallApk(View view) {
		switch (view.getId()) {
		case R.id.btn_auto_install_apk_untill_installing:
			installApk(ApkInstallTarget.ACTION_AUTO_INSTALL);
			break;
		case R.id.btn_auto_install_apk_untill_finish:
			installApk(ApkInstallTarget.ACTION_AUTO_INSTALL | ApkInstallTarget.ACTION_WAIT_INSTALLING |
			           ApkInstallTarget.ACTION_CLICK_FINISH);
			break;
		case R.id.btn_auto_install_apk_untill_open:
			installApk(ApkInstallTarget.ACTION_AUTO_INSTALL | ApkInstallTarget.ACTION_WAIT_INSTALLING |
			           ApkInstallTarget.ACTION_CLICK_OPEN);
			break;
		default:
			break;
		}
	}
	
	private void installApk(final int actions) {
		final Context applicationContext = getActivity().getApplicationContext();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 先检查是否已经安装，没有就从assets中复制到手机中，然后在安装
				if (isPakcageInstall(applicationContext, TEST_APP_PKG_NAME)) {
					toast("目标应用已经安装");
					return;
				}
				
				File file = new File(applicationContext.getExternalCacheDir(), TEST_APP_FILE_NAME_IN_ASSETS);
				FileUtils.delete(file);
				file = FileUtils.getVaildFile(file);
				DLog.i("file : %s", file == null ? "null" : file.getAbsolutePath());
				if (file == null) {
					return;
				}
				FileUtils.cpFromAssets(applicationContext, TEST_APP_FILE_NAME_IN_ASSETS, file);
				
				// 安装之前配置自动点击行为
				AccessibilityHelper.apkInstallHelper()
				                   .withTargets(new ApkInstallTarget.Builder().setAppName(TEST_APP_NAME)
				                                                              .setAction(actions)
				                                                              .build())
				                   .withCallBacks(new OnApkInstallCallBack() {
					                   @Override
					                   public void onApkInstallBtnClick(ApkInstallTarget target) {
						                   toast("%1$tH:%1$tM:%1$tS 点击了\"下一步/安装\"按钮", System.currentTimeMillis());
					                   }
					
					                   @Override
					                   public void onApkInstalling(ApkInstallTarget target) {
						                   toast("%1$tH:%1$tM:%1$tS 安装中", System.currentTimeMillis());
					                   }
					
					                   @Override
					                   public void onApkInstallFinish(ApkInstallTarget target) {
						                   toast("%1$tH:%1$tM:%1$tS 点击了安装完成界面中的\"完成\"", System.currentTimeMillis());
					                   }
					
					                   @Override
					                   public void onApkInstallLaunch(ApkInstallTarget target) {
						                   toast("%1$tH:%1$tM:%1$tS 点击了安装完成界面中的\"打开\"", System.currentTimeMillis());
					                   }
					
					                   @Override
					                   public void onApkUninstallConfirm(ApkInstallTarget target) {
						                   toast("%1$tH:%1$tM:%1$tS 点击了卸载界面中的\"确认\"", System.currentTimeMillis());
					                   }
					
					                   @Override
					                   public void onApkUninstallCancel(ApkInstallTarget target) {
						                   toast("%1$tH:%1$tM:%1$tS 点击了卸载界面中的\"取消\"", System.currentTimeMillis());
					                   }
				                   })
				                   .enable(true)
				                   .active();
				
				Intent intent = new Intent(Intent.ACTION_VIEW);
				Uri uri;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					uri = FileProvider.getUriForFile(applicationContext, BuildConfig.APPLICATION_ID + ".fileprovider", file);
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				} else {
					uri = Uri.fromFile(file);
				}
				intent.setDataAndType(uri, "application/vnd.android.package-archive");
				startActivityForResult(intent, REQ_APK_INSTALL);
			}
		}).start();
	}
	
	@OnClick(R.id.btn_auto_uninstall_app)
	public void autoUninstallApp() {
		final Context applicationContext = getActivity().getApplicationContext();
		if (!isPakcageInstall(applicationContext, TEST_APP_PKG_NAME)) {
			toast("目标应用还没有安装");
			return;
		}
		AccessibilityHelper.apkInstallHelper()
		                   .withTargets(new ApkInstallTarget.Builder().setAppName(TEST_APP_NAME)
		                                                              .setAction(ApkInstallTarget.ACTION_AUTO_DELETE)
		                                                              .build())
		                   .withCallBacks(new OnApkInstallCallBackAdapter() {
			
			                   /**
			                    * 点击了apk卸载界面中"确认"按钮
			                    *
			                    * @param target 目标应用
			                    */
			                   @Override
			                   public void onApkUninstallConfirm(ApkInstallTarget target) {
				                   toast("%1$tH:%1$tM:%1$tS 点击了卸载界面中的\"确定\"", System.currentTimeMillis());
			                   }
		                   })
		                   .enable(true)
		                   .active();
		
		Uri uri = Uri.parse("package:" + TEST_APP_PKG_NAME);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		startActivityForResult(intent, REQ_APK_INSTALL);
	}
	
	@OnClick(R.id.btn_refuse_to_uninstall_app)
	public void refuse2UninstallApp() {
		final Context applicationContext = getActivity().getApplicationContext();
		if (!isPakcageInstall(applicationContext, TEST_APP_PKG_NAME)) {
			toast("目标应用还没有安装");
			return;
		}
		AccessibilityHelper.apkInstallHelper()
		                   .withTargets(new ApkInstallTarget.Builder().setAppName(TEST_APP_NAME)
		                                                              .setAction(ApkInstallTarget.ACTION_CAN_NOT_DELETE)
		                                                              .build())
		                   .withCallBacks(new OnApkInstallCallBackAdapter() {
			
			                   /**
			                    * 点击了apk卸载界面中"取消"按钮
			                    *
			                    * @param target 目标应用
			                    */
			                   @Override
			                   public void onApkUninstallCancel(ApkInstallTarget target) {
				                   toast("%1$tH:%1$tM:%1$tS 点击了卸载界面中的\"取消\"", System.currentTimeMillis());
			                   }
			
		                   })
		                   .enable(true)
		                   .active();
		
		Uri uri = Uri.parse("package:" + TEST_APP_PKG_NAME);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		startActivityForResult(intent, REQ_APK_INSTALL);
	}
}