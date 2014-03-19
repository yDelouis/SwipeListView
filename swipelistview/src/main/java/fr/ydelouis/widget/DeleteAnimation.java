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


import android.animation.Animator;
import android.animation.ValueAnimator;

class DeleteAnimation
		implements
		Animator.AnimatorListener,
		ValueAnimator.AnimatorUpdateListener {
	private SwipeToDeleteListView listView;
	private ItemState itemState;
	private ValueAnimator animator;

	public DeleteAnimation(SwipeToDeleteListView listView, ItemState itemState, long duration) {
		this.listView = listView;
		this.itemState = itemState;
		float dragPercentage = itemState.getDragPercentage();
		float end = dragPercentage > 0 ? 1f : -1f;
		animator = ValueAnimator.ofFloat(dragPercentage, end);
		animator.setDuration(duration);
		animator.addUpdateListener(this);
		animator.addListener(this);
	}

	public void start() {
		animator.start();
	}

	@Override
	public void onAnimationStart(Animator animator) {
	}

	@Override
	public void onAnimationUpdate(ValueAnimator animator) {
		itemState.setDragPercentage((Float) animator.getAnimatedValue());
		listView.invalidate();
	}

	@Override
	public void onAnimationEnd(Animator animator) {
		listView.onItemDeleted(itemState);
	}

	@Override
	public void onAnimationCancel(Animator animator) {
	}

	@Override
	public void onAnimationRepeat(Animator animator) {
	}
}
