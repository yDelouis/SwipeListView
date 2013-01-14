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
	private static final long		ANIM_LENGTH			= 300;
	private static final long		ANIM_REFRESH		= 50;
	private static final int		NO_ITEM_DRAGGED		= -1;

	private int						draggedItemPosition	= NO_ITEM_DRAGGED;
	private float					dragPercentage		= 0;
	private MotionEvent				lastMotionEvent;
	private Drawable				selector;
	private OnItemDeletedListener	onItemDeletedListener;

	public SwipeToDeleteListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeToDeleteListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		selector = getSelector();
		if(getSelector() == null)
			selector = getResources().getDrawable(android.R.drawable.list_selector_background);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(new SwipeToDeleteAdapter(adapter));
	}

	@Override
	public ListAdapter getAdapter() {
		if(super.getAdapter() == null)
			return null;
		return ((SwipeToDeleteAdapter) super.getAdapter()).getAdapter();
	}

	public void notifyDataSetChanged() {
		((SwipeToDeleteAdapter) super.getAdapter()).notifyDataSetChanged();
	}

	public void setOnItemDeletedListener(OnItemDeletedListener onItemDeletedListener) {
		this.onItemDeletedListener = onItemDeletedListener;
	}

	public OnItemDeletedListener getOnItemDeletedListener() {
		return onItemDeletedListener;
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		if(draggedItemPosition != (Integer) child.getTag(getId()))
			return super.drawChild(canvas, child, drawingTime);

		return drawDraggedChild(canvas, child, drawingTime);
	}

	private boolean drawDraggedChild(Canvas canvas, View child, long drawingTime) {
		canvas.save(Canvas.ALL_SAVE_FLAG);
		setAlpha(canvas);
		setTranslation(canvas);
		boolean hasInvalidated = super.drawChild(canvas, child, drawingTime);
		canvas.restore();
		return hasInvalidated;
	}

	private void setAlpha(Canvas canvas) {
		float alphaPercentage = 1f - 4f / 3f * Math.abs(dragPercentage);
		if(alphaPercentage < 0)
			alphaPercentage = 0;
		int alpha = (int) (255 * alphaPercentage);
		RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.saveLayerAlpha(rect, alpha, Canvas.ALL_SAVE_FLAG);
	}

	private void setTranslation(Canvas canvas) {
		float translationX = dragPercentage * getWidth();
		canvas.translate(translationX, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled;
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(selector != null)
					setSelector(selector);
				startDragging(event);
				handled = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if(isDraggingLeftOrRight(event)) {
					setSelector(android.R.color.transparent);
					drag(event);
					handled = true;
				} else if(isDraggingTopOrBottom(event)) {
					startBackAnimation();
					handled = false;
				} else {
					handled = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				drag(event);
				stopDragging();
				handled = Math.abs(dragPercentage * getWidth()) > 20;
				break;
			default:
				handled = false;
				break;
		}
		return handled || super.onTouchEvent(event);
	}

	private void saveLastMotionEvent(MotionEvent event) {
		lastMotionEvent = MotionEvent.obtain(event);
	}

	private void startDragging(MotionEvent event) {
		draggedItemPosition = pointToPosition((int) event.getX(), (int) event.getY());
		dragPercentage = 0;
		saveLastMotionEvent(event);
	}

	private void drag(MotionEvent event) {
		float draggedViewOffset = event.getX() - lastMotionEvent.getX();
		dragPercentage += draggedViewOffset / getWidth();
		notifyDataSetChanged();
		saveLastMotionEvent(event);
	}

	private void stopDragging() {
		if(isToBeDeleted())
			startDeletionAnimation();
		else
			startBackAnimation();
	}

	private void reset() {
		dragPercentage = 0;
		draggedItemPosition = NO_ITEM_DRAGGED;
		notifyDataSetChanged();
	}

	private boolean isToBeDeleted() {
		return Math.abs(dragPercentage) > 0.5;
	}

	private boolean isDraggingLeftOrRight(MotionEvent event) {
		float deltaX = Math.abs(event.getX() - lastMotionEvent.getX());
		float deltaY = Math.abs(event.getY() - lastMotionEvent.getY());
		return deltaX > deltaY;
	}

	private boolean isDraggingTopOrBottom(MotionEvent event) {
		float deltaY = Math.abs(event.getY() - lastMotionEvent.getY());
		return deltaY > 20;
	}

	private void startBackAnimation() {
		new BackAnimator().start();
	}

	private void startDeletionAnimation() {
		new DeleteAnimator().start();
	}

	public interface OnItemDeletedListener
	{
		public void onItemDeleted(SwipeToDeleteListView listView, int position);
	}

	private class BackAnimator extends CountDownTimer
	{
		public BackAnimator() {
			super(ANIM_LENGTH, ANIM_REFRESH);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			float delta = 0.5f * ((float) ANIM_REFRESH) / ((float) ANIM_LENGTH);
			if(dragPercentage > 0)
				delta = -delta;
			float newDragPercentage = dragPercentage + delta;
			if(isChangingSign(dragPercentage, newDragPercentage))
				onFinish();
			else {
				dragPercentage = newDragPercentage;
				notifyDataSetChanged();
			}
		}

		@Override
		public void onFinish() {
			reset();
		}

		private boolean isChangingSign(float oldNumber, float newNumber) {
			return (oldNumber * newNumber) <= 0;
		}
	}

	private class DeleteAnimator extends CountDownTimer
	{
		private int	deletedPosition;

		public DeleteAnimator() {
			super(ANIM_LENGTH, ANIM_REFRESH);
			deletedPosition = draggedItemPosition;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			float delta = 0.5f * ((float) ANIM_REFRESH) / ((float) ANIM_LENGTH);
			if(dragPercentage < 0)
				delta = -delta;
			dragPercentage = dragPercentage + delta;
			if(isBeingInvisible())
				onFinish();
			else
				notifyDataSetChanged();
		}

		@Override
		public void onFinish() {
			if(onItemDeletedListener != null)
				onItemDeletedListener.onItemDeleted(SwipeToDeleteListView.this, deletedPosition);
			reset();
		}

		private boolean isBeingInvisible() {
			return Math.abs(dragPercentage) > 1;
		}
	}

	private class SwipeToDeleteAdapter extends BaseAdapter
	{
		private ListAdapter	adapter;

		public SwipeToDeleteAdapter(ListAdapter adapter) {
			this.adapter = adapter;
		}

		public ListAdapter getAdapter() {
			return adapter;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			view = adapter.getView(position, view, parent);
			view.setTag(SwipeToDeleteListView.this.getId(), position);
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
