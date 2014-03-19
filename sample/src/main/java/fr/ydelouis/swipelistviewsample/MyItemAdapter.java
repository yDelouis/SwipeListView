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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class MyItemAdapter extends ArrayAdapter<MyItem> {

	public MyItemAdapter(Context context, List<MyItem> items) {
		super(context, 0, items);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		MyItemView myItemView;
		if (view != null && view instanceof MyItemView)
			myItemView = (MyItemView) view;
		else
			myItemView = new MyItemView(getContext());

		MyItem myItem = getItem(position);
		myItemView.bind(myItem);

		return myItemView;
	}
}
