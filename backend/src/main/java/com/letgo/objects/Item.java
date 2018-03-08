package com.letgo.objects;

import com.letgo.utils.Constants;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.List;

public class Item {

	private String id;
	private String name;
	private String category;
	private String username;
	private String desc;
	private long price;
	private boolean sold;
	private String uri;
	private byte[] image = null;

	public Item(String id) {
		this.id = id;
	}

	public Item(String id, String name, String category, long price, String desc, byte[] image, String username, boolean sold) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.desc = desc;
		this.username = username;
		this.price = price;
		this.sold = sold;
		this.image = image;
	}

	public Item(String id, String name, String category, long price, String desc, byte[] image, String username) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.desc = desc;
		this.username = username;
		this.price = price;
		this.image = image;
		sold = false;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public String getUser() {
		return username;
	}

	public void setUser(String username) {
		this.username = username;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}


	public JSONObject toJson () throws JSONException{
		JSONObject object;

		object = new JSONObject();
		object.put(Constants.ID, id);
		object.put(Constants.NAME, name);
		object.put(Constants.DESCRIPTION, desc);
		object.put(Constants.CATEGORY, category);
		object.put(Constants.PRICE, price);
		object.put(Constants.USERNAME, username);
		object.put(Constants.SOLD, sold);
		object.put(Constants.HAS_IMAGE, isImageExists());

		return object;
	}


	public static String toJson(List<Item> items){

		try {
			JSONObject object = new JSONObject();
			JSONArray arr = new JSONArray();

			if (items == null) {
				return null;
			}

			if (items.size() == 0) {
				return null;
			}


			for (Item it : items) {
				if (it != null) {
					arr.add(it.toJson());
				}
			}

			object.put(Constants.ITEMS, arr);
			return object.toString();

		} catch (JSONException e) {
			return "";
		}

	}


	public void setSold(int sold) {
		if (sold == 1)
			this.sold = true;

		else if (sold == 0)
			this.sold = false;
	}

	private boolean isImageExists() {
		if (image == null || image.length == 0) {
			return false;
		}
		return true;
	}


	public boolean isSold() {
		return sold;
	}

	public String getUri() {
		return uri;
	}

	public String getCategory() {
		return category;
	}

	public byte[] getImage() {
		return image;
	}
}
