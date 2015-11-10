package com.example.mobestdraglistview;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

/**
 * 
 * @author mo
 *
 */
public final class Menu {
	private List<MenuItem> mLeftMenuItems;
	private List<MenuItem> mRightMenuItems;
	
	private int mItemHeight;
	private Drawable itemBackGroundDrawable;
	private boolean mWannaOver = true;
	
	public Menu(int itemHeight, Drawable itemBackGroundDrawable){
		this(itemHeight, itemBackGroundDrawable,true);
	}
	public Menu(int itemHeight, Drawable itemBackGroundDrawable, boolean wannaOver){
		this.mItemHeight = itemHeight;
		this.itemBackGroundDrawable = itemBackGroundDrawable;
		this.mWannaOver = wannaOver;
		mLeftMenuItems = new ArrayList<MenuItem>();
		mRightMenuItems = new ArrayList<MenuItem>();
	}
	
	public int getItemHeight(){
		return mItemHeight;
	}
	
	public Drawable getItemBackGroundDrawable(){
		return itemBackGroundDrawable;
	}
	
	public boolean isWannaOver(){
		return mWannaOver;
	}
	
	public void addItem(MenuItem menuItem){
		if(menuItem.direction ==  MenuItem.DIRECTION_LEFT){
			mLeftMenuItems.add(menuItem);
		}else{
			mRightMenuItems.add(menuItem);
		}
	}
	
	public boolean removeItem(MenuItem menuItem){
		if(menuItem.direction == MenuItem.DIRECTION_LEFT){
			return mLeftMenuItems.remove(menuItem);
		}else{
			return mRightMenuItems.remove(menuItem);
		}
	}
	
	protected int getTotalBtnLength(int direction){
		int total = 0;
		if(direction == MenuItem.DIRECTION_LEFT){
			for(MenuItem menuItem : mLeftMenuItems){
				total += menuItem.width;
			}
			return total;
		}else{
			for(MenuItem menuItem : mRightMenuItems){
				total += menuItem.width;
			}
			return total;
		}
	}
	
	// 这个函数并不是十分安全, 因为获取到List之后自己操作add或者remove 的话btn总长度不会有操作变化
	
	protected List<MenuItem> getMenuItems(int direction){
		if(direction == MenuItem.DIRECTION_LEFT){
			return mLeftMenuItems;
		}else{
			return mRightMenuItems;
		}
	}
	
}

























