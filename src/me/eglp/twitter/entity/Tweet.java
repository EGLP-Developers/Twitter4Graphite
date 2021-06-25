package me.eglp.twitter.entity;

import me.mrletsplay.mrcore.json.converter.JSONConstructor;
import me.mrletsplay.mrcore.json.converter.JSONConvertible;
import me.mrletsplay.mrcore.json.converter.JSONValue;

public class Tweet implements JSONConvertible {
	
	@JSONValue
	private String id;
	
	@JSONValue
	private String text;
	
	@JSONConstructor
	private Tweet() {}
	
	public String getID() {
		return id;
	}
	
	public String getText() {
		return text;
	}

}
