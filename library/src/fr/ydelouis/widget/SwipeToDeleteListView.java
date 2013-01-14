package fr.ydelouis.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeToDeleteListView extends ListView
{
	private static final long ANIM_LENGTH = 300;
	private static final long ANIM_REFRESH = 50;
	private static final int NO_ITEM_DRAGGED = -1;
	
	private int draggedItemPosition = NO_ITEM_DRAGGED;
	private float draggedViewOffset = 0;
	private float deletedItemHeigth;
	private float deletedHeightPercentage = 1;
	private float lastMotionEventX;
	private float lastMotionEventY;
	private Drawable selector;
	private OnItemDeletedListener onItemDeletedListener;
	
	public SwipeToDeleteListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SwipeToDeleteListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		if(getId() == NO_ID)
			throw new RuntimeException("An id must be set in the XML layout for this DeletableListView");
		
		selector = getSelector();
		if(getSelector() == null)
			selector = getResources().getDrawable(android.R.drawable.list_selector_background);
	}
	
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(new DeletableListAdapter(adapter));
	}
	
	@Override
	public ListAdapter getAdapter() {
		if(super.getAdapter() == null)
			return null;
		return ((DeletableListAdapter) super.getAdapter()).getAdapter();
	}
	
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		if(draggedItemPosition != (Integer) child.getTag(getId()))
			return super.drawChild(canvas, child, drawingTime);
		
		float alpha = 1 - 3/2*Math.abs(draggedViewOffset / getWidth());
		if(alpha < 0)
			alpha = 0;
		RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.saveLayerAlpha(rect, (int) (alpha*255), Canvas.ALL_SAVE_FLAG);
		canvas.translate(draggedViewOffset, 0);
		boolean hasInvalidated = super.drawChild(canvas, child, drawingTime);
		canvas.translate(-draggedViewOffset, 0);
		canvas.restore();
		
		return hasInvalidated;
	}
	
	private boolean isToBeDeleted() {
		return Math.abs(draggedViewOffset) > getWidth()*1/2;
	}
	
	private void reset() {
		deletedItemHeigth = 0;
		deletedHeightPercentage = 1;
		draggedViewOffset = 0;
		draggedItemPosition = NO_ITEM_DRAGGED;
		notifyDataSetChanged();
	}
	
	private void startBackAnimation() {
		new CountDownTimer(ANIM_LENGTH, ANIM_REFRESH) {
			public void onTick(long millisUntilFinished) {
				if(draggedViewOffset > 0) {
					draggedViewOffset -= getWidth()/(ANIM_LENGTH/ANIM_REFRESH);
					if(draggedViewOffset < 0) {
						onFinish();
					}
				}
				if(draggedViewOffset < 0) {
					draggedViewOffset += getWidth()/(ANIM_LENGTH/ANIM_REFRESH);
					if(draggedViewOffset > 0) {
						onFinish();
					}
				}
				notifyDataSetChanged();
			}
			
			@Override
			public void onFinish() {
				reset();
			}
		}.start();
	}
	
	private void startDeletionAnimation() {
		new CountDownTimer(ANIM_LENGTH, ANIM_REFRESH) {
			public void onTick(long millisUntilFinished) {
				if(draggedViewOffset > 0) {
					draggedViewOffset += getWidth()/3/(ANIM_LENGTH/ANIM_REFRESH);
				}
				if(draggedViewOffset < 0) {
					draggedViewOffset -= getWidth()/3/(ANIM_LENGTH/ANIM_REFRESH);
				}
				notifyDataSetChanged();
			}
			
			@Override
			public void onFinish() {
				new CountDownTimer(ANIM_LENGTH, ANIM_REFRESH) {
					@Override
					public void onTick(long millisUntilFinished) {
						deletedHeightPercentage = ((float) millisUntilFinished) / ((float) ANIM_LENGTH);
						notifyDataSetChanged();
					}
					
					@Override
					public void onFinish() {
						int deletedPosition = draggedItemPosition;
						reset();
						if(onItemDeletedListener != null) 
							onItemDeletedListener.onItemDeleted(SwipeToDeleteListView.this, deletedPosition);
					}
				}.start();
			}
		}.start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(selector != null)
					setSelector(selector);
				startDragging(event);
				break;
			case MotionEvent.ACTION_MOVE:
				if(isDraggingLeftOrRight(event)) {
					setSelector(android.R.color.transparent);
					draggedViewOffset += event.getX() - lastMotionEventX;
					notifyDataSetChanged();
					saveLastMotionEvent(event);
					return true;
				} else if(isDraggingTopOrBottom(event))
					startBackAnimation();
				break;
			case MotionEvent.ACTION_UP:
				draggedViewOffset += event.getX() - lastMotionEventX;
				saveLastMotionEvent(event);
				if(isToBeDeleted())
					startDeletionAnimation();
				else
					startBackAnimation();
				if(Math.abs(draggedViewOffset) > 20)
					return true;
		}
		return super.onTouchEvent(event);
	}
	
	private void startDragging(MotionEvent event) {
		draggedItemPosition = pointToPosition((int) event.getX(), (int) event.getY());
		draggedViewOffset = 0;
		saveLastMotionEvent(event);
	}
	
	private boolean isDraggingLeftOrRight(MotionEvent event) {
		return Math.abs(event.getX() - lastMotionEventX) > Math.abs(event.getY() - lastMotionEventY);
	}
	
	private boolean isDraggingTopOrBottom(MotionEvent event) {
		return Math.abs(event.getY() - lastMotionEventY) > 20;
	}
	
	private void saveLastMotionEvent(MotionEvent event) {
		lastMotionEventX = event.getX();
		lastMotionEventY = event.getY();
	}
	
	public void notifyDataSetChanged() {
		((DeletableListAdapter) super.getAdapter()).notifyDataSetChanged();
	}
	
	public void setOnItemDeletedListener(OnItemDeletedListener onItemDeletedListener) {
		this.onItemDeletedListener = onItemDeletedListener;
	}
	
	public interface OnItemDeletedListener {
		public void onItemDeleted(SwipeToDeleteListView listView, int position);
	}
	
	private class DeletableListAdapter extends BaseAdapter
	{
		private ListAdapter adapter;
		
		public DeletableListAdapter(ListAdapter adapter) {
			this.adapter = adapter;
		}
		
		public ListAdapter getAdapter() {
			return adapter;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			view = adapter.getView(position, view, parent);
			view.setTag(SwipeToDeleteListView.this.getId(), position);
			if(position == draggedItemPosition) {
				if(deletedItemHeigth == 0)
					deletedItemHeigth = view.getHeight();
				view.getLayoutParams().height = (int) (deletedItemHeigth*deletedHeightPercentage);
			} else if(view.getLayoutParams() != null)
				view.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
			return view;
		}
		
		public int getCount() {
			return adapter.getCount();
		}
		public Object getItem(int position) {
			return adapter.getItem(position);
		}
		public long getItemId(int position) {
			return adapter.getItemId(position);
		}
	}
}
