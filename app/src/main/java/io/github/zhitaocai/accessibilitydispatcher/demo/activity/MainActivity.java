package io.github.zhitaocai.accessibilitydispatcher.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zhitaocai.accessibilitydispatcher.AccessibilityDispatcher;
import io.github.zhitaocai.accessibilitydispatcher.AccessibilityServiceUtils;
import io.github.zhitaocai.accessibilitydispatcher.demo.R;
import io.github.zhitaocai.accessibilitydispatcher.demo.fragment.AutoApkInstallFragment;
import io.github.zhitaocai.accessibilitydispatcher.demo.fragment.AutoVpnConfigFragment;
import io.github.zhitaocai.accessibilitydispatcher.demo.fragment.BaseFragment;
import io.github.zhitaocai.accessibilitydispatcher.demo.fragment.BasePagerAdapter;

public class MainActivity extends AppCompatActivity {
	
	private final static int REQ_ACCESSIBILITY_SERVICE_TURN_ON = 1;
	
	@BindView(R.id.switch_open_accessibility) SwitchCompat mSwitchCompat;
	
	@BindView(R.id.toolbar) Toolbar mToolbar;
	
	@BindView(R.id.tab_layout) TabLayout mTabLayout;
	
	@BindView(R.id.viewpager) ViewPager mViewpager;
	
	private BasePagerAdapter mViewPagerAdapter;
	
	private AutoApkInstallFragment mAutoApkInstallFragment;
	
	private AutoVpnConfigFragment mAutoVpnConfigFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		
		initToolbar();
		updateSwitchCompat();
		initViewPageAndTabLayout();
		
		// 开启调试log
		AccessibilityDispatcher.debugLog(true).withEventSourceLog(false).withClassNameInTag(false);
		
	}
	
	private void initToolbar() {
		mToolbar.setTitle("AccessibilityDispatcher");
	}
	
	private void updateSwitchCompat() {
		boolean isAccessibilityServiceOn = AccessibilityServiceUtils.isAccessibilityServiceOn(this);
		Toast.makeText(
				this,
				String.format(Locale.getDefault(), "辅助功能%s", isAccessibilityServiceOn ? "已经开启" : "还没有开启"),
				Toast.LENGTH_SHORT
		).show();
		mSwitchCompat.setChecked(isAccessibilityServiceOn);
	}
	
	private void initViewPageAndTabLayout() {
		mAutoVpnConfigFragment = new AutoVpnConfigFragment();
		mAutoApkInstallFragment = new AutoApkInstallFragment();
		List<BaseFragment> mLists = new ArrayList<>();
		mLists.add(mAutoVpnConfigFragment);
		mLists.add(mAutoApkInstallFragment);
		mViewPagerAdapter = new BasePagerAdapter(getSupportFragmentManager(), mLists);
		mViewpager.setAdapter(mViewPagerAdapter);
		mViewpager.setCurrentItem(0);
		mTabLayout.setupWithViewPager(mViewpager);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQ_ACCESSIBILITY_SERVICE_TURN_ON:
			updateSwitchCompat();
			break;
		default:
			break;
		}
	}
	
	@OnClick(R.id.switch_open_accessibility)
	protected void openAccessibility() {
		startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), REQ_ACCESSIBILITY_SERVICE_TURN_ON);
	}
	
}

