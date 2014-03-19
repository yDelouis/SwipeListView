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
package fr.ydelouis.widget;

import android.view.ViewGroup;

class ItemState {
	private int position;
	private State state = State.Normal;
	private float dragPercentage = 0;
	private float heightPercentage = 1;
	private float initialViewHeight = 0;
	public ItemState(int position) {
		this.position = position;
	}

	public int getHeight() {
		if (initialViewHeight == 0)
			return ViewGroup.LayoutParams.WRAP_CONTENT;

		return (int) (initialViewHeight * heightPercentage);
	}

	public void reset() {
		state = State.Normal;
		dragPercentage = 0;
		heightPercentage = 1;
		initialViewHeight = 0;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public float getDragPercentage() {
		return dragPercentage;
	}

	public void setDragPercentage(float dragPercentage) {
		this.dragPercentage = dragPercentage;
	}

	public float getHeightPercentage() {
		return heightPercentage;
	}

	public void setHeightPercentage(float heightPercentage) {
		this.heightPercentage = heightPercentage;
	}

	public int getInitialViewHeight() {
		return (int) initialViewHeight;
	}

	public void setInitialViewHeight(int initialViewHeight) {
		this.initialViewHeight = initialViewHeight;
	}

	enum State {
		Normal, Dragged, Deleted, DeletionConfirmed
	}
}
