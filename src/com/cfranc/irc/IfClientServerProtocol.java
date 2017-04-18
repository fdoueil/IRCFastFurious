package com.cfranc.irc;

public interface IfClientServerProtocol {
	public static final String LOGIN_PWD = "#Login?#Pwd?";
	public static final String SEPARATOR = "#";
	public static final String KO = "#KO";
	public static final String OK = "#OK";
	public static final String ADD = "#+#";
	public static final String DEL = "#-#";
	// sample for create channel 'Discussions' and creator user 'John' ->
	// #John#+C#Discussions
	public static final String CREATE_CHANNEL = "+C";
	public static final String OK_CHANNEL = "OK+C";
	// sample for create channel 'Discussions' and joined user 'Paul' ->
	// #Paul#C+U#Discussions
	public static final String USER_JOIN_CHANNEL = "C+U";
	public static final String OK_JOIN_CHANNEL = "OKC+U";
	// sample for message on channel 'Discussions' and joined user 'Bill' ->
	// #Bill#C+MSG#Discussions#Message
	public static final String USER_MESSAGE_CHANNEL = "C+MSG";
}