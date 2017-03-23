package io.github.zhitaocai.accessibilitydispatcher.demo;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import io.github.zhitaocai.accessibilitydispatcher.AccessibilityDispatcher;

public class AccessibilityServiceDemo extends AccessibilityService {
	
	/**
	 * This method is a part of the {@link AccessibilityService} lifecycle and is
	 * called after the system has successfully bound to the service. If is
	 * convenient to use this method for setting the {@link AccessibilityServiceInfo}.
	 *
	 * @see AccessibilityServiceInfo
	 * @see #setServiceInfo(AccessibilityServiceInfo)
	 */
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AccessibilityDispatcher.onServiceConnected(this);
		}
	}
	
	/**
	 * Callback for {@link AccessibilityEvent}s.
	 *
	 * @param event The new event. This event is owned by the caller and cannot be used after
	 *              this method returns. Services wishing to use the event after this method returns should
	 *              make a copy.
	 */
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AccessibilityDispatcher.onAccessibilityEvent(this, event);
		}
	}
	
	/**
	 * Callback for interrupting the accessibility feedback.
	 */
	@Override
	public void onInterrupt() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AccessibilityDispatcher.onInterrupt(this);
		}
	}
}