package me.eglp.twitter.util;

import me.eglp.twitter.TwitterAPI;

public enum TwitterEndpoint {
	
	USER_BY_USERNAME(TwitterAPI.ENDPOINT + "users/by/username/%s"),
	TWEETS(TwitterAPI.ENDPOINT + "users/%s/tweets"),
	USER_BY_ID(TwitterAPI.ENDPOINT +  "users/%s"),
	USERS_BY_ID(TwitterAPI.ENDPOINT + "users"),
	USERS_BY(TwitterAPI.ENDPOINT + "users/by"),
	;
	
	public final String url;
	
	private TwitterEndpoint(String url) {
		this.url = url;
	}
	
	public String getURL(String... pathParams) {
		return String.format(url, (Object[]) pathParams);
	}
	
}
