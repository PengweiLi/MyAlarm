package com.lipengwei.myalarm.ui;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lipengwei.myalarm.R;


public class ActionableToastBar extends LinearLayout {
    private boolean mHidden = false;
    private Animator mShowAnimation;
    private Animator mHideAnimation;
    private final int mBottomMarginSizeInConversation;

    /** Icon for the description. */
    private ImageView mActionDescriptionIcon;
    /** The clickable view */
    private View mActionButton;
    /** Icon for the action button. */
    private View mActionIcon;
    /** The view that contains the description. */
    private TextView mActionDescriptionView;
    
    private TextView mActionText;
    //private ToastBarOperation mOperation;

    public ActionableToastBar(Context context) {
        this(context, null);
    }

    public ActionableToastBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActionableToastBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBottomMarginSizeInConversation = context.getResources().getDimensionPixelSize(
                R.dimen.toast_bar_bottom_margin_in_conversation);
        LayoutInflater.from(context).inflate(R.layout.actionable_toast_row, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mActionDescriptionIcon = (ImageView) findViewById(R.id.description_icon);
        mActionDescriptionView = (TextView) findViewById(R.id.description_text);
        mActionButton = findViewById(R.id.action_button);
        mActionIcon = findViewById(R.id.action_icon);
        mActionText = (TextView) findViewById(R.id.action_text);
    }

    public void setConversationMode(boolean isInConversationMode) {
        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = isInConversationMode ? mBottomMarginSizeInConversation : 0;
        setLayoutParams(params);
    }

    public void show(final ActionClickedListener listener, int descriptionIconResourceId,
            CharSequence descriptionText, boolean showActionIcon, int actionTextResource,
            boolean replaceVisibleToast) {

        if (!mHidden && !replaceVisibleToast) {
            return;
        }

        mActionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View widget) {
                if (listener != null) {
                    listener.onActionClicked();
                }
                hide(true);
            }
        });

        // Set description icon.
        if (descriptionIconResourceId == 0) {
            mActionDescriptionIcon.setVisibility(GONE);
        } else {
            mActionDescriptionIcon.setVisibility(VISIBLE);
            mActionDescriptionIcon.setImageResource(descriptionIconResourceId);
        }

        mActionDescriptionView.setText(descriptionText);
        mActionIcon.setVisibility(showActionIcon ? VISIBLE : GONE);
        mActionText.setText(actionTextResource);

        mHidden = false;
        getShowAnimation().start();
    }

    /**
     * Hides the view and resets the state.
     */
    public void hide(boolean animate) {
        // Prevent multiple call to hide.
        // Also prevent hiding if show animation is going on.
        if (!mHidden && !getShowAnimation().isRunning()) {
            mHidden = true;
            if (getVisibility() == View.VISIBLE) {
                mActionDescriptionView.setText("");
                mActionButton.setOnClickListener(null);
                // Hide view once it's clicked.
                if (animate) {
                    getHideAnimation().start();
                } else {
                    setAlpha(0);
                    setVisibility(View.GONE);
                }
            }
        }
    }

    private Animator getShowAnimation() {
        if (mShowAnimation == null) {
            mShowAnimation = AnimatorInflater.loadAnimator(getContext(), android.R.animator.fade_in);
            mShowAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    // There is a tiny change that and hide animation could have finished right
                    // before the show animation finished.  In that case, the hide will mark the
                    // view as GONE.  We need to make sure the last one wins.
                    setVisibility(View.VISIBLE);
                }
            });
            mShowAnimation.setTarget(this);
        }
        return mShowAnimation;
    }

    private Animator getHideAnimation() {
        if (mHideAnimation == null) {
            mHideAnimation = AnimatorInflater.loadAnimator(getContext(), android.R.animator.fade_out);
            mHideAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                }
            });
            mHideAnimation.setTarget(this);
        }
        return mHideAnimation;
    }

    public boolean isEventInToastBar(MotionEvent event) {
        if (!isShown()) {
            return false;
        }
        int[] xy = new int[2];
        float x = event.getX();
        float y = event.getY();
        getLocationOnScreen(xy);
        return (x > xy[0] && x < (xy[0] + getWidth()) && y > xy[1] && y < xy[1] + getHeight());
    }

    /**
     * Classes that wish to perform some action when the action button is clicked
     * should implement this interface.
     */
    public interface ActionClickedListener {
        public void onActionClicked();
    }
}

