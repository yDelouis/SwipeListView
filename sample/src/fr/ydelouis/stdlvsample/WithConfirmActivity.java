package fr.ydelouis.stdlvsample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import fr.ydelouis.widget.SwipeToDeleteListView;

import java.util.List;

public class WithConfirmActivity
		extends Activity
		implements
		SwipeToDeleteListView.OnItemDeletedListener,
		SwipeToDeleteListView.OnItemDeletionConfirmedListener,
		AdapterView.OnItemLongClickListener,
		AdapterView.OnItemClickListener
{

	private static final int NB_ITEMS = 30;

	private SwipeToDeleteListView listView;
	private MyItemAdapter adapter;
	private List<MyItem> items;

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

		listView.setConfirmNeeded(true);
		listView.setDeletedViewAdapter(new MyDeletedViewAdapter(listView));
		listView.setOnItemDeletionConfirmedListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_withconfirm, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getItemId() == R.id.menu_withconfirm_confirm) {
			listView.confirmAllDeletion();
			return true;
		}
		if(item.getItemId() == R.id.menu_withconfirm_cancel) {
			listView.cancelAllDeletions();
			return true;
		}
		return false;
	}

	@Override
	public void onItemDeleted(SwipeToDeleteListView listView, int position) {
		Toast.makeText(this, position + " has been deleted", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onItemDeletionConfirmed(SwipeToDeleteListView listView, int position) {
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