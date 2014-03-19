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
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyItemModel {
	private String[] subjects;
	private String[] firstNames;
	private String[] lastNames;
	private Random random;

	public MyItemModel(Context context) {
		Resources resources = context.getResources();
		subjects = resources.getStringArray(R.array.departments);
		firstNames = resources.getStringArray(R.array.firstNames);
		lastNames = resources.getStringArray(R.array.lastNames);
		random = new Random();
	}

	public List<MyItem> get(int number) {
		List<MyItem> items = new ArrayList<MyItem>();
		for (int i = 0; i < number; i++) {
			items.add(createRandom());
		}
		return items;
	}

	public MyItem createRandom() {
		MyItem myItem = new MyItem();
		myItem.setSubject(randomSubject());
		myItem.setContact(randomContact());
		return myItem;
	}

	private String randomSubject() {
		return subjects[random.nextInt(subjects.length)];
	}

	private String randomContact() {
		return firstNames[random.nextInt(firstNames.length)] + " " + lastNames[random.nextInt(lastNames.length)];
	}
}
