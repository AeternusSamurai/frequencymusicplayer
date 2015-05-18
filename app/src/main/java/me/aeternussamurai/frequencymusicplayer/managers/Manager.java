package me.aeternussamurai.frequencymusicplayer.managers;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Manager<T, K, S> {
	protected HashMap<T, K> list;

	public abstract HashMap<T, K> getList();
	
	public abstract ArrayList<T> getManagerTags();
	
	public abstract K getItemByTag(T tag);
	
	public abstract void add(S s);
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
}
