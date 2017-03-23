package io.github.zhitaocai.accessibilitydispatcher.businss;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;

/**
 * 自动点击业务类工厂
 *
 * @author zhitao
 * @since 2017-03-23 12:00
 */
public interface IHandlerFactory {
	
	/**
	 * 根据机型 系统版本 系统类型等来创建具体的自动点击业务类
	 *
	 * @return
	 */
	ArrayList<AbsASHandler> initHandlers();
	
}
