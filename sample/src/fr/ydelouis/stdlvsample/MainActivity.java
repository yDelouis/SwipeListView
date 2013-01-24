package fr.ydelouis.stdlvsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity
		extends Activity
		implements
			View.OnClickListener
{
	private View withoutConfirm;
	private View withConfirm;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViews();
		setListeners();
	}

	private void findViews() {
		withoutConfirm = findViewById(R.id.main_withoutConfirm);
		withConfirm = findViewById(R.id.main_withConfirm);
	}

	private void setListeners() {
		withoutConfirm.setOnClickListener(this);
		withConfirm.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if(view.equals(withoutConfirm))
			startActivity(new Intent(this, WithoutConfirmActivity.class));
		if(view.equals(withConfirm))
			startActivity(new Intent(this, WithConfirmActivity.class));
	}
}