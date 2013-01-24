package fr.ydelouis.widget;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import fr.ydelouis.widget.ItemState.State;

class BackAnimation
		implements
		AnimatorListener,
		AnimatorUpdateListener
{
	private SwipeToDeleteListView listView;
	private ItemState itemState;
	private ValueAnimator animator;

	public BackAnimation(SwipeToDeleteListView listView, ItemState itemState, long duration) {
		this.listView = listView;
		this.itemState = itemState;
		float dragPercentage = itemState.getDragPercentage();
		animator = ValueAnimator.ofFloat(dragPercentage, 0);
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
		itemState.setState(State.Normal);
		itemState.setDragPercentage(0);
	}

	@Override
	public void onAnimationCancel(Animator animator) {
	}

	@Override
	public void onAnimationRepeat(Animator animator) {
	}
}
