package fr.ydelouis.stdlvsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import fr.ydelouis.widget.SwipeToDeleteListView;
import fr.ydelouis.widget.SwipeToDeleteListView.OnItemDeletedListener;

import java.util.List;

public class WithoutConfirmActivity
		extends Activity
		implements
		OnItemDeletedListener,
		AdapterView.OnItemLongClickListener,
		AdapterView.OnItemClickListener
{
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
