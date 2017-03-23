package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;
import io.github.zhitaocai.accessibilitydispatcher.androidsettings.vpn.android.AndroidVpnSettingsASHandler;

/**
 * @author zhitao
 * @since 2017-03-23 14:18
 */
public class VpnHandlerFactory implements IHandlerFactory {
	
	/**
	 * 根据机型 系统版本 系统类型等来创建具体的自动点击业务类
	 *
	 * @return
	 */
	@Override
	public ArrayList<AbsASHandler> initHandlers() {
		ArrayList<AbsASHandler> handlers = new ArrayList<>();
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
		//			handlers.add(new AndroidAccessibilityPageASHandler510());
		//			return handlers;
		//		}
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		//			handlers.add(new AndroidAccessibilityPageASHandler444());
		//			return handlers;
		//		}
		handlers.add(new AndroidVpnSettingsASHandler());
		return handlers;
	}
}
