package me.eglp.twitter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.eglp.twitter.entity.Tweet;
import me.eglp.twitter.entity.TwitterUser;
import me.eglp.twitter.ratelimit.Ratelimiter;
import me.eglp.twitter.util.TwitterEndpoint;
import me.mrletsplay.mrcore.http.HttpGet;
import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.http.HttpResult;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConverter;

public class TwitterAPI {
	
	public static final String
		ENDPOINT = "https://api.twitter.com/2/";

	private String oAuthToken;
	
	public TwitterAPI(String oAuthToken) {
		this.oAuthToken = oAuthToken;
	}
	
	public TwitterUser getUserByUsername(String name) {
		JSONObject obj = makeGetRequest(TwitterEndpoint.USER_BY_USERNAME.getURL(name), "user.fields", "profile_image_url");
		if(obj.containsKey("errors")) return null;
		return JSONConverter.decodeObject(obj.getJSONObject("data"), TwitterUser.class);
	}
	
	public List<TwitterUser> getUsersByUsernames(List<String> names) {
		JSONObject obj = makeGetRequest(TwitterEndpoint.USERS_BY.getURL(), "user.fields", "profile_image_url", "usernames", names.stream().collect(Collectors.joining(",")));
		if(obj.containsKey("errors")) return null;
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, TwitterUser.class))
				.collect(Collectors.toList());
	}
	
	public TwitterUser getUserByID(String id) {
		JSONObject obj = makeGetRequest(TwitterEndpoint.USER_BY_ID.getURL(id), "user.fields", "profile_image_url");
		if(obj.containsKey("errors")) return null;
		return JSONConverter.decodeObject(obj.getJSONObject("data"), TwitterUser.class);
	}
	
	public List<TwitterUser> getUsersByIDs(List<String> ids) {
		JSONObject obj = makeGetRequest(TwitterEndpoint.USERS_BY_ID.getURL(), "user.fields", "profile_image_url", "ids", ids.stream().collect(Collectors.joining(",")));
		if(obj.containsKey("errors")) return null;
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, TwitterUser.class))
				.collect(Collectors.toList());
	}
	
	public List<Tweet> getTimelineSince(String userID, String lastTweetID) {
		JSONObject obj = makeGetRequest(TwitterEndpoint.TWEETS.getURL(userID), "exclude", "replies,retweets", "since_id", lastTweetID, "max_results", "100", "tweet.fields", "text");
		if(obj.containsKey("errors")) return Collections.emptyList();
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, Tweet.class))
				.collect(Collectors.toList());
	}
	
	public List<Tweet> getTimeline(String userID, int tweetCount) {
		if(tweetCount > 100 || tweetCount < 1) throw new IllegalArgumentException("tweetCount cannot be more than 100 or less than 1");
		JSONObject o = makeGetRequest(TwitterEndpoint.TWEETS.getURL(userID), "exclude", "replies,retweets", "max_results", String.valueOf(tweetCount));
		if(o.containsKey("errors")) return Collections.emptyList();
		return o.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, Tweet.class))
				.collect(Collectors.toList());
	}
	
	public Tweet getLatestTweet(String userID) {
		List<Tweet> t = getTimeline(userID, 1);
		if(t.isEmpty()) return null;
		return t.get(0);
	}
	
	public synchronized JSONObject makeGetRequest(String endpoint, String... queryParams) {
		Ratelimiter.waitForRatelimitIfNeeded();
		HttpGet r = HttpRequest.createGet(endpoint);
		
		r.setHeaderParameter("Authorization", "Bearer " + oAuthToken);
		for(int i = 0; i < queryParams.length; i+=2) {
			r.addQueryParameter(queryParams[i], queryParams[i+1]);
		}
		
		try {
			HttpResult res = r.execute();
			
			if(res.getHeaderFields().containsKey("x-rate-limit-reset")) {
				String ratelimitReset = res.getHeaderFields().get("x-rate-limit-reset").get(0);
				String ratelimitRemaining = res.getHeaderFields().get("x-rate-limit-remaining").get(0);
				Ratelimiter.setRatelimitReset(ratelimitRemaining.equals("0"), Long.parseLong(ratelimitReset));
			}
			
			return res.asJSONObject();
		}catch(Exception e) {
			throw e;
		}
	}
	
}
