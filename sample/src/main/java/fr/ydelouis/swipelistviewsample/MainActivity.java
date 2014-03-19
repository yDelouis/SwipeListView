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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity
		extends Activity
		implements
		View.OnClickListener {
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
		if (view.equals(withoutConfirm))
			startActivity(new Intent(this, WithoutConfirmActivity.class));
		if (view.equals(withConfirm))
			startActivity(new Intent(this, WithConfirmActivity.class));
	}
}
