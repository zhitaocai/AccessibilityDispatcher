package io.github.zhitaocai.accessibilitydispatcher.demo.fragment;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-03-30 15:03
 */
public abstract class BaseFragment extends Fragment {
	
	/**
	 * tab 所显示的title
	 *
	 * @return
	 */
	@Nullable
	public abstract String getFragmentTitle();
}
