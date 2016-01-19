/**
 * TODO：需要再考虑一些细节
 * 此方法是用来解决输入框被输入法覆盖的冲突， 将输入框所在的view 滑动到 scrollToView 所在位置，即将scrollToView以上的view 滑动到输入法之上
 */
private void controlKeyboardLayout(final View root, final View scrollToView)
{
	root.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener()
	{
		int mLastHeight = 0;
		int mLastBottom = -1;


		ValueAnimator scrollAnimator = null;
		@Override
		public void onGlobalLayout()
		{
			Rect rect = new Rect();
			root.getWindowVisibleDisplayFrame(rect);

			LogUtils.e(TAG, "Rect: " + rect);

			if (mLastBottom == -1) {
				mLastBottom = rect.bottom;
				return;
			}

			int nb = rect.bottom;
			int ob = mLastBottom;

			if (nb < ob) {
				// 键盘显示了， 滑上去
				int[] location = new int[2];
				scrollToView.getLocationInWindow(location);
				int scrollHeight = (location[1] + scrollToView.getHeight()) - nb;

				scrollAnimator = ValueAnimator.ofInt(0, scrollHeight);
				scrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator)
					{
						int value = (int) valueAnimator.getAnimatedValue();
						root.scrollTo(0, value);
						mLastHeight = value;
					}
				});
				scrollAnimator.start();
			}
			else if (nb > ob) {
				// 键盘隐藏了, 滑下来
				if (scrollAnimator != null) {
					scrollAnimator.cancel();
				}
				scrollAnimator = ValueAnimator.ofInt(mLastHeight, 0);
				scrollAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
				scrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator valueAnimator)
					{
						int value = (int) valueAnimator.getAnimatedValue();
						root.scrollTo(0, value);
						mLastHeight = value;
					}
				});
				scrollAnimator.addListener(new Animator.AnimatorListener()
				{
					@Override
					public void onAnimationStart(Animator animator)
					{
					}

					@Override
					public void onAnimationEnd(Animator animator)
					{
						root.scrollTo(0, 0);
						mLastHeight = 0;
					}

					@Override
					public void onAnimationCancel(Animator animator)
					{
						root.scrollTo(0, 0);
						mLastHeight = 0;
					}

					@Override
					public void onAnimationRepeat(Animator animator)
					{
					}
				});

				scrollAnimator.start();
			}

			if (nb != ob) {
				mLastBottom = nb;
			}
		}
	});
}