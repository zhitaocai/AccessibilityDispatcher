package io.github.zhitaocai.accessibilitydispatcher.demo.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @author zhitao
 * @since 2017-02-02 18:38
 */
public class BasePagerAdapter extends FragmentPagerAdapter {
	
	private List<BaseFragment> mFragments;
	
	public BasePagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
		super(fm);
		mFragments = fragments;
	}
	
	@Override
	public Fragment getItem(int position) {
		return mFragments == null ? null : mFragments.get(position);
	}
	
	@Override
	public int getCount() {
		return mFragments == null ? 0 : mFragments.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (mFragments == null || mFragments.isEmpty()) {
			return "N/A";
		}
		return mFragments.get(position).getFragmentTitle();
	}
}
