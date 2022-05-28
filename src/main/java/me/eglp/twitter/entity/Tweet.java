package me.eglp.twitter.entity;

import java.time.OffsetDateTime;

import me.mrletsplay.mrcore.json.JSONObject;
import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class Tweet implements JSONConvertible {
	
	@JSONValue
	private String id;
	
	@JSONValue
	private String text;
	
	private OffsetDateTime createdAt;
	
	@JSONConstructor
	private Tweet() {}
	
	public String getID() {
		return id;
	}
	
	public String getText() {
		return text;
	}
	
	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}
	
	@Override
	public void preDeserialize(JSONObject object) {
		createdAt = OffsetDateTime.parse(object.getString("created_at"));
	}

}
