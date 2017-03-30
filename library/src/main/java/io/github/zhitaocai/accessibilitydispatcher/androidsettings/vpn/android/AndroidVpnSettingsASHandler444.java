package io.github.zhitaocai.accessibilitydispatcher.androidsettings.vpn.android;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import io.github.zhitaocai.accessibilitydispatcher.businss.vpn.VpnTarget;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * @author zhitao
 * @since 2017-03-23 10:58
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class AndroidVpnSettingsASHandler444 extends AbsAndroidVpnSettingsASHandler {
	
	@Override
	protected void onServiceConnected() {
		
	}
	
	@Override
	protected void onInterrupt() {
		
	}
	
	@Override
	protected void onAccessibilityEvent(AccessibilityEvent event) {
		switch (event.getEventType()) {
		//		case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
		//			switch (event.getClassName().toString()) {
		//			case "android.widget.ListView":
		//				handleInVpnListPage();
		//				break;
		//			default:
		//				break;
		//			}
		//			break;
		case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
			switch (event.getClassName().toString()) {
			case "com.android.settings.SubSettings":
			case "com.android.settings.Settings$VpnSettingsActivity":
				handleInVpnListPage();
				//handleScrollListPage();
				break;
			case "com.android.settings.vpn2.VpnDialog":
				handleInVpnConfigDialog();
				handleInUserConfigDialog();
				break;
			case "android.app.AlertDialog":
				handleInVpnProfileEditDialog();
				break;
			default:
				break;
			}
			break;
		case AccessibilityEvent.TYPE_VIEW_SCROLLED:
			switch (event.getClassName().toString()) {
			case "android.widget.ScrollView":
				handleInVpnListPage();
				//handleScrollListPage();
				break;
			default:
				break;
			}
			break;
		case AccessibilityEvent.TYPE_VIEW_CLICKED:
			switch (event.getClassName().toString()) {
			case "android.widget.CheckBox":
				handleInVpnConfigDialog();
				//handleScrollListPage();
				break;
			case "android.widget.Spinner":
				handleInVpnConfigDialogSpinnerWindow();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 是否在VPN列表页
	 *
	 * @return true or false
	 */
	@Override
	protected boolean isInVpnListPage() {
		return isNodeExistInRootActiveWindowByViewIds("android:id/action_bar",
				"com.android.settings:id/vpn_create",
				"android:id/prefs",
				"android:id/list"
		);
	}
	
	/**
	 * 执行在VPN列表页的逻辑
	 */
	@Override
	protected void runLogicInVpnListPage() {
		for (final VpnTarget target : getTargets()) {
			if (target == null || !target.isValid()) {
				continue;
			}
			
			// 下面几个支持的ACTION都和现在是否已经存在对应的VPN配置有关
			//
			// 比如：要创建 VPN1 那么我们也得新看看是否已经存在，存在就不创建了
			//
			// TODO 其实这种请检查可以尝试看看系统中是否有隐藏的方法之类可以检查到，如果可以的话，应该在外面判断最佳
			//
			// 综上
			// 1. 我们还是先看看listview中已经存在哪些VPN配置
			// 2. 另外还得注意的是，一次一个，当我们完成一个任务的action的时候就应该结束循环
			
			// 遍历ListView的项获取当前ListView中已经存在的VPN配置
			List<AccessibilityNodeInfo> listViewNodeInfos = getNodeByViewIdFromRootInActiveWindow("android:id/list");
			if (listViewNodeInfos == null || listViewNodeInfos.isEmpty()) {
				break;
			}
			AccessibilityNodeInfo listViewNodeInfo = listViewNodeInfos.get(0);
			if (listViewNodeInfo == null) {
				break;
			}
			
			AccessibilityNodeInfo targetTitleNode = null;
			for (int i = 0; i < listViewNodeInfo.getChildCount(); i++) {
				AccessibilityNodeInfo itemNodes = listViewNodeInfo.getChild(i);
				if (itemNodes == null) {
					continue;
				}
				
				List<AccessibilityNodeInfo> titleNodes = getNodeByViewIdFromNode(itemNodes, "android:id/title");
				//List<AccessibilityNodeInfo> subTitleNodes = getNodeByViewIdFromNode(itemNodes,"android:id/summary");
				if (titleNodes == null || titleNodes.isEmpty()) {
					continue;
				}
				AccessibilityNodeInfo titleNode = titleNodes.get(0);
				if (titleNode == null) {
					continue;
				}
				// 记录当前列表页显示的VPN配置
				// 这里得注意，VPN配置是可以同名的，也就是说listview是可以存在很多个VPN1 ...
				// 这里暂时不考虑同名情况，TODO 考虑这种情况
				String title = titleNode.getText() == null ? null : titleNode.getText().toString();
				if (title == null) {
					continue;
				}
				if (title.equals(target.getVpnConfig().getVpnName())) {
					targetTitleNode = titleNode;
					break;
				}
			}
			
			int curStep = getVpnTargetCacheHashMap().get(target);
			
			DLog.i("目标VPN的item是否为空: %b 当前步骤: %d", targetTitleNode == null, curStep);
			
			// 看看是否找到目标VPN的Node
			if (targetTitleNode == null) {
				
				// 如果没有找到，就表示还没有创建，这个时候只能执行创建VPN配置
				if ((target.getAction() & VpnTarget.ACTION_CREATE_VPN_CONFIG) != 0) {
					
					// 如果之前还没有点击创建过的话，就点击
					if ((curStep & STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE) == 0) {
						if (performClickByViewIdFromRootActiveWindow("com.android.settings:id/vpn_create")) {
							
							// 标记已经为这个VPN配置信息点击过创建VPN配置了，下次在进入VPN配置的时候，就不在为这个VPN配置点击创建了
							getVpnTargetCacheHashMap().put(target, curStep | STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE);
							callBackEnterVpnConfigDialog(target);
						}
						return;
					}
				}
			} else {
				
				// 如果找到了，那么就表示已经创建了，这个时候可以做的就比较多了
				
				// 如果需要更新VPN配置信息
				if ((target.getAction() & VpnTarget.ACTION_UPDATE_VPN_CONFIG) != 0) {
					if ((curStep & STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE) == 0) {
						
						// 因为title不一定能点击，所以我们需要偏离其父级node，找到能长按点击的
						// count 是为了防止死循环
						int count = 0;
						AccessibilityNodeInfo longClickNode = targetTitleNode;
						boolean isLongClickable = longClickNode.isLongClickable();
						while (!isLongClickable && count++ < 20) {
							longClickNode = longClickNode.getParent();
							if (longClickNode == null) {
								break;
							}
							isLongClickable = longClickNode.isLongClickable();
						}
						if (longClickNode != null && isLongClickable) {
							longClickNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
							return;
						}
					}
				}
				
				// 如果需要配置用户信息
				if ((target.getAction() & VpnTarget.ACTION_INPUT_USER_CONFIG) != 0) {
					
					// 如果之前还没有点击创建过的话，就点击指定的VPN配置进行创建
					if ((curStep & STEP_USER_CONFIG_START_INPUT) == 0) {
						
						// 因为title不一定能点击，所以我们需要偏离其父级node，找到能点击的
						// count 是为了防止死循环
						int count = 0;
						AccessibilityNodeInfo clickNode = targetTitleNode;
						boolean isClickable = clickNode.isClickable();
						while (!isClickable && count++ < 20) {
							clickNode = clickNode.getParent();
							if (clickNode == null) {
								break;
							}
							isClickable = clickNode.isClickable();
						}
						if (clickNode != null && isClickable) {
							// 点击目标VPN配置的可点击的node，进入用户信息输入的对话框
							if (clickNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
								
								// 标记开始为这个VPN输入用户信息
								getVpnTargetCacheHashMap().put(target, curStep | STEP_USER_CONFIG_START_INPUT);
								callBackEnterUserConfigDialog(target);
							}
							return;
						}
					}
				}
				DLog.i("已经完成所有配置");
			}
		}
		
		// 到这里就表示已经遍历完所有已经完成的目标VPN配置
		// 这里为在再次回到列表时，退出VPN设置页面
		DLog.i("点击返回");
		goBack();
	}
	
	/**
	 * 滑动列表页的VPN的ListView
	 */
	@Override
	protected void scrollListViewInVpnListPage() {
		// 遍历ListView的项获取当前ListView中已经存在的VPN配置
		List<AccessibilityNodeInfo> listViewNodeInfos = getNodeByViewIdFromRootInActiveWindow("android:id/list");
		if (listViewNodeInfos == null || listViewNodeInfos.isEmpty()) {
			return;
		}
		AccessibilityNodeInfo listViewNodeInfo = listViewNodeInfos.get(0);
		if (listViewNodeInfo == null) {
			return;
		}
		listViewNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
	}
	
	/**
	 * 是否在VPN配置的对话框中
	 *
	 * @return true or false
	 */
	@Override
	protected boolean isInVpnConfigDialog() {
		return isNodeExistInRootActiveWindowByViewIds("android:id/alertTitle",
				"com.android.settings:id/name",
				"com.android.settings:id/type"
				//				"com.android.settings:id/server",
				//				"com.android.settings:id/mppe"
		);
	}
	
	/**
	 * 执行在VPN配置的对话框中的逻辑
	 */
	@Override
	protected void runLogicInVpnConfigDialog() {
		for (final VpnTarget target : getTargets()) {
			
			if (target == null || !target.isValid()) {
				continue;
			}
			
			// 如果存在显示高级选项，那么就先点击
			if (performClickByViewIdFromRootActiveWindow("com.android.settings:id/show_options")) {
				
				// 因为只有点击这里之后才有下面的DNS设置的界面布局出来，所以点击之后我们return一下，然后下次再次进来的时候就能解析全部布局
				return;
			}
			
			// 如果需要创建/更新VPN配置
			if (((target.getAction() & VpnTarget.ACTION_CREATE_VPN_CONFIG) != 0) ||
			    ((target.getAction() & VpnTarget.ACTION_UPDATE_VPN_CONFIG) != 0)) {
				
				//  这里比较诡异的是，列表页中点击创建后，命名已经保存了记录，但是这里并没有点击了创建VPN的步骤记录，所以下面的代码都会补写
				int curStep = getVpnTargetCacheHashMap().get(target);
				
				// 如果还没有选择过VPN类型的话，就先选择VPN类型
				if ((curStep & STEP_VPN_CONFIG_HAS_CHOOSE_VPN_TYPE) == 0) {
					performClickByViewIdFromRootActiveWindow("com.android.settings:id/type");
					return;
				}
				
				// 如果还没有输入过VPN信息的话，就输入VPN信息
				if ((curStep & STEP_VPN_CONFIG_FINISH_INPUT) == 0) {
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/name",
							target.getVpnConfig().getVpnName(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/server",
							target.getVpnConfig().getVpnServerAddr(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/l2tp_secret",
							target.getVpnConfig().getL2TPSecret(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/ipsec_identifier",
							target.getVpnConfig().getIPSecIdentifier(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/ipsec_secret",
							target.getVpnConfig().getIPSecPreSharedKey(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/search_domains",
							target.getVpnConfig().getDnsSearchDomain(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/dns_servers",
							target.getVpnConfig().getDnsServers(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/routes",
							target.getVpnConfig().getForwardingRoutes(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					if (performClickByViewIdFromRootActiveWindow("android:id/button1")) {
						getVpnTargetCacheHashMap().put(target,
								curStep | STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE | STEP_VPN_CONFIG_FINISH_INPUT |
								STEP_VPN_CONFIG_HAS_CHOOSE_VPN_TYPE
						);
						callBackExitVpnConfigDialog(target);
					}
					return;
				}
			}
		}
	}
	
	/**
	 * 是否在VPN配置的对话框中选择VPN类型的Spinner中
	 *
	 * @return true or false
	 */
	@Override
	protected boolean isInVpnConfigDialogSpinnerWindow() {
		return isNodeExistInRootActiveWindowByTextIds("PPTP",
				"L2TP/IPSec PSK",
				"L2TP/IPSec RSA",
				"IPSec Xauth PSK",
				"IPSec Xauth RSA",
				"IPSec Hybrid RSA"
		);
	}
	
	/**
	 * 执行在VPN配置的对话框中选择VPN类型的Spinner中的逻辑
	 */
	@Override
	protected void runLogicInVpnConfigDialogSpinnerWindow() {
		for (final VpnTarget target : getTargets()) {
			
			if (target == null || !target.isValid()) {
				continue;
			}
			
			// 如果需要创建/更新VPN配置
			if (((target.getAction() & VpnTarget.ACTION_CREATE_VPN_CONFIG) != 0) ||
			    ((target.getAction() & VpnTarget.ACTION_UPDATE_VPN_CONFIG) != 0)) {
				
				// 如果已经选择过的话，就算了
				int curStep = getVpnTargetCacheHashMap().get(target);
				if ((curStep & STEP_VPN_CONFIG_HAS_CHOOSE_VPN_TYPE) != 0) {
					continue;
				}
				
				boolean hasChoosedVpnType = false;
				if (target.getVpnConfig().getVpnType() == VpnTarget.VpnType.PPTP) {
					performClickByTextFromRootActiveWindow("PPTP");
					hasChoosedVpnType = true;
				} else if (target.getVpnConfig().getVpnType() == VpnTarget.VpnType.L2TP_IPSec_PSK) {
					performClickByTextFromRootActiveWindow("L2TP/IPSec PSK");
					hasChoosedVpnType = true;
				} else if (target.getVpnConfig().getVpnType() == VpnTarget.VpnType.L2TP_IPSec_RSA) {
					performClickByTextFromRootActiveWindow("L2TP/IPSec RSA");
					hasChoosedVpnType = true;
				} else if (target.getVpnConfig().getVpnType() == VpnTarget.VpnType.IPSec_Xauth_PSK) {
					performClickByTextFromRootActiveWindow("IPSec Xauth PSK");
					hasChoosedVpnType = true;
				} else if (target.getVpnConfig().getVpnType() == VpnTarget.VpnType.IPSec_Xauth_RSA) {
					performClickByTextFromRootActiveWindow("IPSec Xauth RSA");
					hasChoosedVpnType = true;
				} else if (target.getVpnConfig().getVpnType() == VpnTarget.VpnType.IPSec_Hybrid_RSA) {
					performClickByTextFromRootActiveWindow("IPSec Hybrid RSA");
					hasChoosedVpnType = true;
				}
				if (hasChoosedVpnType) {
					getVpnTargetCacheHashMap().put(target,
							curStep | STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE | STEP_VPN_CONFIG_HAS_CHOOSE_VPN_TYPE
					);
					return;
				}
			}
		}
	}
	
	/**
	 * 是否在用户配置的对话框中
	 *
	 * @return true or false
	 */
	@Override
	protected boolean isInUserConfigDialog() {
		return isNodeExistInRootActiveWindowByViewIds("android:id/alertTitle",
				"com.android.settings:id/username",
				"com.android.settings:id/password",
				"com.android.settings:id/save_login"
		);
	}
	
	/**
	 * 执行在用户配置的对话框中的逻辑
	 */
	@Override
	protected void runLogicInUserConfigDialog() {
		for (final VpnTarget target : getTargets()) {
			
			if (target == null || !target.isValid()) {
				continue;
			}
			
			// 如果需要配置用户信息
			if ((target.getAction() & VpnTarget.ACTION_INPUT_USER_CONFIG) != 0) {
				
				//  这里比较诡异的是，列表页中点击创建后，命名已经保存了记录，但是这里并没有点击了创建VPN的步骤记录，所以下面的代码都会补写
				int curStep = getVpnTargetCacheHashMap().get(target);
				
				// 如果还没有输入过用户信息的话，就输入用户信息
				if ((curStep & STEP_USER_CONFIG_FINISH_INPUT) == 0) {
					
					// 先确定是否需要点击CheckBox，如果需要的话，就点击
					List<AccessibilityNodeInfo> checkBoxNodeInfos =
							getNodeByViewIdFromRootInActiveWindow("com.android.settings:id/save_login");
					if (checkBoxNodeInfos != null && !checkBoxNodeInfos.isEmpty()) {
						AccessibilityNodeInfo checkBoxNodeInfo = checkBoxNodeInfos.get(0);
						if (checkBoxNodeInfo != null) {
							boolean isNeed2SaveUserConfig = target.getUserConfig().isSaveAccountInfo();
							boolean isNodeChecked = checkBoxNodeInfo.isChecked();
							
							// 如果需要保存，但是还没有勾选保存  或者
							// 如果需要不保存，但是已经勾选保存
							// 那么就点击
							if ((isNeed2SaveUserConfig && !isNodeChecked) || (!isNeed2SaveUserConfig && isNodeChecked)) {
								checkBoxNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
							}
						}
					}
					
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/username",
							target.getUserConfig().getUserName(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					performInputTextByViewIdFromRootInActiveWindow("com.android.settings:id/password",
							target.getUserConfig().getPassword(),
							true,
							AccessibilityNodeInfo.MOVEMENT_GRANULARITY_LINE
					);
					
					if (performClickByViewIdFromRootActiveWindow("android:id/button1")) {
						getVpnTargetCacheHashMap().put(target,
								curStep | STEP_USER_CONFIG_START_INPUT | STEP_USER_CONFIG_FINISH_INPUT
						);
						callBackExitUserConfigDialog(target);
					}
					break;
				}
			}
		}
	}
	
	/**
	 * 是否在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中
	 *
	 * @return true or false
	 */
	@Override
	protected boolean isInVpnProfileEditDialog() {
		return isNodeExistInRootActiveWindowByViewIds("android:id/alertTitle", "android:id/select_dialog_listview");
	}
	
	/**
	 * 执行在VPN列表页，长按了某个VPN配置之后，弹出来的对话框中的逻辑
	 */
	@Override
	protected void runLogicInVpnProfileEditDialog() {
		
		String title = getTextByViewIdFromRootActiveWindow("android:id/alertTitle");
		if (TextUtils.isEmpty(title)) {
			return;
		}
		
		for (final VpnTarget target : getTargets()) {
			if (target == null || !target.isValid()) {
				continue;
			}
			
			if (!title.equals(target.getVpnConfig().getVpnName())) {
				continue;
			}
			
			// 如果需要更新VPN配置信息就点击
			if ((target.getAction() & VpnTarget.ACTION_UPDATE_VPN_CONFIG) != 0) {
				List<AccessibilityNodeInfo> listViewNodes =
						getNodeByViewIdFromRootInActiveWindow("android:id/select_dialog_listview");
				if (listViewNodes == null || listViewNodes.isEmpty()) {
					return;
				}
				
				AccessibilityNodeInfo listViewNode = listViewNodes.get(0);
				if (listViewNode == null) {
					return;
				}
				
				// 因为比较确定第一项就是修改配置，所以这里就不循环了
				AccessibilityNodeInfo itemNode = listViewNode.getChild(0);
				if (itemNode == null) {
					return;
				}
				
				// 如果之前还没有点击创建过的话，就点击
				int curStep = getVpnTargetCacheHashMap().get(target);
				
				// 标记已经为这个VPN配置信息点击过修改VPN配置了
				getVpnTargetCacheHashMap().put(target, curStep | STEP_VPN_CONFIG_START_2_CREATE_OR_UPDATE);
				
				if (itemNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
					callBackEnterVpnConfigDialog(target);
				}
			}
		}
	}
}
