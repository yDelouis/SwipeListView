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
		extends BaseAdapter {
	private SwipeToDeleteListView listView;
	private ListAdapter adapter;
	private SparseArray<ItemState> itemStates = new SparseArray<ItemState>();

	public SwipeToDeleteAdapter(SwipeToDeleteListView listView, ListAdapter adapter) {
		this.listView = listView;
		this.adapter = adapter;
		this.adapter.registerDataSetObserver(new DataSetObserver() {
			public void onChanged() {
				notifyDataSetChanged();
			}

			public void onInvalidated() {
				notifyDataSetInvalidated();
			}
		});
	}

	public ListAdapter getAdapter() {
		return adapter;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ItemState itemState = getItemState(position);
		switch (itemState.getState()) {
			case DeletionConfirmed:
				return getDeletionConfirmedView(position, view, parent, itemState);
			case Deleted:
				return getDeletedView(position, view, parent, itemState);
			default:
				return getNormalView(position, view, parent, itemState);
		}
	}

	public ItemState getItemState(int position) {
		if (position < 0)
			return null;
		ItemState itemState = itemStates.get(position);
		if (itemState == null) {
			itemState = new ItemState(position);
			itemStates.put(position, itemState);
		}
		return itemState;
	}

	private View getDeletionConfirmedView(int position, View view, ViewGroup parent, ItemState itemState) {
		if (view == null) {
			if (listView.isConfirmNeeded())
				view = getNormalView(position, view, parent, itemState);
			else
				view = new View(listView.getContext());
		}
		setViewHeight(view, itemState);
		return view;
	}

	private View getDeletedView(int position, View view, ViewGroup parent, ItemState itemState) {
		view = listView.getDeletedViewAdapter().getView(position, view, parent);
		setViewHeight(view, itemState);
		return view;
	}

	private View getNormalView(int position, View view, ViewGroup parent, ItemState itemState) {
		view = adapter.getView(position, view, parent);
		itemState.setInitialViewHeight(view.getHeight());
		view.setTag(listView.getId(), itemState);
		return view;
	}

	private void setViewHeight(View view, ItemState itemState) {
		ViewGroup.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
		lp.height = itemState.getHeight();
		view.setLayoutParams(lp);
	}

	public void onItemDeletionConfirmed(ItemState itemState) {
		int position = itemState.getPosition();
		itemStates.remove(position);
		List<Integer> greaterPositions = new ArrayList<Integer>();
		for (int index = 0; index < itemStates.size(); index++) {
			int key = itemStates.keyAt(index);
			if (key > position)
				greaterPositions.add(key);
		}
		Collections.sort(greaterPositions);
		for (Integer key : greaterPositions) {
			ItemState iState = itemStates.get(key);
			itemStates.remove(key);
			iState.setPosition(iState.getPosition() - 1);
			itemStates.put(iState.getPosition(), iState);
		}
	}

	@Override
	public int getItemViewType(int position) {
		ItemState itemState = getItemState(position);
		switch (itemState.getState()) {
			case DeletionConfirmed:
				if (listView.isConfirmNeeded())
					return 1;
				else
					return 2;
			case Deleted:
				return 1;
			default:
				return 0;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 3;
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

	public List<Integer> getDeleted() {
		List<Integer> deleted = new ArrayList<Integer>();
		for (int i = 0; i < itemStates.size(); i++) {
			ItemState itemState = itemStates.valueAt(i);
			if (itemState.getState() == ItemState.State.Deleted)
				deleted.add(itemState.getPosition());
		}
		return deleted;
	}
}
