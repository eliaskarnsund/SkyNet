package com.network.networkMonitor;

import android.app.Application;

public class GlobalData extends Application{
	private NetworkMapDataSource DSNetworkMap;
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	public NetworkMapDataSource getDSNetworkMap() {
		return DSNetworkMap;
	}

	public void setDSNetworkMap(NetworkMapDataSource dSNetworkMap) {
		DSNetworkMap = dSNetworkMap;
	}
	
}
