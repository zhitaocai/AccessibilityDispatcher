package io.github.zhitaocai.accessibilitydispatcher.utils;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.github.zhitaocai.accessibilitydispatcher.log.DLog;

/**
 * 剪切板使用（暂时只支持文字剪切）
 *
 * @author zhitaocai edit on 2014-7-15
 */
public class ClipboardManagerUtil {
	
	/**
	 * 保存文字到剪切板中
	 *
	 * @param context
	 * @param str
	 *
	 * @return
	 */
	public static boolean setText(@NonNull Context context, String str) {
		Context appliactionContext = context.getApplicationContext();
		//		if (Build.VERSION.SDK_INT >= 11) {
		try {
			ClipboardManager clipManager = (ClipboardManager) appliactionContext.getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("simple text", str);
			clipManager.setPrimaryClip(clip);
			return true;
		} catch (Exception e) {
			DLog.e(e);
		}
		return false;
		//		} else {
		//			try {
		//				android.text.ClipboardManager clipManager =
		//						(android.text.ClipboardManager) appliactionContext.getSystemService(Context.CLIPBOARD_SERVICE);
		//				clipManager.setText(str);
		//				return true;
		//			} catch (Exception e) {
		//				DLog.e(e);
		//			}
		//			return false;
		//		}
	}
	
	/**
	 * 获取剪切版中的文字，如果有的话
	 *
	 * @param context
	 *
	 * @return
	 */
	@Nullable
	public static String getText(@NonNull Context context) {
		Context appliactionContext = context.getApplicationContext();
		
		// 如果当前设备的android-sdk 版本号小于11
		//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		try {
			ClipboardManager clipManager = (ClipboardManager) appliactionContext.getSystemService(Context.CLIPBOARD_SERVICE);
			if (clipManager.hasPrimaryClip()) {
				// 如果剪切版中的是文字
				if (clipManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
					StringBuilder sb = new StringBuilder();
					ClipData clipData = clipManager.getPrimaryClip();
					for (int i = 0; i < clipData.getItemCount(); ++i) {
						sb.append(clipData.getItemAt(i).getText());
						
						// ClipData.Item item = clipData.getItemAt(i);
						// CharSequence str = item.coerceToText(SettingsActivity.this);
						// resultString += str;
					}
					return sb.toString();
				}
			}
		} catch (Exception e) {
			DLog.e(e);
		}
		return null;
		
		//		} else {
		//			try {
		//				android.text.ClipboardManager clipManager =
		//						(android.text.ClipboardManager) appliactionContext.getSystemService(Context.CLIPBOARD_SERVICE);
		//				if (clipManager.hasText()) {
		//					return clipManager.getText().toString();
		//				}
		//			} catch (Exception e) {
		//				DLog.e(e);
		//			}
		//			return null;
		//		}
	}
}