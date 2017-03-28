package io.github.zhitaocai.accessibilitydispatcher;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.zhitaocai.accessibilitydispatcher.businss.AbsHelper;
import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 辅助功能事件分发器
 *
 * @author zhitao
 * @since 2016-08-03 17:27
 */
public final class AccessibilityDispatcher {
	
	/**
	 * 子业务实例同步锁
	 */
	private final static String SYNC = "sync";
	
	/**
	 * 支持的子业务
	 * <ul>
	 * <li>key : 业务唯一标识</li>
	 * <li>value : 执行该业务所用到的自动点击类，理论上应该为1个，但是这里弄成列表是为了能兼容处理多个，比如需要自动安装应用，被类库支持原生系统，那么我在miui
	 * 上运行可能会出问题，这个时候，我尝试使用好几个自动点击处理类，一个为原生Android
	 * 系统的处理类，一个为根据文字模糊搜索的点击处理类，毕竟文字模糊搜索的处理兼容性更好
	 * （尽管准确度可能存在问题，但是这里我们出现一个明显不适配的场合了，所以用上这种兼容性处理就比较好了）</li>
	 * </ul>
	 */
	private static HashMap<String, ArrayList<AbsASHandler>> sAbsASHandlers;
	
	/**
	 * 耗时任务运行的线程池
	 */
	private static ExecutorService sSingleThreadExecutor;
	
	/**
	 * UIhandler
	 */
	private static Handler sUIHandler;
	
	/**
	 * 配置实例
	 */
	private static AccessibilityDebugConfig sConfig = new AccessibilityDebugConfig();
	
	/**
	 * 是否开启调试的log
	 *
	 * @param isDebug <ul>
	 *                <li>{@code true}: 开启调试模式，会有log输出</li>
	 *                <li>{@code false}: 关闭调试模式，没有log输出（默认）</li>
	 *                </ul>
	 *
	 * @return 调试配置
	 */
	public static AccessibilityDebugConfig debugLog(boolean isDebug) {
		sConfig.debugLog(isDebug);
		return sConfig;
	}
	
	/**
	 * 所有的节点查找或者其他耗时操作都统一用这个服务来执行
	 * <p>
	 * 你应该需要知道
	 * <ul>
	 * <li>AccessibilityService中本质还是一个Service，在里面执行查找节点之类等的耗时操作是可能导致UI线程产生卡顿感</li>
	 * <li>实际上，我们用辅助功能产生的事件是一件一件来的，如果我们要自动点击某些事件，那么其实在我们还没有点击前，基本是不会产生新的事件（不考虑计时器之类的界面）</li>
	 * </ul>
	 * 基于上面两点，我们就可以用一个单线程池来，来针对每个新的事件做判断而不会导致逻辑混淆
	 */
	static ExecutorService getSingleThreadExecutor() {
		if (sSingleThreadExecutor == null) {
			sSingleThreadExecutor = Executors.newSingleThreadExecutor();
		}
		return sSingleThreadExecutor;
	}
	
	static Handler getUIHandler() {
		if (sUIHandler == null) {
			sUIHandler = new Handler(Looper.getMainLooper());
		}
		return sUIHandler;
	}
	
	static HashMap<String, ArrayList<AbsASHandler>> getAbsASHandlers() {
		if (sAbsASHandlers == null) {
			sAbsASHandlers = new HashMap<>();
		}
		return sAbsASHandlers;
	}
	
	/**
	 * 更新辅助功能支持的列表以及其他各种属性
	 *
	 * @param helper 实际对应的业务
	 */
	public static void updateHelper(AbsHelper helper) {
		if (helper == null) {
			return;
		}
		synchronized (SYNC) {
			// 先看看当前支持的列表中是否有
			ArrayList<AbsASHandler> handlers = getAbsASHandlers().get(helper.getIdentify());
			if (handlers == null || handlers.isEmpty()) {
				// 如果列表没有的话，检查是否需要开启，如果不需要开启的话，那么也就没必要创建对象
				if (!helper.isEnable()) {
					return;
				}
				handlers = helper.getHandlerFactory().initHandlers();
			}
			for (AbsASHandler handler : handlers) {
				handler.setEnable(helper.isEnable());
				handler.setCallBacks(helper.getCallBacks());
				handler.setTargets(helper.getTargets());
			}
			sAbsASHandlers.put(helper.getIdentify(), handlers);
			return;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void onServiceConnected(AccessibilityService service) {
		DLog.setTag("accessibility");
		synchronized (SYNC) {
			try {
				if (sConfig.isShowDebugLog()) {
					DLog.i(">>>>>>>>>>>>>>>>>> 辅助功能服务连接 >>>>>>>>>>>>>>>>>>");
				}
				if (sAbsASHandlers == null || sAbsASHandlers.isEmpty()) {
					return;
				}
				for (ArrayList<AbsASHandler> handlers : sAbsASHandlers.values()) {
					if (handlers == null || handlers.isEmpty()) {
						continue;
					}
					for (AbsASHandler handler : handlers) {
						handler.setAccessibilityService(service);
						handler.handlerOnServiceConnected();
					}
				}
			} catch (Throwable e) {
				DLog.e(e);
			}
		}
	}
	
	public static void onInterrupt(AccessibilityService service) {
		synchronized (SYNC) {
			try {
				if (sConfig.isShowDebugLog()) {
					DLog.i(">>>>>>>>>>>>>>>>>> 辅助功能服务被中断 >>>>>>>>>>>>>>>>>>");
				}
				if (sAbsASHandlers == null || sAbsASHandlers.isEmpty()) {
					return;
				}
				for (ArrayList<AbsASHandler> handlers : sAbsASHandlers.values()) {
					if (handlers == null || handlers.isEmpty()) {
						continue;
					}
					for (AbsASHandler handler : handlers) {
						handler.setAccessibilityService(service);
						handler.handlerOnInterrupt();
					}
				}
			} catch (Throwable e) {
				DLog.e(e);
			}
		}
	}
	
	public static void onAccessibilityEvent(AccessibilityService service, AccessibilityEvent event) {
		synchronized (SYNC) {
			try {
				if (sConfig.isShowDebugLog()) {
					DLog.i(
							">>>>>>>>>>>>>>>>>> 触发新事件 >>>>>>>>>>>>>>>>>>\n" +
							"> 事件来源包名: %s\n> 事件源类名: %s\n> 事件类型描述: %s\n> 事件类型: %d\n> 事件源: %s",
							event.getPackageName(),
							event.getClassName(),
							getEventTypeNameByTypeId(event.getEventType()),
							event.getEventType(),
							sConfig.isShowEventSourceLog() ? event.getSource() : "暂时不输出"
					);
				}
				if (sAbsASHandlers == null || sAbsASHandlers.isEmpty()) {
					return;
				}
				for (ArrayList<AbsASHandler> handlers : sAbsASHandlers.values()) {
					if (handlers == null || handlers.isEmpty()) {
						continue;
					}
					for (AbsASHandler handler : handlers) {
						if (handler.isUsingPkgName2TrackEvent()) {
							if (!event.getPackageName().equals(handler.getSupportPkgName())) {
								continue;
							}
						}
						
						handler.setAccessibilityService(service);
						handler.handlerOnAccessibilityEvent(event);
					}
				}
			} catch (Throwable e) {
				DLog.e(e);
			}
		}
	}
	
	/**
	 * 主要用来输出log
	 */
	private static String getEventTypeNameByTypeId(int typeId) {
		if (sEventNameArray == null) {
			sEventNameArray = new SparseArray<>();
			Field[] fields = AccessibilityEvent.class.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) &&
				    Modifier.isFinal(field.getModifiers()) && field.getName().startsWith("TYPE_")) {
					try {
						sEventNameArray.put(field.getInt(AccessibilityEvent.class), field.getName());
					} catch (Exception e) {
						DLog.i(e);
					}
				}
			}
		}
		return sEventNameArray.get(typeId);
	}
	
	/**
	 * 主要用来输出log
	 * <p/>
	 * key : eventType
	 * value : eventTypeDesc
	 */
	private static SparseArray<String> sEventNameArray;
	
}


