package fr.ydelouis.widget;

import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SwipeToDeleteAdapter
		extends BaseAdapter
{
	private SwipeToDeleteListView listView;
	private ListAdapter adapter;
	private SparseArray<ItemState> itemStates = new SparseArray<ItemState>();

	public SwipeToDeleteAdapter(SwipeToDeleteListView listView, ListAdapter adapter) {
		this.listView = listView;
		this.adapter = adapter;
	}

	public ListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if(view != null && view.getTag(listView.getId()) == null)
			view = null;
		view = adapter.getView(position, view, parent);

		ItemState itemState = getItemState(position);
		if(itemState.getState() == ItemState.State.DeletedConfirmed) {
			if(itemState.getInitialViewHeight() == 0)
				itemState.setInitialViewHeight(view.getHeight());
			ViewGroup.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
			lp.height = itemState.getHeight();
			view = new View(listView.getContext());
			view.setLayoutParams(lp);
		} else {
			view.setTag(listView.getId(), itemState);
		}
		return view;
	}

	public ItemState getItemState(int position) {
		if(position < 0)
			return null;
		ItemState itemState = itemStates.get(position);
		if(itemState == null) {
			itemState = new ItemState(position);
			itemStates.put(position, itemState);
		}
		return itemState;
	}

	public void onItemDeletionConfirmed(ItemState itemState) {
		int position = itemState.getPosition();
		itemStates.remove(position);
		List<Integer> greaterPositions = new ArrayList<Integer>();
		for(int index = 0; index < itemStates.size(); index++) {
			int key = itemStates.keyAt(index);
			if(key > position)
				greaterPositions.add(key);
		}
		Collections.sort(greaterPositions);
		for(Integer key : greaterPositions) {
			ItemState iState = itemStates.get(key);
			itemStates.remove(key);
			iState.setPosition(iState.getPosition() - 1);
			itemStates.put(iState.getPosition(), iState);
		}
	}

	@Override
	public int getCount() {
		return adapter.getCount();
	}

	@Override
	public Object getItem(int position) {
		return adapter.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return adapter.getItemId(position);
	}
}