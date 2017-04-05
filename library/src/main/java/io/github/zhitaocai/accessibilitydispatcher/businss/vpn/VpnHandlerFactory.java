package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.androidsettings.vpn.android.AndroidVpnSettingsASHandler444;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;

/**
 * @author zhitao
 * @since 2017-03-23 14:18
 */
public class VpnHandlerFactory implements IHandlerFactory {
	
	/**
	 * 根据机型 系统版本 系统类型等来创建具体的自动点击业务类
	 *
	 * @return 返回业务对象列表
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
		handlers.add(new AndroidVpnSettingsASHandler444());
		return handlers;
	}
}
