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
package fr.ydelouis.swipelistviewsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.List;

import fr.ydelouis.widget.SwipeToDeleteListView;
import fr.ydelouis.widget.SwipeToDeleteListView.OnItemDeletedListener;

public class WithoutConfirmActivity
		extends Activity
		implements
		OnItemDeletedListener,
		AdapterView.OnItemLongClickListener,
		AdapterView.OnItemClickListener {
	private static final int NB_ITEMS = 30;

	private SwipeToDeleteListView listView;
	private MyItemAdapter adapter;
	private List<MyItem> items;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		items = new MyItemModel(this).get(NB_ITEMS);
		adapter = new MyItemAdapter(this, items);

		listView = (SwipeToDeleteListView) findViewById(R.id.list);
		listView.setOnItemDeletedListener(this);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		listView.setAdapter(adapter);
	}

	@Override
	public void onItemDeleted(SwipeToDeleteListView listView, int position) {
		items.remove(position);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(this, position + " has been long clicked", Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Toast.makeText(this, position + " has been clicked", Toast.LENGTH_SHORT).show();
	}
}
