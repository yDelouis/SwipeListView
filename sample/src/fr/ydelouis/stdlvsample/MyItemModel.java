package fr.ydelouis.stdlvsample;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyItemModel
{
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
		for(int i = 0; i < number; i++) {
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