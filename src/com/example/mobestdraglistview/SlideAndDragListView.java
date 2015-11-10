package com.example.mobestdraglistview;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
/**
 * 
 * @author monian
 *
 */
public class SlideAndDragListView<T> extends MoDragListView<T>   implements WrapperAdapter.OnAdapterSlideListenerProxy,
WrapperAdapter.OnAdapterButtonClickListenerProxy,Handler.Callback{
		
	// 是否可编辑
	private static boolean EDITED = false;  // 默认不可编辑
	
	public static boolean isEdited(){
		return EDITED;
	}
	
	public static void setEDITED(boolean eDITED){
		EDITED = eDITED;
	}
	
	// Handler 的 Message 信息
	private static final int MSG_WHAT_LONG_CLICK =1;
	
	//Handler 发送message 需要延迟的时间
	private static final long CLICK_LONG_TRIGGER_TIME = 1000; // 1s
	
	// onTouch 里边的状态
	private static final int STATE_NOTHING = -1; // 抬起状态
	private static final int STATE_DOWN = 0; // 按下状态
	private static final int STATE_LONG_CLICK =1; //长点击状态
	
	private static final int STATE_SCROLL = 2; // SCROLL状态
	
	private static final int STATE_LONG_CLICK_FINISH = 3; // 长点击已经触发完成
	private int mState = STATE_NOTHING;
	
	// 震动
	private Vibrator mVibrator;
	// handler
	private Handler mHandler;
  // 是否要出发itemClick
	private boolean mIsWannaTriggerClick = true;
	// 手指放下的坐标
	
	private int mXDown;
	private int mYDown;
	
	// Menu
	private Menu mMenu;
	
	// WrapperAdapter
	private WrapperAdapter mWrapperAdapter;
	
	// 监听器
	private OnSlideListener mOnSlideListener;
	
	private OnButtonClickListener mOnButtonClickListener;
	
	private OnListItemLongClickListener mOnListItemLongClickListener;
	
	private OnListItemClickListener mOnListItemClickListener;
	
	
	
	
	public SlideAndDragListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		mVibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
		mHandler = new Handler(this);
	}

	public SlideAndDragListView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public SlideAndDragListView(Context context) {
   this(context, null);
	}
	
	

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_WHAT_LONG_CLICK:
			if(mState == STATE_LONG_CLICK){  // 如果得到msg的时候state状态是Long Click 的话
				// 改为long click 触发完成
				mState = STATE_LONG_CLICK_FINISH;
				// 得到长点击的位置
				int position = msg.arg1;
				// 找到那个位置的view
				View view = getChildAt(position - getFirstVisiblePosition());
				// 如果设置了监听器的话, 就触发
				if(mOnListItemLongClickListener != null){
					mVibrator.vibrate(100);
					mOnListItemLongClickListener.onListItemLongClick(view, position);
				}
				boolean canDrag = scrollBackByDrag(position);
				if(EDITED){
					canDrag = true;
				}else{
					canDrag = false;
				}
				if(canDrag){
					setDragPosition(position);
				}
				
			}
			break;

	
		}
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 获取出坐标来
			mXDown = (int) ev.getX();
			mYDown = (int) ev.getY();
			
			// 当前state 状态为按下
			mState = STATE_DOWN;
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(fingerNotMove(ev) && mState != STATE_SCROLL){ // 手指的范围在50以内
				sendLongClickMessage(pointToPosition(mXDown, mYDown));
				mState = STATE_LONG_CLICK;
			}else if(fingerLeftAndRightMove(ev)){  // 上下范围在50, 主要检测左右滑动
				removeLongClickMessage();
				mState = STATE_SCROLL;
				
				// 将当前想要滑动哪一个传递给wrapperAdapter
				int position = pointToPosition(mXDown, mYDown);
				if(position != AdapterView.INVALID_POSITION){
					mWrapperAdapter.setSlideItemPosition(position);
				}
				
				if(EDITED){
					// 将事件传递下去
					return super.dispatchTouchEvent(ev);
				}else{
					return false;
				}
			}
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(mState == STATE_DOWN || mState ==STATE_LONG_CLICK){
				int position = pointToPosition(mXDown, mYDown);
				// 是否ScrollBack了, 是的话就不去执行onListItemClick 操作了
				boolean bool = scrollBack(position);
				if(bool){
					removeLongClickMessage();
					mState = STATE_NOTHING;
					break;
				}
				if(mOnListItemClickListener != null && mIsWannaTriggerClick){
					View v = getChildAt(position - getFirstVisiblePosition());
					mOnListItemClickListener.onListItemClick(v, position);
				}
			}
			removeLongClickMessage();
			mState = STATE_NOTHING;
			break;
			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
		
	}
	/**
	 * 将滑开的item归位
	 * @param position
	 * @return  true --> 有归位操作, false --> 没有归位操作, 也就是没有滑开的item
	 */
	private boolean scrollBack(int position){
		// 是不是当前花开的这个
		if(mWrapperAdapter.getSlideItemPosition() == position){
			return true;
		}else if(mWrapperAdapter.getSlideItemPosition() != -1){
			mWrapperAdapter.returnSlideItemPosition();
			return true;
		}
		return false;
		
	}
	

	
	// remove掉message
	private void removeLongClickMessage(){
		if(mHandler.hasMessages(MSG_WHAT_LONG_CLICK)){
			mHandler.removeMessages(MSG_WHAT_LONG_CLICK);
		}
	}
	
	
	// sendMessage
	private void sendLongClickMessage(int position){
		if(!mHandler.hasMessages(MSG_WHAT_LONG_CLICK)){
			Message message = new Message();
			message.what = MSG_WHAT_LONG_CLICK;
			message.arg1 = position;
			mHandler.sendMessageDelayed(message, CLICK_LONG_TRIGGER_TIME);
		}
	}
	
	
	// 左右得超出50, 上下不能超出50
	private boolean fingerLeftAndRightMove(MotionEvent ev){
		return ((ev.getX() - mXDown> 25 || ev.getX() - mXDown < -25)&& ev.getY() -mYDown < 25 && ev.getY() - mYDown >-25);
	}
	
	
	
	
	// 上下左右不能超过50
	private boolean fingerNotMove(MotionEvent ev){
		return (mXDown - ev.getX() < 25 && mXDown -ev.getX()>-25&&mYDown - ev.getY()<25 && mYDown-ev.getY()>-25);
	}
	
	// 设置Menu
	
	public void setMenu(Menu menu){
		mMenu = menu;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		if(mMenu == null){
			throw new IllegalArgumentException("先设置Menu");
		}
		
		mWrapperAdapter = new WrapperAdapter(getContext(), this, adapter,mMenu){
			public void onScrollStateChangedProxy(android.widget.AbsListView view, int scrollState) {
				if(scrollState == WrapperAdapter.SCROLL_STATE_IDLE){
					mIsWannaTriggerClick = true;
					
				}else{
					mIsWannaTriggerClick = false;
				}
			}
			@Override
			public void onScrollProxy(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		};
		mWrapperAdapter.setOnAdapterSlideListenerProxy(this);
		mWrapperAdapter.setOnAdapterButtonClickListenerProxy(this);
		setRawAdapter(adapter);
		super.setAdapter(mWrapperAdapter);
	}
	
	//设置item滑动监听器
	public void setOnSlideListener(OnSlideListener listener){
		mOnSlideListener = listener;
	}
	
	/**
	 * 用于drag 的ScrollBack 逻辑操作
	 * @param position
	 * @return true  --> 可以drag false --> 不能drag
	 */
	private boolean scrollBackByDrag(int position){
		// 是不是当前滑开的这个
		if(mWrapperAdapter.getSlideItemPosition() == position){
			return false;
		}else if(mWrapperAdapter.getSlideItemPosition() != -1){
			mWrapperAdapter.returnSlideItemPosition();
			return true;
		}
		return true;
	}
	
	
	// item中的button 监听器
	public interface OnButtonClickListener{
		/**
		 * 点击事件
		 * @param v 
		 * @param itemPosition
		 *       第几个item
		 * @param buttonPosition
		 * 		第几个button
		 * @param direction
		 *    方向
		 */
		void onClick(View v, int itemPosition, int buttonPosition, int direction);
	}
	
	
	// item的滑动的监听器
	public interface OnSlideListener{
		   // 当滑动开的时候触发
		
		void onSlideOpen(View view, View parentView, int position, int direction);
		
		// 当滑动归位的时候触发
		void onSlideClose(View view, View parentView, int position, int direction);
		
	}
	
	
	
	
	
	
	
	@Override
	public void onClick(View v, int itemPosition, int buttonPosition,
			int direction) {
			if(mOnButtonClickListener != null){
				mOnButtonClickListener.onClick(v, itemPosition, buttonPosition, direction);
			}
		
	}
	@Override
	public void setOnItemClickListener(
			android.widget.AdapterView.OnItemClickListener listener) {
	}
	
	// 设置监听器
	public void setOnListItemClickListener(OnListItemClickListener listener){
		mOnListItemClickListener = listener;
	}

	@Override
	public void onSlideOpen(View view, int position, int direction) {
				if(mOnSlideListener != null){
					mOnSlideListener.onSlideOpen(view, this, position, direction);
				}
		
	}

	@Override
	public void onSlideClose(View view, int position, int direction) {
			if(mOnSlideListener != null){
				mOnSlideListener.onSlideClose(view, this, position, direction);
			}
	}
	
	// 设置item中的button点击事件的监听器
	public void setOnButtonClickListener(
		OnButtonClickListener onButtonClickListener){
		mOnButtonClickListener = onButtonClickListener;
	}


	
	// 自己的点击事件
	public interface OnListItemClickListener{
		void onListItemClick(View v, int position);
	}
	
	@Override
	public void setOnItemLongClickListener(
			android.widget.AdapterView.OnItemLongClickListener listener) {
	}
	
	// 设置监听器
	public void setOnListItemLongClickListener(
		OnListItemLongClickListener listener){
		mOnListItemLongClickListener = listener;
	}
	// 自己写的长点击事件
	public interface OnListItemLongClickListener{
		void onListItemLongClick(View view, int position);
	}
	
	public interface setEdit{
		
	}

	
	

}































