package io.github.zhitaocai.accessibilitydispatcher.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityHelper;
import io.github.zhitaocai.accessibilitydispatcher.businss.security.SecurityTarget;
import io.github.zhitaocai.accessibilitydispatcher.demo.R;
import io.github.zhitaocai.accessibilitydispatcher.demo.utils.PermissionUtils;

/**
 * @author zhitao
 * @since 2017-03-30 14:29
 */
public class AutoInstallFragment extends BaseFragment {
	
	private final static int REQ_SECURITY_UNKNOWN_SOURCES = 100;
	
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
		              .setEnable(true)
		              .active();
		
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		getActivity().startActivityForResult(intent, REQ_SECURITY_UNKNOWN_SOURCES);
	}
	
	@OnClick(R.id.btn_turn_off_unknownsource)
	public void turnOffUnKnownSources() {
		if (!PermissionUtils.isOpenUnknownSources(getActivity())) {
			Toast.makeText(getActivity(), "已经关闭了允许安装位置来源的开关", Toast.LENGTH_SHORT).show();
			return;
		}
		
		SecurityHelper.getInstance()
		              .setTarget(new SecurityTarget.Builder().setAction(SecurityTarget.ACTION_TURN_OFF_UNKNOWNSOURCES).build())
		              .setEnable(true)
		              .active();
		
		Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		intent.addFlags(
				Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		getActivity().startActivityForResult(intent, REQ_SECURITY_UNKNOWN_SOURCES);
		
	}
	
	@OnClick(R.id.btn_auto_install_apk)
	public void autoInstallApk() {
		
	}
	
	@OnClick(R.id.btn_auto_uninstall_app)
	public void autoUninstallApp() {
		
	}
	
	@OnClick(R.id.btn_refuse_to_uninstall_app)
	public void refuse2UninstallApp() {
		
	}
}