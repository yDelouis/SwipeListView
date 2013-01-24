package fr.ydelouis.widget;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

class DeleteAnimation
		implements
		AnimatorListener,
		AnimatorUpdateListener
{
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
