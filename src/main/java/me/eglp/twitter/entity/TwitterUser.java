package me.eglp.twitter.entity;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class TwitterUser implements JSONConvertible {
	
	@JSONValue
	private String id;
	
	@JSONValue
	private String name;
	
	@JSONValue
	private String username;
	
	@JSONValue("profile_image_url")
	private String profileImageURL;
	
	@JSONConstructor
	private TwitterUser() {}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getProfileImageURL() {
		return profileImageURL;
	}

	public String getOriginalProfileImageURL() {
		return profileImageURL.replace("_normal", "");
	}
	
}
