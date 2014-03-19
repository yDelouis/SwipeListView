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

import android.view.View;
import android.view.ViewGroup;

import fr.ydelouis.widget.SwipeToDeleteListView;

public class MyDeletedViewAdapter implements SwipeToDeleteListView.DeletedViewAdapter {
	private SwipeToDeleteListView listView;

	public MyDeletedViewAdapter(SwipeToDeleteListView listView) {
		this.listView = listView;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
			view = View.inflate(listView.getContext(), R.layout.view_deletedview, null);
		}
		view.findViewById(R.id.deletedView_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listView.cancelDeletion(position);
			}
		});
		return view;
	}
}
