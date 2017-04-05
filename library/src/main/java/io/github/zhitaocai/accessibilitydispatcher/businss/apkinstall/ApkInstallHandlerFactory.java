package io.github.zhitaocai.accessibilitydispatcher.businss.apkinstall;

import android.os.Build;

import java.util.ArrayList;

import io.github.zhitaocai.accessibilitydispatcher.AbsASHandler;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android.AndroidApkInstallASHandler444;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android.AndroidApkInstallASHandler500;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android.AndroidApkInstallASHandler501;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android.AndroidApkInstallASHandler510;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android.AndroidApkInstallASHandler601;
import io.github.zhitaocai.accessibilitydispatcher.ashandler.apkinstall.android.AndroidApkInstallASHandler700;
import io.github.zhitaocai.accessibilitydispatcher.businss.IHandlerFactory;

/**
 * @author zhitao
 * @since 2017-04-01 09:47
 */
public class ApkInstallHandlerFactory implements IHandlerFactory {
	
	/**
	 * 根据机型 系统版本 系统类型等来创建具体的自动点击业务类
	 *
	 * @return 返回业务对象列表
	 */
	@Override
	public ArrayList<AbsASHandler> initHandlers() {
		ArrayList<AbsASHandler> handlers = new ArrayList<>();
		//		handlers.add(new SamsungApkInstallASHandler501());
		//		handlers.add(new FuzzyApkInstallASHandler(AndroidApkInstallViewCompat.PKGNAME));
		//		handlers.add(new FuzzyApkInstallASHandler(AndroidApkInstallViewCompat.PKGNAME_601));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			handlers.add(new AndroidApkInstallASHandler700());
			return handlers;
		}
		if ("6.0.1".equals(Build.VERSION.RELEASE)) {
			handlers.add(new AndroidApkInstallASHandler601());
			return handlers;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
			handlers.add(new AndroidApkInstallASHandler510());
			return handlers;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			if ("5.0.1".equals(Build.VERSION.RELEASE)) {
				handlers.add(new AndroidApkInstallASHandler501());
				return handlers;
			}
			handlers.add(new AndroidApkInstallASHandler500());
			return handlers;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			handlers.add(new AndroidApkInstallASHandler444());
			return handlers;
		}
		handlers.add(new AndroidApkInstallASHandler444());
		return handlers;
	}
}
