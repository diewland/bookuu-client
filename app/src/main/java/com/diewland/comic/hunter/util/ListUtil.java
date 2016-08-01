package com.diewland.comic.hunter.util;

import java.util.ArrayList;

import com.diewland.comic.hunter.bean.Item;

import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListUtil {

	ListView list;
	ArrayAdapter<String> adapter;
	ArrayList<String> list_data;

	public ListUtil(ListView list, ArrayAdapter<String> adapter, ArrayList<String> list_data) {
		this.list = list;
		this.adapter = adapter;
		this.list_data = list_data;
	}

	public void reload(ArrayList<Item> core_data){
		list_data.clear();
		for(int i=core_data.size()-1; i>=0; i--){ // revert loop, latest item at top
			Item item = core_data.get(i);
			list_data.add(item.getTitle() + "\n" + item.getStringTime());
		}
		list.invalidateViews();
	}

	public void add(String title){
		adapter.add(title);
	}

	public void remove(int index){
        adapter.remove(list_data.get(index));
        adapter.notifyDataSetChanged();
	}

}