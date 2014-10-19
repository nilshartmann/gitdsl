package de.gitworkshop.web;

public class WebAppEnvInfo {
	
	public static final String LAUNCHER_SYSTEM_PROPERTY="de.gitworkshop.web.launcherId";
	
	public String getWebAppVersion() {
		return "TODO";
	}
	
	public String getLauncher() {
		return System.getProperty(LAUNCHER_SYSTEM_PROPERTY, "unknown");
	}

}
