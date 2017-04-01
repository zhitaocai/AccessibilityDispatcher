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
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.OnApkInstallCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.OnSecurityCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.OnSecurityCallBackAdapter;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;
import io.github.zhitaocai.accessibilitydispatcher.demo.BuildConfig;
import io.github.zhitaocai.accessibilitydispatcher.demo.R;
import io.github.zhitaocai.accessibilitydispatcher.demo.utils.FileUtils;
import io.github.zhitaocai.accessibilitydispatcher.demo.utils.PackageUtils;
import io.github.zhitaocai.accessibilitydispatcher.demo.utils.PermissionUtils;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-03-30 14:29
 */
public class AutoApkInstallFragment extends BaseFragment {
	
	private final static int REQ_SECURITY_UNKNOWN_SOURCES = 200;
	
	private final static int REQ_APK_INSTALL = 201;
	
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
			SecurityHelper.getInstance().reset().active();
			break;
		case REQ_APK_INSTALL:
			ApkInstallHelper.getInstance().reset().active();
			break;
		default:
			break;
		}
	}
	
	@OnClick(R.id.btn_turn_on_unknownsource)
	public void turnOnUnKnownSources() {
		if (PermissionUtils.isOpenUnknownSources(getActivity())) {
			Toast.makeText(getActivity(), "已经开启了允许安装位置来源的开关", Toast.LENGTH_SHORT).show();
			return;
		}
		
		SecurityHelper.getInstance()
		              .setTarget(new SecurityTarget.Builder().setAction(SecurityTarget.ACTION_TURN_ON_UNKNOWNSOURCES).build())
		              .setCallBack(new OnSecurityCallBack() {
			              @Override
			              public void onUnknownSourceItemClick() {
				              Toast.makeText(getActivity(), String.format(Locale.getDefault(),
						              "%1$tH:%1$tM:%1$tS 点击了\"未知来源\"所在的item",
						              System.currentTimeMillis()
				              ), Toast.LENGTH_SHORT).show();
			              }
			
			              @Override
			              public void onUnknownSourceDialogConfirm() {
				              Toast.makeText(getActivity(), String.format(Locale.getDefault(),
						              "%1$tH:%1$tM:%1$tS 点击了开启\"未知来源\"时对话框中的确认按钮",
						              System.currentTimeMillis()
				              ), Toast.LENGTH_SHORT).show();
			              }
		              })
		              .setEnable(true)
		              .active();
		
		startActivity2SecuritySettings();
	}
	
	@OnClick(R.id.btn_turn_off_unknownsource)
	public void turnOffUnKnownSources() {
		if (!PermissionUtils.isOpenUnknownSources(getActivity())) {
			Toast.makeText(getActivity(), "已经关闭了允许安装位置来源的开关", Toast.LENGTH_SHORT).show();
			return;
		}
		
		SecurityHelper.getInstance()
		              .setTarget(new SecurityTarget.Builder().setAction(SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES).build())
		              .setCallBack(new OnSecurityCallBackAdapter() {
			              /**
			               * 点击了 未知来源 所在的item时的回调
			               */
			              @Override
			              public void onUnknownSourceItemClick() {
				              Toast.makeText(
						              getActivity(),
						              String.format(Locale.getDefault(),
								              "%1$tH:%1$tM:%1$tS 点击了\"未知来源\"所在的item",
								              System.currentTimeMillis()
						              ),
						              Toast.LENGTH_SHORT
				              ).show();
			              }
		              })
		              .setEnable(true)
		              .active();
		
		startActivity2SecuritySettings();
	}
	
	private void startActivity2SecuritySettings() {
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		startActivityForResult(intent, REQ_SECURITY_UNKNOWN_SOURCES);
	}
	
	@OnClick(R.id.btn_auto_install_apk)
	public void autoInstallApk() {
		
		final Context applicationContext = getActivity().getApplicationContext();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (PackageUtils.isPakcageInstall(applicationContext, "io.github.zhitaocai.accessibilitydispatcher.targetapk")) {
					return;
				}
				
				File file = new File(applicationContext.getExternalCacheDir(), "targetapk-debug.apk");
				FileUtils.delete(file);
				file = FileUtils.getVaildFile(file);
				DLog.i("file : %s", file == null ? "null" : file.getAbsolutePath());
				if (file == null) {
					return;
				}
				FileUtils.cpFromAssets(applicationContext, "targetapk-debug.apk", file);
				ApkInstallHelper.getInstance()
				                .setTarget(new ApkInstallTarget.Builder().setAppName("TargetApk")
				                                                         .setAction(ApkInstallTarget.ACTION_AUTO_INSTALL |
				                                                                    ApkInstallTarget.ACTION_WAIT_INSTALLING |
				                                                                    ApkInstallTarget.ACTION_CLICK_FINISH)
				                                                         .build())
				                .setCallBack(new OnApkInstallCallBack() {
					                @Override
					                public void onApkInstallBtnClick(ApkInstallTarget target) {
						                Toast.makeText(getActivity(), String.format(Locale.getDefault(),
								                "%1$tH:%1$tM:%1$tS 点击了\"下一步/安装\"按钮",
								                System.currentTimeMillis()
						                ), Toast.LENGTH_SHORT).show();
					                }
					
					                @Override
					                public void onApkInstalling(ApkInstallTarget target) {
						                Toast.makeText(getActivity(),
								                String.format(Locale.getDefault(),
										                "%1$tH:%1$tM:%1$tS 安装中",
										                System.currentTimeMillis()
								                ),
								                Toast.LENGTH_SHORT
						                ).show();
					                }
					
					                @Override
					                public void onApkInstallFinish(ApkInstallTarget target) {
						                Toast.makeText(getActivity(), String.format(Locale.getDefault(),
								                "%1$tH:%1$tM:%1$tS 点击了安装完成界面中的\"完成\"",
								                System.currentTimeMillis()
						                ), Toast.LENGTH_SHORT).show();
					                }
					
					                @Override
					                public void onApkInstallLaunch(ApkInstallTarget target) {
						                Toast.makeText(getActivity(), String.format(Locale.getDefault(),
								                "%1$tH:%1$tM:%1$tS 点击了安装完成界面中的\"打开\"",
								                System.currentTimeMillis()
						                ), Toast.LENGTH_SHORT).show();
					                }
					
					                @Override
					                public void onApkUninstallConfirm(ApkInstallTarget target) {
						                Toast.makeText(getActivity(), String.format(Locale.getDefault(),
								                "%1$tH:%1$tM:%1$tS 点击了卸载界面中的\"确认\"",
								                System.currentTimeMillis()
						                ), Toast.LENGTH_SHORT).show();
					                }
					
					                @Override
					                public void onApkUninstallCancel(ApkInstallTarget target) {
						                Toast.makeText(getActivity(), String.format(Locale.getDefault(),
								                "%1$tH:%1$tM:%1$tS 点击了卸载界面中的\"取消\"",
								                System.currentTimeMillis()
						                ), Toast.LENGTH_SHORT).show();
					                }
				                })
				                .setEnable(true)
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
				//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivityForResult(intent, REQ_APK_INSTALL);
			}
		}).start();
	}
	
	@OnClick(R.id.btn_auto_uninstall_app)
	public void autoUninstallApp() {
		
	}
	
	@OnClick(R.id.btn_refuse_to_uninstall_app)
	public void refuse2UninstallApp() {
		
	}
}