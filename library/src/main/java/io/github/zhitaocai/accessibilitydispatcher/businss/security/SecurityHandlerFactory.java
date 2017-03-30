package io.github.zhitaocai.accessibilitydispatcher.businss.security;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.androidsettings.security.unknownsources.fuzzy.UnknownSourcesFuzzyASHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;

/**
 * @author zhitao
 * @since 2017-03-30 11:51
 */
public class SecurityHandlerFactory implements IHandlerFactory {
	
	/**
	 * 根据机型 系统版本 系统类型等来创建具体的自动点击业务类
	 *
	 * @return 返回业务对象列表
	 */
	@Override
	public ArrayList<AbsASHandler> initHandlers() {
		ArrayList<AbsASHandler> handlers = new ArrayList<>();
		
		// 先加入文字搜索的处理，成功率难说，但是这种方案可能兼容更多的版本
		handlers.add(new UnknownSourcesFuzzyASHandler());
		
//		// 后续再加入更加精准的处理，成功率提高了，但是适配的版本比较局限
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//			handlers.add(new AndroidUnknownSourcesASHandler700());
//		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//			handlers.add(new AndroidUnknownSourcesASHandler510());
//		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			handlers.add(new AndroidUnknownSourcesASHandler444());
//		}
		return handlers;
	}
}
