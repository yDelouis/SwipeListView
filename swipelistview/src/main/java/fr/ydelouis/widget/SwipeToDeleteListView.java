/**
 * Copyright 2013 Yoann Delouis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ydelouis.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.widget.ItemState.State;

public class SwipeToDeleteListView
		extends ListView {
	private static final long ANIM_LENGTH = 300;
	private long animLength = ANIM_LENGTH;
	private static final int NO_ITEM_DRAGGED = -1;
	private int draggedItemPosition = NO_ITEM_DRAGGED;
	private SwipeToDeleteAdapter adapter;
	private DeletedViewAdapter deletedViewAdapter;
	private MotionEvent lastMotionEvent;
	private Drawable selector;
	private OnItemClickListener onItemClickListener;
	private OnItemLongClickListener onItemLongClickListener;
	private OnItemDeletedListener onItemDeletedListener;
	private OnItemDeletionConfirmedListener onItemDeletionConfirmedListener;
	private boolean isConfirmNeeded = false;

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
		if (selector == null)
			selector = getResources().getDrawable(android.R.drawable.list_selector_background);
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		ItemState itemState = (ItemState) child.getTag(getId());
		if (itemState != null && itemState.getState() == State.Dragged)
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
		float alphaPercentage = 1f - 2f * Math.abs(itemState.getDragPercentage());
		alphaPercentage = Math.max(alphaPercentage, 0.2f);
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
		if (adapter == null)
			return super.onTouchEvent(event);

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				onActionDown(event);
				break;
			case MotionEvent.ACTION_MOVE:
				onActionMove(event);
				break;
			case MotionEvent.ACTION_UP:
				onActionUp();
				break;
		}
		saveLastMotionEvent(event);
		return super.onTouchEvent(event);
	}

	private void saveLastMotionEvent(MotionEvent event) {
		lastMotionEvent = MotionEvent.obtain(event);
	}

	private void onActionDown(MotionEvent event) {
		enableOtherEvents();
		setDraggedItem(event);
	}

	private void setDraggedItem(MotionEvent event) {
		int position = pointToPosition((int) event.getX(), (int) event.getY());
		ItemState itemState = adapter.getItemState(position);
		if (itemState != null && itemState.getState() == State.Normal) {
			draggedItemPosition = position;
			itemState.setState(State.Dragged);
		}
	}

	private void onActionMove(MotionEvent event) {
		if (isDraggingHorizontally(event)) {
			if (hasBeenDragged())
				disableOtherEvents();
			drag(event);
		} else if (isDraggingVertically(event))
			startBackAnimation();
	}

	private boolean isDraggingHorizontally(MotionEvent event) {
		if (lastMotionEvent == null || event == null)
			return false;
		float deltaX = Math.abs(event.getX() - lastMotionEvent.getX());
		float deltaY = Math.abs(event.getY() - lastMotionEvent.getY());
		return deltaX > deltaY;
	}

	private boolean isDraggingVertically(MotionEvent event) {
		float deltaY = Math.abs(event.getY() - lastMotionEvent.getY());
		return deltaY > 20;
	}

	private boolean hasBeenDragged() {
		ItemState itemState = getDraggedItemState();
		if (itemState == null || itemState.getState() != State.Dragged)
			return false;
		float dragPercentage = itemState.getDragPercentage();
		return Math.abs(dragPercentage * getWidth()) > 20;
	}

	private void disableOtherEvents() {
		setSelector(android.R.color.transparent);
		super.setOnItemClickListener(null);
		super.setOnItemLongClickListener(null);
	}

	private void drag(MotionEvent event) {
		ItemState itemState = getDraggedItemState();
		if (itemState != null && itemState.getState() == State.Dragged) {
			float dragOffset = event.getX() - lastMotionEvent.getX();
			float dragPercentage = itemState.getDragPercentage();
			dragPercentage += dragOffset / getWidth();
			itemState.setDragPercentage(dragPercentage);
			invalidate();
		}
	}

	private void onActionUp() {
		stopDragging();
	}

	private void enableOtherEvents() {
		if (selector != null)
			setSelector(selector);
		super.setOnItemClickListener(onItemClickListener);
		super.setOnItemLongClickListener(onItemLongClickListener);
	}

	private void stopDragging() {
		if (isToBeDeleted())
			startDeletionAnimation();
		else
			startBackAnimation();
		draggedItemPosition = NO_ITEM_DRAGGED;
	}

	private boolean isToBeDeleted() {
		ItemState itemState = getDraggedItemState();
		if (itemState == null || itemState.getState() != State.Dragged)
			return false;
		float dragPercentage = itemState.getDragPercentage();
		return Math.abs(dragPercentage) > 0.5;
	}

	private ItemState getDraggedItemState() {
		if (draggedItemPosition == NO_ITEM_DRAGGED || adapter == null)
			return null;
		return adapter.getItemState(draggedItemPosition);
	}

	private void startBackAnimation() {
		startBackAnimation(getDraggedItemState());
	}

	private void startBackAnimation(ItemState itemState) {
		if (itemState != null) {
			new BackAnimation(this, itemState, animLength).start();
		}
	}

	private void startDeletionAnimation() {
		startDeletionAnimation(draggedItemPosition);
	}

	private void startDeletionAnimation(int position) {
		if (adapter == null)
			return;
		ItemState draggedItemState = adapter.getItemState(position);
		if (draggedItemState != null)
			new DeleteAnimation(this, draggedItemState, animLength).start();
	}

	private void startDeletionConfirmedAnimation(ItemState itemState) {
		itemState.setState(State.DeletionConfirmed);
		new DeletionConfirmedAnimation(this, itemState, animLength).start();
	}

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	void onItemDeleted(ItemState itemState) {
		if (isConfirmNeeded) {
			if (onItemDeletedListener != null)
				onItemDeletedListener.onItemDeleted(this, itemState.getPosition());
			itemState.setState(ItemState.State.Deleted);
			notifyDataSetChanged();
		} else {
			startDeletionConfirmedAnimation(itemState);
			invalidate();
		}
	}

	void onItemDeletionConfirmed(ItemState itemState) {
		if (onItemDeletedListener != null) {
			if (isConfirmNeeded)
				onItemDeletionConfirmedListener.onItemDeletionConfirmed(this, itemState.getPosition());
			else
				onItemDeletedListener.onItemDeleted(this, itemState.getPosition());
		}
		adapter.onItemDeletionConfirmed(itemState);
	}

	public void delete(int position) {
		if (isConfirmNeeded) {
			startDeletionAnimation();
		} else {
			confirmDeletion(position);
		}
	}

	public List<Integer> getDeleted() {
		if (adapter == null)
			return new ArrayList<Integer>();
		return adapter.getDeleted();
	}

	public void confirmDeletion(int position) {
		if (adapter == null)
			return;
		ItemState itemState = adapter.getItemState(position);
		if (itemState == null)
			return;
		startDeletionConfirmedAnimation(itemState);
	}

	public void confirmAllDeletion() {
		for (Integer position : getDeleted())
			confirmDeletion(position);
	}

	public void cancelDeletion(int position) {
		ItemState itemState = adapter.getItemState(position);
		if (itemState == null)
			return;
		itemState.setState(State.Dragged);
		notifyDataSetChanged();
		startBackAnimation(itemState);
	}

	public void cancelAllDeletions() {
		for (Integer position : getDeleted())
			cancelDeletion(position);
	}

	@Override
	public ListAdapter getAdapter() {
		if (adapter == null)
			return null;
		return adapter.getAdapter();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		this.adapter = new SwipeToDeleteAdapter(this, adapter);
		super.setAdapter(this.adapter);
	}

	public DeletedViewAdapter getDeletedViewAdapter() {
		return deletedViewAdapter;
	}

	public void setDeletedViewAdapter(DeletedViewAdapter deletedViewAdapter) {
		this.deletedViewAdapter = deletedViewAdapter;
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.onItemClickListener = listener;
		super.setOnItemClickListener(listener);
	}

	@Override
	public void setOnItemLongClickListener(OnItemLongClickListener listener) {
		this.onItemLongClickListener = listener;
		super.setOnItemLongClickListener(listener);
	}

	public OnItemDeletedListener getOnItemDeletedListener() {
		return onItemDeletedListener;
	}

	public void setOnItemDeletedListener(OnItemDeletedListener onItemDeletedListener) {
		this.onItemDeletedListener = onItemDeletedListener;
	}

	public OnItemDeletionConfirmedListener getOnItemDeletionConfirmedListener() {
		return onItemDeletionConfirmedListener;
	}

	public void setOnItemDeletionConfirmedListener(OnItemDeletionConfirmedListener onItemDeletionConfirmedListener) {
		this.onItemDeletionConfirmedListener = onItemDeletionConfirmedListener;
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

	public interface OnItemDeletedListener {
		public void onItemDeleted(SwipeToDeleteListView listView, int position);
	}

	public interface OnItemDeletionConfirmedListener {
		public void onItemDeletionConfirmed(SwipeToDeleteListView listView, int position);
	}

	public interface DeletedViewAdapter {
		public View getView(int position, View view, ViewGroup parent);
	}
}
