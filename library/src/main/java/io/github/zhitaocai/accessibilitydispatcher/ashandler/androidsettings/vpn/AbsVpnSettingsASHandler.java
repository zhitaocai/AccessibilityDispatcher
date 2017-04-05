package io.github.zhitaocai.accessibilitydispatcher.ashandler.androidsettings.vpn;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.androidsettings.AndroidSettingsCompat;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.OnVpnCallBack;
import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;

/**
 * @author zhitao
 * @since 2017-03-23 10:59
 */
public abstract class AbsVpnSettingsASHandler extends AbsASHandler<VpnTarget, OnVpnCallBack> {
	
	/**
	 * 具体实现类的辅助功能所针对的应用包名
	 * <p>
	 * e.g.
	 * <p>
	 * 如果需要改动系统设置，那么这里的包名可能为 com.android.settings 或者 其他第三方系统所对应的包名
	 *
	 * @return 具体实现类的辅助功能所针对的应用包名
	 *
	 * @see #isUsingPkgName2TrackEvent()
	 */
	@Override
	protected String getSupportPkgName() {
		return AndroidSettingsCompat.ANDROID_PKGNAME;
	}
}
