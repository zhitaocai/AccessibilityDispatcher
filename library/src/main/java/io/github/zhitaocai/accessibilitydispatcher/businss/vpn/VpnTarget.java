package io.github.zhitaocai.accessibilitydispatcher.businss.vpn;

import android.support.annotation.NonNull;

import io.github.zhitaocai.accessibilitydispatcher.businss.ITarget;

/**
 * VPN 配置需要的信息
 * <p>
 * 暂时只支持 暂时只支持 PPTP 和 L2TP/IPSec PSK
 *
 * @author zhitao
 * @since 2017-03-23 11:26
 */
public class VpnTarget implements ITarget {
	
	/**
	 * 创建指定的VPN配置
	 * <p>
	 * 如果之前已经存在同样名字的VPN配置，那么创建将失效
	 * <p>
	 * 如果希望存在就更新，不存在就创建，那么可以使用 {@code |} 运算符
	 * <p>
	 * e.g.
	 * <p>
	 * {@link #ACTION_CREATE_VPN_CONFIG} | {@link #ACTION_UPDATE_VPN_CONFIG}
	 */
	public final static int ACTION_CREATE_VPN_CONFIG = 1;
	
	/**
	 * 更新指定的VPN配置
	 * <p>
	 * 如果之前并没有指定的VPN配置，那么更新将失效
	 * <p>
	 * 如果希望存在就更新，不存在就创建，那么可以使用 {@code |} 运算符
	 * <p>
	 * e.g.
	 * <p>
	 * {@link #ACTION_CREATE_VPN_CONFIG} | {@link #ACTION_UPDATE_VPN_CONFIG}
	 *
	 * @see #ACTION_CREATE_VPN_CONFIG
	 */
	public final static int ACTION_UPDATE_VPN_CONFIG = 2;
	
	/**
	 * 为指定VPN配置用户信息，配置完毕后将会自动连接
	 * <p>
	 * 如果之前并没有这个指定的VPN信息，那么这个ACTION将失效
	 */
	public final static int ACTION_INPUT_USER_CONFIG = 4;
	
	/**
	 * 支持的行为
	 */
	private int mAction;
	
	/**
	 * VPN配置信息
	 */
	private VpnConfig mVpnConfig;
	
	/**
	 * 用户配置信息
	 */
	private UserConfig mUserConfig;
	
	private VpnTarget() {
		super();
	}
	
	/**
	 * TODO 预留方法
	 *
	 * @return
	 */
	public boolean isValid() {
		return true;
	}
	
	public int getAction() {
		return mAction;
	}
	
	/**
	 * 设置自动配置需要做的内容，支持位运算符
	 * <p>
	 * e.g.
	 * <p>
	 * 传入 ( {@link #ACTION_CREATE_VPN_CONFIG} | {@link #ACTION_INPUT_USER_CONFIG} )
	 * 那么就可以自动创建VPN配置，创建完毕之后就会自动创建用户配置，完毕后会自动连接
	 *
	 * @param action
	 */
	public void setAction(int action) {
		mAction = action;
	}
	
	public VpnConfig getVpnConfig() {
		return mVpnConfig;
	}
	
	public void setVpnConfig(VpnConfig vpnConfig) {
		mVpnConfig = vpnConfig;
	}
	
	public UserConfig getUserConfig() {
		return mUserConfig;
	}
	
	public void setUserConfig(UserConfig userConfig) {
		mUserConfig = userConfig;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("VpnTarget{");
		sb.append("\n  mAction=").append(mAction);
		sb.append("\n  mVpnConfig=").append(mVpnConfig);
		sb.append("\n  mUserConfig=").append(mUserConfig);
		sb.append("\n}");
		return sb.toString();
	}
	
	public static class UserConfig {
		
		/**
		 * 用户名
		 */
		private String mUserName;
		
		/**
		 * 密码
		 */
		private String mPassword;
		
		/**
		 * 是否保存用户账户信息
		 */
		private boolean mIsSaveAccountInfo;
		
		/**
		 * TODO 预留方法
		 *
		 * @return
		 */
		public boolean isValid() {
			return true;
		}
		
		public String getUserName() {
			return mUserName;
		}
		
		void setUserName(String userName) {
			mUserName = userName;
		}
		
		public String getPassword() {
			return mPassword;
		}
		
		void setPassword(String password) {
			mPassword = password;
		}
		
		public boolean isSaveAccountInfo() {
			return mIsSaveAccountInfo;
		}
		
		void setSaveAccountInfo(boolean saveAccountInfo) {
			mIsSaveAccountInfo = saveAccountInfo;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("UserConfig{");
			sb.append("\n  mUserName='").append(mUserName).append('\'');
			sb.append("\n  mPassword='").append(mPassword).append('\'');
			sb.append("\n  mIsSaveAccountInfo=").append(mIsSaveAccountInfo);
			sb.append("\n}");
			return sb.toString();
		}
	}
	
	/**
	 * VPN 配置所需要的信息
	 */
	public static class VpnConfig {
		
		/**
		 * VPN名字
		 */
		private String mVpnName;
		
		/**
		 * VPN类型
		 */
		private VpnType mVpnType;
		
		/**
		 * VPN服务器地址
		 */
		private String mVpnServerAddr;
		
		/**
		 * 是否开启 PPPEncryption 加密
		 */
		private boolean mPPPEncryption;
		
		/**
		 * DNS搜索域
		 */
		private String mDnsSearchDomain;
		
		/**
		 * DNS服务器
		 */
		private String mDnsServers;
		
		/**
		 * 转发路线
		 */
		private String mForwardingRoutes;
		
		/**
		 * L2TP密钥
		 */
		private String mL2TPSecret;
		
		/**
		 * IPSec标识符
		 */
		private String mIPSecIdentifier;
		
		/**
		 * IPSec预共享密钥
		 */
		private String mIPSecPreSharedKey;
		
		/**
		 * TODO 预留方法
		 *
		 * @return
		 */
		public boolean isValid() {
			return true;
		}
		
		public String getVpnName() {
			return mVpnName;
		}
		
		void setVpnName(String vpnName) {
			mVpnName = vpnName;
		}
		
		public VpnType getVpnType() {
			return mVpnType;
		}
		
		void setVpnType(VpnType vpnType) {
			mVpnType = vpnType;
		}
		
		public String getVpnServerAddr() {
			return mVpnServerAddr;
		}
		
		void setVpnServerAddr(String vpnServerAddr) {
			mVpnServerAddr = vpnServerAddr;
		}
		
		public boolean isPPPEncryption() {
			return mPPPEncryption;
		}
		
		void setPPPEncryption(boolean PPPEncryption) {
			mPPPEncryption = PPPEncryption;
		}
		
		public String getDnsSearchDomain() {
			return mDnsSearchDomain;
		}
		
		void setDnsSearchDomain(String dnsSearchDomain) {
			mDnsSearchDomain = dnsSearchDomain;
		}
		
		public String getDnsServers() {
			return mDnsServers;
		}
		
		void setDnsServers(String dnsServers) {
			mDnsServers = dnsServers;
		}
		
		public String getForwardingRoutes() {
			return mForwardingRoutes;
		}
		
		void setForwardingRoutes(String forwardingRoutes) {
			mForwardingRoutes = forwardingRoutes;
		}
		
		public String getL2TPSecret() {
			return mL2TPSecret;
		}
		
		void setL2TPSecret(String l2TPSecret) {
			mL2TPSecret = l2TPSecret;
		}
		
		public String getIPSecIdentifier() {
			return mIPSecIdentifier;
		}
		
		void setIPSecIdentifier(String IPSecIdentifier) {
			mIPSecIdentifier = IPSecIdentifier;
		}
		
		public String getIPSecPreSharedKey() {
			return mIPSecPreSharedKey;
		}
		
		void setIPSecPreSharedKey(String IPSecPreSharedKey) {
			mIPSecPreSharedKey = IPSecPreSharedKey;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("VpnConfig{");
			sb.append("\n  mVpnName='").append(mVpnName).append('\'');
			sb.append("\n  mVpnType=").append(mVpnType);
			sb.append("\n  mVpnServerAddr='").append(mVpnServerAddr).append('\'');
			sb.append("\n  mPPPEncryption=").append(mPPPEncryption);
			sb.append("\n  mDnsSearchDomain='").append(mDnsSearchDomain).append('\'');
			sb.append("\n  mDnsServers='").append(mDnsServers).append('\'');
			sb.append("\n  mForwardingRoutes='").append(mForwardingRoutes).append('\'');
			sb.append("\n  mL2TPSecret='").append(mL2TPSecret).append('\'');
			sb.append("\n  mIPSecIdentifier='").append(mIPSecIdentifier).append('\'');
			sb.append("\n  mIPSecPreSharedKey='").append(mIPSecPreSharedKey).append('\'');
			sb.append("\n}");
			return sb.toString();
		}
	}
	
	public enum VpnType {
		PPTP,
		L2TP_IPSec_PSK,
		L2TP_IPSec_RSA,
		IPSec_Xauth_PSK,
		IPSec_Xauth_RSA,
		IPSec_Hybrid_RSA,
	}
	
	public static class Builder {
		
		private VpnTarget mVpnTarget = new VpnTarget();
		
		private VpnConfig mVpnConfig = new VpnConfig();
		
		private UserConfig mUserConfig = new UserConfig();
		
		public Builder setAction(int action) {
			mVpnTarget.setAction(action);
			return this;
		}
		
		public Builder setVpnName(@NonNull String vpnName) {
			mVpnConfig.setVpnName(vpnName);
			return this;
		}
		
		public Builder setVpnType(VpnType vpnType) {
			mVpnConfig.setVpnType(vpnType);
			return this;
		}
		
		public Builder setVpnServerAddr(@NonNull String vpnServerAddr) {
			mVpnConfig.setVpnServerAddr(vpnServerAddr);
			return this;
		}
		
		public Builder setPPPEncryption(boolean PPPEncryption) {
			mVpnConfig.setPPPEncryption(PPPEncryption);
			return this;
		}
		
		public Builder setDnsSearchDomain(@NonNull String dnsSearchDomain) {
			mVpnConfig.setDnsSearchDomain(dnsSearchDomain);
			return this;
		}
		
		public Builder setDnsServers(@NonNull String dnsServers) {
			mVpnConfig.setDnsServers(dnsServers);
			return this;
		}
		
		public Builder setForwardingRoutes(@NonNull String forwardingRoutes) {
			mVpnConfig.setForwardingRoutes(forwardingRoutes);
			return this;
		}
		
		public Builder setL2TPSecret(@NonNull String l2TPSecret) {
			mVpnConfig.setL2TPSecret(l2TPSecret);
			return this;
		}
		
		public Builder setIPSecIdentifier(@NonNull String IPSecIdentifier) {
			mVpnConfig.setIPSecIdentifier(IPSecIdentifier);
			return this;
		}
		
		public Builder setIPSecPreSharedKey(@NonNull String IPSecPreSharedKey) {
			mVpnConfig.setIPSecPreSharedKey(IPSecPreSharedKey);
			return this;
		}
		
		public Builder setUserName(@NonNull String userName) {
			mUserConfig.setUserName(userName);
			return this;
		}
		
		public Builder setPassword(@NonNull String password) {
			mUserConfig.setPassword(password);
			return this;
		}
		
		public Builder setSaveAccountInfo(boolean saveAccountInfo) {
			mUserConfig.setSaveAccountInfo(saveAccountInfo);
			return this;
		}
		
		public VpnTarget build() {
			mVpnTarget.setVpnConfig(mVpnConfig);
			mVpnTarget.setUserConfig(mUserConfig);
			return mVpnTarget;
		}
	}
}
