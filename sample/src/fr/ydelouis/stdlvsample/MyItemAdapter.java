package fr.ydelouis.stdlvsample;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class MyItemAdapter
		extends ArrayAdapter<MyItem>
{
	public MyItemAdapter(Context context, List<MyItem> items) {
		super(context, 0, items);
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		MyItemView myItemView;
		if(view != null && view instanceof MyItemView)
			myItemView = (MyItemView) view;
		else
			myItemView = new MyItemView(getContext());

		MyItem myItem = getItem(position);
		myItemView.bind(myItem);

		return myItemView;
	}
}
