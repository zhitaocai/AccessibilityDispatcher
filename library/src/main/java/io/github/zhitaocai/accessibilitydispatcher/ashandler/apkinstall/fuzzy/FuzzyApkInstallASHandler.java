package io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.fuzzy;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.R;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.AbsApkInstallHandler;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallHandlerFactory;
import io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 根据文字定位的自动安装
 * <ul>
 * <li>1. 不根据包名去过滤事件</li>
 * <li>2. 暂时只支持自动安装，卸载和防卸载不支持，因为不同rom卸载的界面差别实在太大</li>
 * <li>3. 默认不加入到工厂中，如果要使用，请继承
 * {@linkplain io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall.ApkInstallHandlerFactory 工厂} ，重写方法
 * {@link ApkInstallHandlerFactory#initHandlers()}</li>
 * </ul>
 * <p>
 * TODO 各种机型测试
 *
 * @author zhitao
 * @since 2017-04-05 11:10
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class FuzzyApkInstallASHandler extends AbsApkInstallHandler {
	
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
	@NonNull
	@Override
	protected String getSupportPkgName() {
		return "";
	}
	
	/**
	 * 是否根据包名去定位事件，并交由指定包名的自动点击类处理
	 * <p>
	 * 如果能确定包名的话，当然是最好
	 * 但是如果不能确定包名的话，那么这里就要返回false
	 * <p>
	 * e.g.
	 * <p>
	 * 安装apk的系统应用包名在不同系统上都不太相同，暂时我们可能采取文字搜索来定位 "下一步" "安装" 等等的按钮来点击
	 * （毕竟即便包名不一样，但是给用户显示的界面一般都有"下一步" "安装"这些按钮文字），如果我们采取了这种根据文字搜索的方法来处理的话
	 * 那么我们就不需要包名来定位了，所以这种时候就需要返回false了
	 *
	 * @return 返回结果
	 * <ul>
	 * <li>true : 是</li>
	 * <li>false : 否</li>
	 * </ul>
	 *
	 * @see #getSupportPkgName()
	 */
	@Override
	protected boolean isUsingPkgName2TrackEvent() {
		return false;
	}
	
	@Override
	protected void onServiceConnected() {
		
	}
	
	@Override
	protected void onInterrupt() {
		
	}
	
	@Override
	protected void onAccessibilityEvent(AccessibilityEvent event) {
		switch (event.getEventType()) {
		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
			handleApkInstall();
			handleApkInstallSuccess();
			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			handleApkInstall();
			handleApkInstallSuccess();
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			handleApkInstall();
			break;
		default:
			break;
		}
	}
	
	/**
	 * @return 是否在apk安装界面
	 */
	@Override
	protected boolean isInApkInstallPage() {
		return true;
	}
	
	/**
	 * 执行在apk安装界面的逻辑
	 */
	@Override
	protected void runLogicInApkInstallPage() {
		AccessibilityNodeInfo rootNodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (rootNodeInfo == null) {
			return;
		}
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			// 找到需要自动安装的app
			if ((target.getAction() & ApkInstallTarget.ACTION_AUTO_INSTALL) == 0) {
				continue;
			}
			
			// 根据名字找到NodeInfo，没有就跳过
			List<AccessibilityNodeInfo> appNameNodes = rootNodeInfo.findAccessibilityNodeInfosByText(target.getAppName());
			DLog.i(
					"textId: %s 在 rootActiveWindow 中有%d个node",
					target.getAppName(),
					appNameNodes == null ? 0 : appNameNodes.size()
			);
			if (appNameNodes == null || appNameNodes.isEmpty()) {
				continue;
			}
			boolean isFindAppNameSuccess = false;
			for (AccessibilityNodeInfo appNameNode : appNameNodes) {
				if (appNameNode == null || appNameNode.getText() == null) {
					continue;
				}
				DLog.i("* text: %s", appNameNode.getText().toString());
				if (!target.getAppName().equals(appNameNode.getText().toString())) {
					continue;
				}
				DLog.i("找到应用[%s]的View", target.getAppName());
				isFindAppNameSuccess = true;
				break;
			}
			if (!isFindAppNameSuccess) {
				continue;
			}
			
			DLog.i("可能进入应用[%s]的安装界面", target.getAppName());
			
			// 再查找 下一步 或者 安装 的按钮
			// 如果找到就点击，跳出循环
			
			// TODO
			// 用来标记当前页面是不是安装页面
			// 因为这里是for循环，针对每个目标都做一遍，假设有10个目标，但是我们并不需要做10次遍历，因为第一次遍历没有确定是安装页面，后续也不会是
			// 所以加个变量标记
			// boolean isInInstallPage = false;
			
			// 1. 先看看有没有 "下一步" 有就点击，点击之后就不管了，交由新触发的事件处理
			String nextStepStr = getAccessibilityService().getResources()
			                                              .getString(R.string
					                                              .accessibility_dispatcher_apk_install_page_next_step);
			List<AccessibilityNodeInfo> nextStepNodes = rootNodeInfo.findAccessibilityNodeInfosByText(nextStepStr);
			DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", nextStepStr, nextStepNodes == null ? 0 : nextStepNodes.size());
			if (nextStepNodes != null && !nextStepNodes.isEmpty()) {
				boolean isClickNextStepBtn = false;
				for (AccessibilityNodeInfo nextStepNode : nextStepNodes) {
					if (nextStepNode == null || nextStepNode.getText() == null) {
						continue;
					}
					DLog.i("* text: %s", nextStepNode.getText().toString());
					if (!nextStepStr.equals(nextStepNode.getText().toString())) {
						continue;
					}
					if (!nextStepNode.isClickable()) {
						continue;
					}
					DLog.i("可能找到下一步按钮[%s]", nextStepStr);
					if (nextStepNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
						isClickNextStepBtn = true;
						callbackApkInstallBtnClick(target);
						break;
					}
				}
				if (isClickNextStepBtn) {
					break;
				}
			}
			
			// 2. 到这里就表示没有 "下一步" 的按钮，可能已经浏览完所有权限，所以我们就需要点击 "安装"
			
			String installStr = getAccessibilityService().getResources()
			                                             .getString(R.string.accessibility_dispatcher_apk_install_page_install);
			List<AccessibilityNodeInfo> installNodes = rootNodeInfo.findAccessibilityNodeInfosByText(installStr);
			DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", installStr, installNodes == null ? 0 : installNodes.size());
			if (installNodes != null && !installNodes.isEmpty()) {
				boolean isCLickInstallBtn = false;
				for (AccessibilityNodeInfo installNode : installNodes) {
					if (installNode == null || installNode.getText() == null) {
						continue;
					}
					DLog.i("* text: %s", installNode.getText().toString());
					if (!installStr.equals(installNode.getText().toString())) {
						continue;
					}
					if (!installNode.isClickable()) {
						continue;
					}
					DLog.i("可能找到安装按钮:%s", installStr);
					if (installNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
						isCLickInstallBtn = true;
						callbackApkInstallBtnClick(target);
						
						if ((target.getAction() & ApkInstallTarget.ACTION_WAIT_INSTALLING) != 0) {
							callbackApkInstalling(target);
						} else {
							goBack();
						}
						break;
					}
				}
				if (isCLickInstallBtn) {
					break;
				}
			}
			// 到这里的话就表示都没有找到 下一步 或者 安装 就不用执行下次循环了
			break;
		}
		rootNodeInfo.recycle();
	}
	
	/**
	 * @return 是否在apk安装中界面
	 */
	@Override
	protected boolean isInApkInstallingPage() {
		return false;
	}
	
	/**
	 * 执行在apk安装中界面的逻辑
	 */
	@Override
	protected void runLogicInApkInstallingPage() {
		
	}
	
	/**
	 * @return 是否在apk安装成功界面
	 */
	@Override
	protected boolean isInApkInstallSuccessPage() {
		return true;
	}
	
	/**
	 * 执行在apk安装成功界面的逻辑
	 */
	@Override
	protected void runLogicInApkInstallSuccessPage() {
		AccessibilityNodeInfo rootNodeInfo = getAccessibilityService().getRootInActiveWindow();
		if (rootNodeInfo == null) {
			return;
		}
		
		for (ApkInstallTarget target : getTargets()) {
			if (!target.isValid()) {
				continue;
			}
			// 找到需要自动安装的app
			if (((target.getAction() & ApkInstallTarget.ACTION_CLICK_FINISH) == 0) &&
			    ((target.getAction() & ApkInstallTarget.ACTION_CLICK_OPEN) == 0)) {
				continue;
			}
			
			// 根据名字找到NodeInfo，没有就跳过
			List<AccessibilityNodeInfo> appNameNodes = rootNodeInfo.findAccessibilityNodeInfosByText(target.getAppName());
			DLog.i(
					"textId: %s 在 rootActiveWindow 中有%d个node",
					target.getAppName(),
					appNameNodes == null ? 0 : appNameNodes.size()
			);
			if (appNameNodes == null || appNameNodes.isEmpty()) {
				continue;
			}
			boolean isFindAppNameSuccess = false;
			for (AccessibilityNodeInfo appNameNode : appNameNodes) {
				if (appNameNode == null || appNameNode.getText() == null) {
					continue;
				}
				DLog.i("* text: %s", appNameNode.getText().toString());
				if (!target.getAppName().equals(appNameNode.getText().toString())) {
					continue;
				}
				DLog.i("找到应用[%s]的View", target.getAppName());
				isFindAppNameSuccess = true;
				break;
			}
			if (!isFindAppNameSuccess) {
				continue;
			}
			
			DLog.i("可能进入应用[%s]的安装成功界面", target.getAppName());
			
			// 再查找 完成 或者 打开 的按钮
			// 如果找到就点击，跳出循环
			
			// TODO
			// 用来标记当前页面是不是安装成功页面
			// 因为这里是for循环，针对每个目标都做一遍，假设有10个目标，但是我们并不需要做10次遍历，因为第一次遍历没有确定是安装页面，后续也不会是
			// 所以加个变量标记
			// boolean isInInstallPage = false;
			
			// 看看是否存在 完成 和 打开 的按钮，如果都有的话就当是安装成功的界面了
			String finishStr =
					getAccessibilityService().getResources().getString(R.string
							.accessibility_dispatcher_apk_install_page_finish);
			String openStr =
					getAccessibilityService().getResources().getString(R.string.accessibility_dispatcher_apk_install_page_open);
			
			List<AccessibilityNodeInfo> finishNodes = rootNodeInfo.findAccessibilityNodeInfosByText(finishStr);
			DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", finishStr, finishNodes == null ? 0 : finishNodes.size());
			
			List<AccessibilityNodeInfo> openNodes = rootNodeInfo.findAccessibilityNodeInfosByText(openStr);
			DLog.i("textId: %s 在 rootActiveWindow 中有%d个node", openStr, openNodes == null ? 0 : openNodes.size());
			
			if (finishNodes == null || finishNodes.isEmpty() || openNodes == null || openNodes.isEmpty()) {
				// 直接跳出循环，因为继续下一次循环也不会确定到这个是安装成功的页面
				break;
			}
			
			if ((target.getAction() & ApkInstallTarget.ACTION_CLICK_FINISH) != 0) {
				boolean isClickFinishBtn = false;
				for (AccessibilityNodeInfo finishNode : finishNodes) {
					if (finishNode == null || finishNode.getText() == null) {
						continue;
					}
					DLog.i("* text: %s", finishNode.getText().toString());
					if (!finishStr.equals(finishNode.getText().toString())) {
						continue;
					}
					if (!finishNode.isClickable()) {
						continue;
					}
					DLog.i("可能找到完成按钮[%s]", finishStr);
					if (finishNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
						isClickFinishBtn = true;
						callbackApkInstallFinish(target);
						break;
					}
				}
				if (isClickFinishBtn) {
					break;
				}
			}
			
			if ((target.getAction() & ApkInstallTarget.ACTION_CLICK_OPEN) != 0) {
				boolean isClickOpenBtn = false;
				for (AccessibilityNodeInfo openNode : openNodes) {
					if (openNode == null || openNode.getText() == null) {
						continue;
					}
					DLog.i("* text: %s", openNode.getText().toString());
					if (!openStr.equals(openNode.getText().toString())) {
						continue;
					}
					if (!openNode.isClickable()) {
						continue;
					}
					DLog.i("可能找到打开按钮[%s]", openStr);
					if (openNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
						isClickOpenBtn = true;
						callbackApkInstallLaunch(target);
						break;
					}
				}
				if (isClickOpenBtn) {
					break;
				}
			}
			// 到这里的话就表示都没有找到 下一步 或者 安装 就不用执行下次循环了
			break;
		}
		rootNodeInfo.recycle();
	}
	
	/**
	 * @return 是否在apk卸载界面
	 */
	@Override
	protected boolean isInAppUninstallPage() {
		return false;
	}
	
	/**
	 * 执行在apk卸载界面的逻辑
	 */
	@Override
	protected void runLogicInAppUninstallPage() {
		
	}
}
