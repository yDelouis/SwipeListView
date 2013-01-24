package fr.ydelouis.widget;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

class DeletionConfirmedAnimation
		implements
		Animator.AnimatorListener,
		ValueAnimator.AnimatorUpdateListener
{
	private SwipeToDeleteListView listView;
	private ItemState itemState;
	private ValueAnimator animator;

	public DeletionConfirmedAnimation(SwipeToDeleteListView listView, ItemState itemState, long duration) {
		this.listView = listView;
		this.itemState = itemState;
		animator = ValueAnimator.ofFloat(1f, 0f);
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
	public void onAnimationUpdate(ValueAnimator valueAnimator) {
		itemState.setHeightPercentage((Float) valueAnimator.getAnimatedValue());
		listView.notifyDataSetChanged();
	}

	@Override
	public void onAnimationEnd(Animator animator) {
		listView.onItemDeletionConfirmed(itemState);
	}

	@Override
	public void onAnimationCancel(Animator animator) {
	}

	@Override
	public void onAnimationRepeat(Animator animator) {
	}
}
