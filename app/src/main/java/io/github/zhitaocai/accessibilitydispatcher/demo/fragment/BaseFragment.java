package io.github.zhitaocai.accessibilitydispatcher.demo.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.Locale;

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
	
	protected void runOnUiThread(Runnable runnable) {
		getActivity().runOnUiThread(runnable);
	}
	
	protected void toast(final String format, final Object... args) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(), String.format(Locale.getDefault(), format, args), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
