package io.github.zhitaocai.accessibilitydispatcher.businss;

/**
 * @author zhitao
 * @since 2017-03-23 11:15
 */
public interface ITarget {
	
	/**
	 * 建立一些规则来判断每个传入来的目标是否有效
	 *
	 * @return 目标是否有效
	 */
	boolean isValid();
	
}
