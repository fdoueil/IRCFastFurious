package com.cfranc.irc;

public interface IfClientServerProtocol {
	public static final String LOGIN_PWD = "#Login?#Pwd?";
	public static final String SEPARATOR="#";
	public static final String KO = "#KO";
	public static final String OK = "#OK";
	public static final String ADD = "#+#";
	public static final String DEL = "#-#";
	// sample for create channel 'Discussions' and creator user 'John' -> #+C#Discussions#John
	public static final String CREATE_CHANNEL = "#+C#";
	// sample for create channel 'Discussions' and joined user 'Paul' -> #C+U#Discussions#Paul
	public static final String USER_JOIN_CHANNEL = "#C+U#";
}