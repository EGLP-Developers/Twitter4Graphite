package me.eglp.twitter.util;

import me.eglp.apibase.util.DefaultRequestMethod;
import me.eglp.apibase.util.Endpoint;
import me.eglp.apibase.util.EndpointDescriptor;
import me.eglp.twitter.TwitterAPI;

public enum TwitterEndpoint implements Endpoint {

	USER_BY_USERNAME(EndpointDescriptor.builder(DefaultRequestMethod.GET, TwitterAPI.ENDPOINT + "users/by/username/{username}")
			.query("user.fields", "profile_image_url")
			.create()),
	TWEETS(EndpointDescriptor.builder(DefaultRequestMethod.GET, TwitterAPI.ENDPOINT + "users/{user}/tweets")
			.query("exclude", "replies,retweets")
			.query("tweet.fields", "created_at")
			.dynamicQuery("max_results", "results")
			.dynamicQuery("since_id", "since")
			.create()),
	USER_BY_ID(EndpointDescriptor.builder(DefaultRequestMethod.GET, TwitterAPI.ENDPOINT +  "users/%s")
			.query("user.fields", "profile_image_url")
			.create()),
	USERS_BY_ID(EndpointDescriptor.builder(DefaultRequestMethod.GET, TwitterAPI.ENDPOINT + "users")
			.query("user.fields", "profile_image_url")
			.create()),
	USERS_BY(EndpointDescriptor.builder(DefaultRequestMethod.GET, TwitterAPI.ENDPOINT + "users/by")
			.query("user.fields", "profile_image_url")
			.dynamicQuery("usernames", "usernames")
			.create()),
	;
	
	private final EndpointDescriptor descriptor;
	
	private TwitterEndpoint(EndpointDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public EndpointDescriptor getDescriptor() {
		return descriptor;
	}

}
