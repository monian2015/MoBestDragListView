package com.example.mobestdraglistview;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author mo
 *
 */
public class MoDemoActivity extends Activity implements SlideAndDragListView.OnListItemLongClickListener,
SlideAndDragListView.OnDragListener,SlideAndDragListView.OnSlideListener,
SlideAndDragListView.OnListItemClickListener,SlideAndDragListView.OnButtonClickListener{
	private Menu mMenu;
	private List<ApplicationInfo> mAppList;
	private static boolean EDITED = true; // 默认可编辑
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_me);
		initData();
		initMenu();
		initUiAndListener();
	}
	




	public void initMenu() {
		mMenu = new Menu((int)getResources().getDimension(R.dimen.slv_item_height),new ColorDrawable(Color.WHITE),true);
		mMenu.addItem(new MenuItem.Builder().setWidth((int)getResources().getDimension(R.dimen.slv_item_bg_btn_width))
				.setBackground(new ColorDrawable(Color.RED)).setText("One").setTextColor(Color.GRAY).setTextSize(
						(int)getResources().getDimension(R.dimen.txt_size)).build());
		mMenu.addItem(new MenuItem.Builder()
		.setWidth(
				(int) getResources().getDimension(
						R.dimen.slv_item_bg_btn_width))
		.setBackground(new ColorDrawable(Color.GREEN))
		.setText("Two")
		.setTextColor(Color.BLACK)
		.setTextSize(
				(int) getResources().getDimension(R.dimen.txt_size))
		.build());
mMenu.addItem(new MenuItem.Builder()
		.setWidth(
				(int) getResources().getDimension(
						R.dimen.slv_item_bg_btn_width) + 30)
		.setBackground(new ColorDrawable(Color.BLUE))
		.setText("Three")
		.setDirection(MenuItem.DIRECTION_RIGHT)
		.setTextColor(Color.BLACK)
		.setTextSize(
				(int) getResources().getDimension(R.dimen.txt_size))
		.build());
mMenu.addItem(new MenuItem.Builder()
		.setWidth(
				(int) getResources().getDimension(
						R.dimen.slv_item_bg_btn_width_img))
		.setBackground(new ColorDrawable(Color.BLACK))
		.setDirection(MenuItem.DIRECTION_RIGHT)
		.setIcon(getResources().getDrawable(R.drawable.ic_launcher))
		.build());
	}






	public void initData() {
			mAppList = getPackageManager().getInstalledApplications(0);
	}


	public void initUiAndListener() {
			SlideAndDragListView listView = (SlideAndDragListView)findViewById(R.id.lv_edit_me);
			listView.setMenu(mMenu);
			SlideAndDragListView.setEDITED(EDITED);
			listView.setAdapter(mAdapter);
			listView.setOnListItemLongClickListener(this);
			listView.setOnDragListener(this,mAppList);
			listView.setOnListItemClickListener(this);
			listView.setOnButtonClickListener(this);
			
	}
	
	private BaseAdapter mAdapter = new BaseAdapter(){

		@Override
		public int getCount() {
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CustomViewHolder cvh;
			if(convertView == null){
				cvh = new CustomViewHolder();
				convertView = LayoutInflater.from(MoDemoActivity.this).inflate(R.layout.item_custom_me, null);
				cvh.imgLogo = (ImageView) convertView
						.findViewById(R.id.img_item_edit);
				cvh.txtName = (TextView) convertView
						.findViewById(R.id.txt_item_edit);
				cvh.imgLogo2 = (ImageView) convertView
						.findViewById(R.id.img_item_edit2);
				convertView.setTag(cvh);
			}else{
				cvh = (CustomViewHolder) convertView.getTag();
			}
				ApplicationInfo item = (ApplicationInfo) this.getItem(position);
				cvh.txtName.setText(item.loadLabel(getPackageManager()));
				cvh.imgLogo.setImageDrawable(item.loadIcon(getPackageManager()));
				cvh.imgLogo2.setImageDrawable(item.loadIcon(getPackageManager()));
				return convertView;
		
		}
		
		class CustomViewHolder{
			public ImageView imgLogo;
			public TextView txtName;
			public ImageView imgLogo2;
		}
		
	};




	@Override
	public void onClick(View v, int itemPosition, int buttonPosition,
			int direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListItemClick(View v, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSlideOpen(View view, View parentView, int position,
			int direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSlideClose(View view, View parentView, int position,
			int direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragViewStart(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragViewMoving(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDragViewDown(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListItemLongClick(View view, int position) {
		// TODO Auto-generated method stub
		
	}
	
}

