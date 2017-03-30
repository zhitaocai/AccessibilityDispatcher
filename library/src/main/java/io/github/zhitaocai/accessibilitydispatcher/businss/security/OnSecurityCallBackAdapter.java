package io.github.zhitaocai.accessibilitydispatcher.businss.security;

/**
 * @author zhitao
 * @since 2017-03-30 18:47
 */
public class OnSecurityCallBackAdapter implements OnSecurityCallBack {
	
	/**
	 * 点击了 未知来源 所在的item时的回调
	 */
	@Override
	public void onUnknownSourceItemClick() {
		
	}
	
	/**
	 * 点击了 未知来源 所在的item时
	 * <p>
	 * <ul>
	 * <li>如果为开启 未知来源 ，会有对话框弹出，这里为点击了对话框中的确认按钮时的回调</li>
	 * <li>如果为关闭 未知来源 ，则没有对话框弹出，所以也不会有这个回调</li>
	 * </ul>
	 */
	@Override
	public void onUnknownSourceDialogConfirm() {
		
	}
}
