package me.eglp.twitter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.eglp.apibase.APIBase;
import me.eglp.apibase.util.Endpoint;
import me.eglp.apibase.util.RequestParameters;
import me.eglp.twitter.entity.Tweet;
import me.eglp.twitter.entity.TwitterUser;
import me.eglp.twitter.ratelimit.Ratelimiter;
import me.eglp.twitter.util.TwitterEndpoint;
import me.eglp.twitter.util.TwitterException;
import me.mrletsplay.mrcore.http.HttpRequest;
import me.mrletsplay.mrcore.http.HttpResult;
import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConverter;

public class TwitterAPI extends APIBase {
	
	public static final String
		ENDPOINT = "https://api.twitter.com/2/";

	private String oAuthToken;
	
	public TwitterAPI(String oAuthToken) {
		this.oAuthToken = oAuthToken;
	}
	
	@Override
	public void onRequest(Endpoint endpoint, RequestParameters parameters, HttpRequest request) {
		Ratelimiter.waitForRatelimitIfNeeded();
		request.setHeader("Authorization", "Bearer " + oAuthToken);
	}
	
	@Override
	public void onRequestResult(Endpoint endpoint, RequestParameters parameters, HttpRequest request, HttpResult result) {
		if(result.getHeaders().firstValue("x-rate-limit-reset") != null) {
			String ratelimitReset = result.getHeaders().firstValue("x-rate-limit-reset").orElse(null);
			String ratelimitRemaining = result.getHeaders().firstValue("x-rate-limit-remaining").orElse("0");
			Ratelimiter.setRatelimitReset("0".equals(ratelimitRemaining), Long.parseLong(ratelimitReset));
		}
	}
	
	public TwitterUser getUserByUsername(String name) {
		JSONObject obj = makeRequest(me.eglp.twitter.util.TwitterEndpoint.USER_BY_USERNAME, new RequestParameters().put("username", name)).asJSONObject();
		if(obj.containsKey("errors")) throw new TwitterException("Got error response: " + obj.toString());
		return JSONConverter.decodeObject(obj.getJSONObject("data"), TwitterUser.class);
	}
	
	public List<TwitterUser> getUsersByUsernames(List<String> names) {
		JSONObject obj = makeRequest(TwitterEndpoint.USERS_BY, new RequestParameters().put("usernames", names.stream().collect(Collectors.joining(",")))).asJSONObject();
		if(obj.containsKey("errors")) throw new TwitterException("Got error response: " + obj.toString());
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, TwitterUser.class))
				.collect(Collectors.toList());
	}
	
	public TwitterUser getUserByID(String id) {
		JSONObject obj = makeRequest(TwitterEndpoint.USER_BY_ID, new RequestParameters().put("id", id)).asJSONObject();
		if(obj.containsKey("errors")) throw new TwitterException("Got error response: " + obj.toString());
		return JSONConverter.decodeObject(obj.getJSONObject("data"), TwitterUser.class);
	}
	
	public List<TwitterUser> getUsersByIDs(List<String> ids) {
		JSONObject obj = makeRequest(TwitterEndpoint.USERS_BY_ID, new RequestParameters().put("ids", ids.stream().collect(Collectors.joining(",")))).asJSONObject();
		if(obj.containsKey("errors")) throw new TwitterException("Got error response: " + obj.toString());
		if(!obj.containsKey("data")) return null;
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, TwitterUser.class))
				.collect(Collectors.toList());
	}
	
	public List<Tweet> getTimelineSince(String userID, String lastTweetID) {
		JSONObject obj = makeRequest(TwitterEndpoint.TWEETS, new RequestParameters().put("user", userID).put("since", lastTweetID).put("results", "100")).asJSONObject();
		if(obj.containsKey("errors")) throw new TwitterException("Got error response: " + obj.toString());
		if(!obj.containsKey("data")) return Collections.emptyList();
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, Tweet.class))
				.collect(Collectors.toList());
	}
	
	public List<Tweet> getTimeline(String userID, int tweetCount) {
		if(tweetCount > 100 || tweetCount < 5) throw new IllegalArgumentException("tweetCount cannot be more than 100 or less than 5");
		JSONObject obj = makeRequest(TwitterEndpoint.TWEETS, new RequestParameters().put("user", userID).put("results", String.valueOf(tweetCount))).asJSONObject();
		if(obj.containsKey("errors")) throw new TwitterException("Got error response: " + obj.toString());
		if(!obj.has("data")) return Collections.emptyList();
		return obj.getJSONArray("data").stream()
				.map(t -> JSONConverter.decodeObject((JSONObject) t, Tweet.class))
				.collect(Collectors.toList());
	}
	
	public Tweet getLatestTweet(String userID) {
		List<Tweet> t = getTimeline(userID, 5);
		if(t.isEmpty()) return null;
		return t.get(0);
	}
	
}
