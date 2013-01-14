package fr.ydelouis.stdlvsample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.ydelouis.widget.SwipeToDeleteListView;
import fr.ydelouis.widget.SwipeToDeleteListView.OnItemDeletedListener;

public class SwipeToDeleteListViewDemoActivity extends Activity
	implements
		OnItemDeletedListener
{
	private static final int image0 = android.R.drawable.ic_menu_add;
	private static final int nbImages = android.R.drawable.ic_menu_zoom - image0;
	
	private ArrayList<String>	items	= new ArrayList<String>();
	private SwipeToDeleteListView list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deletablelist_demo);
		initList();
		list = (SwipeToDeleteListView) findViewById(R.id.list);
		list.setAdapter(new CustomAdapter(this, items));
		list.setOnItemDeletedListener(this);
	}
	
	private void initList() {
		items.add("Un");
		items.add("Deux");
		items.add("Trois");
		items.add("Quatre");
		items.add("Cinq");
		items.add("Six");
		items.add("Sept");
		items.add("Huit");
		items.add("Neuf");
		items.add("Dix");
		items.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam commodo augue sollicitudin erat facilisis nec varius neque mattis. Ut eu nisi quam. Nunc cursus rutrum congue. In fermentum, dui id pellentesque posuere, eros metus commodo lacus, vitae lobortis purus metus a mi.");
		items.add("Onze");
		items.add("Douze");
		items.add("Treize");
		items.add("Quatorze");
		items.add("Quinze");
		items.add("Seize");
		items.add("Dix-Sept");
	}

	@Override
	public void onItemDeleted(SwipeToDeleteListView listView, int position) {
		items.remove(position);
		list.notifyDataSetChanged();
	}

	private class CustomAdapter extends ArrayAdapter<String>
	{
		public CustomAdapter(Context context, List<String> items) {
			super(context, R.layout.deletablelist_item, items);
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if (view == null)
				view = View.inflate(getContext(), R.layout.deletablelist_item, null);

			((TextView) view.findViewById(R.id.deletableListItem_text)).setText(getItem(position));
			((ImageView) view.findViewById(R.id.deletableListItem_image)).setImageResource(image0 + position%nbImages);

			return view;
		}
	}
}
