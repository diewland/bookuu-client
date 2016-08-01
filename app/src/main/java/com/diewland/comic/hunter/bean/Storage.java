package com.diewland.comic.hunter.bean;

import java.util.ArrayList;

import org.apache.pig.impl.util.ObjectSerializer;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Storage {

	String LABEL_CORE_DATA = "STORAGE_LABLE";

	SharedPreferences prefs;

	public Storage(SharedPreferences prefs){
		this.prefs = prefs;
	}

	public ArrayList<Item> load() throws Exception{
        String data_str = this.prefs.getString(LABEL_CORE_DATA, ObjectSerializer.serialize(new ArrayList<Item>()));
        return (ArrayList<Item>) ObjectSerializer.deserialize(data_str);
	}

	public void save(ArrayList<Item> core_data) throws Exception{
		Editor editor = prefs.edit();
		editor.putString(LABEL_CORE_DATA, ObjectSerializer.serialize(core_data));
		editor.commit();
	}

}