package fr.ydelouis.stdlvsample;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyItemView
		extends RelativeLayout
{
	private TextView subject;
	private TextView contact;
	private ImageView favorite;

	public MyItemView(Context context) {
		super(context);
		View.inflate(context, R.layout.view_myitemview, this);
		subject = (TextView) findViewById(R.id.myItemView_subject);
		contact = (TextView) findViewById(R.id.myItemView_contact);
		favorite = (ImageView) findViewById(R.id.myItemView_favorite);
	}

	public void bind(final MyItem myItem) {
		subject.setText(myItem.getSubject());
		contact.setText(myItem.getContact());
		int favoriteImageRes = android.R.drawable.star_big_off;
		if(myItem.isFavorite())
			favoriteImageRes = android.R.drawable.star_big_on;
		favorite.setImageResource(favoriteImageRes);
		favorite.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				myItem.setFavorite(!myItem.isFavorite());
				int favoriteImageRes = android.R.drawable.star_big_off;
				if(myItem.isFavorite())
					favoriteImageRes = android.R.drawable.star_big_on;
				favorite.setImageResource(favoriteImageRes);
			}
		});
	}
}
