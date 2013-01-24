package fr.ydelouis.widget;

import android.R;
import android.graphics.drawable.ColorDrawable;
import fr.ydelouis.widget.ItemState.State;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeToDeleteListView extends ListView
{
	private static final long		ANIM_LENGTH			= 300;
	private static final int		NO_ITEM_DRAGGED		= -1;

	private SwipeToDeleteAdapter	adapter;
	private int						draggedItemPosition	= NO_ITEM_DRAGGED;
	private MotionEvent				lastMotionEvent;
	private Drawable				selector;
	private OnItemDeletedListener	onItemDeletedListener;
	private long animLength = ANIM_LENGTH;
	private boolean					isConfirmNeeded			= false;

	public SwipeToDeleteListView(Context context) {
		super(context);
		init();
	}

	public SwipeToDeleteListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeToDeleteListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		selector = getSelector();
		if(selector == null)
			selector = getResources().getDrawable(android.R.drawable.list_selector_background);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		this.adapter = new SwipeToDeleteAdapter(this, adapter);
		super.setAdapter(this.adapter);
	}

	@Override
	public ListAdapter getAdapter() {
		if(adapter == null)
			return null;
		return adapter.getAdapter();
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		ItemState itemState = (ItemState) child.getTag(getId());
		if(itemState != null && itemState.getState() == State.Dragged)
			return drawDraggedChild(canvas, child, drawingTime);
		else
			return super.drawChild(canvas, child, drawingTime);
	}

	private boolean drawDraggedChild(Canvas canvas, View child, long drawingTime) {
		ItemState itemState = (ItemState) child.getTag(getId());
		canvas.save(Canvas.ALL_SAVE_FLAG);
		setAlpha(canvas, itemState);
		setTranslation(canvas, itemState);
		boolean hasInvalidated = super.drawChild(canvas, child, drawingTime);
		canvas.restore();
		return hasInvalidated;
	}

	private void setAlpha(Canvas canvas, ItemState itemState) {
		float alphaPercentage = 1f - 4f / 3f * Math.abs(itemState.getDragPercentage());
		if(alphaPercentage < 0)
			alphaPercentage = 0;
		int alpha = (int) (255 * alphaPercentage);
		RectF rect = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
		canvas.saveLayerAlpha(rect, alpha, Canvas.ALL_SAVE_FLAG);
	}

	private void setTranslation(Canvas canvas, ItemState itemState) {
		float translationX = itemState.getDragPercentage() * getWidth();
		canvas.translate(translationX, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(adapter == null)
			return super.onTouchEvent(event);

		boolean handled;
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(selector != null)
					setSelector(selector);
				startDragging(event);
				handled = false;
				break;
			case MotionEvent.ACTION_MOVE:
				if(isDraggingHorizontally(event)) {
					setSelector(android.R.color.transparent);
					drag(event);
					handled = true;
				} else {
					if(isDraggingVertically(event))
						startBackAnimation();
					handled = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				drag(event);
				stopDragging();
				handled = hasBeenDragged();
				setSelector(R.color.transparent);
				break;
			default:
				handled = hasBeenDragged();
				break;
		}
		return handled || super.onTouchEvent(event);
	}

	private void saveLastMotionEvent(MotionEvent event) {
		lastMotionEvent = MotionEvent.obtain(event);
	}

	private ItemState getDraggedItemState() {
		if(draggedItemPosition == NO_ITEM_DRAGGED)
			return null;
		return adapter.getItemState(draggedItemPosition);
	}

	private void startDragging(MotionEvent event) {
		draggedItemPosition = pointToPosition((int) event.getX(), (int) event.getY());
		ItemState itemState = adapter.getItemState(draggedItemPosition);
		if(itemState != null) {
			itemState.setState(State.Dragged);
			saveLastMotionEvent(event);
		}
	}

	private void drag(MotionEvent event) {
		ItemState itemState = getDraggedItemState();
		if(itemState != null && itemState.getState() == State.Dragged) {
			float dragOffset = event.getX() - lastMotionEvent.getX();
			float dragPercentage = itemState.getDragPercentage();
			dragPercentage += dragOffset / getWidth();
			itemState.setDragPercentage(dragPercentage);
			invalidate();
		}
		saveLastMotionEvent(event);
	}

	private void stopDragging() {
		if(isToBeDeleted())
			startDeletionAnimation();
		else
			startBackAnimation();
	}

	private boolean hasBeenDragged() {
		ItemState itemState = getDraggedItemState();
		if(itemState == null || itemState.getState() != State.Dragged)
			return false;
		float dragPercentage = itemState.getDragPercentage();
		return Math.abs(dragPercentage * getWidth()) > 20;
	}

	private boolean isToBeDeleted() {
		ItemState itemState = getDraggedItemState();
		if(itemState == null || itemState.getState() != State.Dragged)
			return false;
		float dragPercentage = itemState.getDragPercentage();
		return Math.abs(dragPercentage) > 0.5;
	}

	private boolean isDraggingHorizontally(MotionEvent event) {
		float deltaX = Math.abs(event.getX() - lastMotionEvent.getX());
		float deltaY = Math.abs(event.getY() - lastMotionEvent.getY());
		return deltaX > deltaY;
	}

	private boolean isDraggingVertically(MotionEvent event) {
		float deltaY = Math.abs(event.getY() - lastMotionEvent.getY());
		return deltaY > 20;
	}

	private void startBackAnimation() {
		ItemState draggedItemState = getDraggedItemState();
		if(draggedItemState != null)
			new BackAnimation(this, draggedItemState, animLength).start();
	}

	private void startDeletionAnimation() {
		ItemState draggedItemState = getDraggedItemState();
		if(draggedItemState != null)
			new DeleteAnimation(this, draggedItemState, animLength).start();
	}

	private void startDeletionConfirmedAnimation(ItemState itemState) {
		itemState.setState(State.DeletedConfirmed);
		new DeletionConfirmedAnimation(this, itemState, animLength).start();
	}

	void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	void onItemDeleted(ItemState itemState) {
		if(isConfirmNeeded) {
			if(onItemDeletedListener != null)
				onItemDeletedListener.onItemDeleted(this, itemState.getPosition());
			itemState.setState(ItemState.State.Deleted);
			notifyDataSetChanged();
		} else {
			startDeletionConfirmedAnimation(itemState);
			invalidate();
		}
	}

	void onItemDeletionConfirmed(ItemState itemState) {
		if(onItemDeletedListener != null) {
			if(isConfirmNeeded)
				onItemDeletedListener.onItemDeletionConfirmed(this, itemState.getPosition());
			else
				onItemDeletedListener.onItemDeleted(this, itemState.getPosition());
		}
		adapter.onItemDeletionConfirmed(itemState);
	}

	public void setOnItemDeletedListener(OnItemDeletedListener onItemDeletedListener) {
		this.onItemDeletedListener = onItemDeletedListener;
	}

	public OnItemDeletedListener getOnItemDeletedListener() {
		return onItemDeletedListener;
	}

	public long getAnimLength() {
		return animLength;
	}

	public void setAnimLength(long animLength) {
		this.animLength = animLength;
	}

	public boolean isConfirmNeeded() {
		return isConfirmNeeded;
	}

	public void setConfirmNeeded(boolean confirmNeeded) {
		isConfirmNeeded = confirmNeeded;
	}

	public interface OnItemDeletedListener
	{
		public void onItemDeleted(SwipeToDeleteListView listView, int position);

		public void onItemDeletionConfirmed(SwipeToDeleteListView listView, int position);
	}
}
