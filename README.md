# 辅助功能分发器

主要实现各种功能的辅助点击

* 自动配置VPN信息

## FEATURE

* 暂时只支持Android 原生 4.4.4系统
* 目前只支持第一页创建/更新一个VPN
* 支持常用的两个VPN创建 PPTP 和 L2TP/IPSec PSK

## 最终效果

![](static/gif/auto_create_pptp.gif)
![](static/gif/auto_create_l2tp.gif)

## USAGE

### 下载

```gradle

compile 'io.github.zhitaocai:accessibilitydispatcher:0.1.0'

// or
// 如果你的项目本身已经集成了 support-annotations 那么请移除本类库中本身所依赖的 support-annotations
compile ('io.github.zhitaocai:accessibilitydispatcher:0.1.0') {
    exclude group: 'com.android.support', module: 'support-annotations'
}

```

### 配置辅助功能服务

在你的辅助功能服务的关键几个方法中注入 ``AccessibilityDispatcher`` 的逻辑即可:

```java
public class AccessibilityServiceDemo extends AccessibilityService {
	

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		// 关键的方法处，注入代码即可
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AccessibilityDispatcher.onServiceConnected(this);
		}
	}
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// 关键的方法处，注入代码即可
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AccessibilityDispatcher.onAccessibilityEvent(this, event);
		}
	}
	
	@Override
	public void onInterrupt() {
		// 关键的方法处，注入代码即可
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			AccessibilityDispatcher.onInterrupt(this);
		}
	}
}
```

ps: 记得在AndroidManifest.xml中配置好辅助功能服务

```xml
<service
	android:name=".AccessibilityServiceDemo"
	android:label="@string/app_name"
	android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE" >
	<intent-filter >
		<action android:name="android.accessibilityservice.AccessibilityService" />
	</intent-filter >
	<meta-data
		android:name="android.accessibilityservice"
		android:resource="@xml/accessibility_service" />
</service >
```

### 自动创建一个PPTP

```java
protected void createPPTP() {
	// 在打开VPN界面之前，设置我们需要自动操作的内容
	VpnHelper.getInstance()
	
	         // 创建一个新的或者更新现有的一个PPTP连接
	         .setTarget(new VpnTarget.Builder().setVpnName("PPTP VPN Demo")
	                                           .setAction(VpnTarget.ACTION_CREATE_VPN_CONFIG |
	                                                      VpnTarget.ACTION_UPDATE_VPN_CONFIG |
	                                                      VpnTarget.ACTION_INPUT_USER_CONFIG)
	                                           .setVpnType(VpnTarget.VpnType.PPTP)
	                                           .setVpnServerAddr("1.2.3.4")
	                                           .setDnsSearchDomain("8.8.8.8")
	                                           .setDnsServers("8.8.8.8")
	                                           .setForwardingRoutes("10.0.0.0/8")
	                                           .setUserName("username")
	                                           .setPassword("password")
	                                           .setSaveAccountInfo(true)
	                                           .build())
	         // 设置VPN回调过程中的监听Listener，这里详细列可能会比较长，就不列了，demo里面有
	         .setCallBack(new OnVpnCallBack() {...})
		     
	         // 设置是否自动操作
	         .setEnable(true)
	
	         // 将本次设置的内容加入到辅助功能服务，不然你只是在瞎逼逼
	         .active();
	
	Intent intent;
	if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
		intent = new Intent(Settings.ACTION_VPN_SETTINGS);
	} else {
		intent = new Intent();
		intent.setAction("android.net.vpn.SETTINGS");
	}
	startActivityForResult(intent, REQ_AUTO_CONFIG_VPN);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	switch (requestCode) {
	case REQ_AUTO_CONFIG_VPN:
		
		// 不管配置是否成功，建议都取消自动配置，这样子，如果用户自己从设置中打开VPN配置的话，我们的自动点击程序就不会影响到用户的操作了
		//if (resultCode == RESULT_OK) { }
		VpnHelper.getInstance().reset().active();
		break;
	default:
		break;
	}
}
```

### 自动创建一个L2TP

```java


// 在打开VPN界面之前，设置我们需要自动操作的内容
VpnHelper.getInstance()

         // 创建一个新的或者更新现有的一个L2TP连接
         .setTarget(new VpnTarget.Builder().setVpnName("L2TP VPN Demo")
                                           .setAction(VpnTarget.ACTION_CREATE_VPN_CONFIG |
                                                      VpnTarget.ACTION_UPDATE_VPN_CONFIG |
                                                      VpnTarget.ACTION_INPUT_USER_CONFIG)
                                           .setVpnType(VpnTarget.VpnType.L2TP_IPSec_PSK)
                                           .setVpnServerAddr("1.2.3.4")
                                           .setL2TPSecret("L2TPSecret-test")
                                           .setIPSecIdentifier("IPSecIdentifier-test")
                                           .setIPSecPreSharedKey("IPSecPreSharedKey-test")
                                           .setDnsSearchDomain("8.8.8.8")
                                           .setDnsServers("8.8.8.8")
                                           .setForwardingRoutes("10.0.0.0/8")
                                           .setUserName("username")
                                           .setPassword("password")
                                           .setSaveAccountInfo(true)
                                           .build())

         // 设置VPN回调过程中的监听Adapter(相比起listener，adapter可以实现你感兴趣的回调而不用全部实现所有接口)
         .setCallBack(new OnVpnCallBackAdapter() {...})

         // 设置是否自动操作
         .setEnable(true)

         // 将本次设置的内容加入到辅助功能服务，不然你只是在瞎逼逼
         .active();
```

### TODO

* [ ] 目前只能一页，还没有考虑列表很长的情况，后续考虑滚动处理多页情况
* [ ] 支持一次输入多个VPN创建/修改

### Where to get demo apk?

1. run the following command line:
	```
	./gradlew :app:assembleRelease
	```
2. then you can find the apk in ``/static/apk``

## License

    Copyright 2017 Zhitao Cai

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.