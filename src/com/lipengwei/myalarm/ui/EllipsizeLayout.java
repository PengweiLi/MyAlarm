package com.lipengwei.myalarm.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class EllipsizeLayout extends LinearLayout {

    public EllipsizeLayout(Context context) {
        this(context, null);
    }

    public EllipsizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getOrientation() == HORIZONTAL
                && (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY)) {
            int totalLength = 0;
            // If any of the constraints of this class are exceeded, outOfSpec becomes true
            // and the no alterations are made to the ellipsize-text-view.
            boolean outOfSpec = false;
            TextView ellipsizeView = null;
            final int count = getChildCount();
            final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            final int queryWidthMeasureSpec = MeasureSpec.
                    makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED);

            for (int ii = 0; ii < count && !outOfSpec; ++ii) {
                final View child = getChildAt(ii);
                if (child != null && child.getVisibility() != GONE) {
                    // Identify the ellipsize view
                    if (child instanceof TextView) {
                        final TextView tv = (TextView) child;
                        if (tv.getEllipsize() != null) {
                            if (ellipsizeView == null) {
                                ellipsizeView = tv;
                                // Clear the maximum width on ellipsizeView before measurement
                                ellipsizeView.setMaxWidth(Integer.MAX_VALUE);
                            } else {
                                // TODO: support multiple android:ellipsize
                                outOfSpec = true;
                            }
                        }
                    }
                    // Ask the child to measure itself
                    measureChildWithMargins(child, queryWidthMeasureSpec, 0, heightMeasureSpec, 0);

                    // Get the layout parameters to check for a weighted width and to add the
                    // child's margins to the total length.
                    final LinearLayout.LayoutParams layoutParams =
                            (LinearLayout.LayoutParams) child.getLayoutParams();
                    if (layoutParams != null) {
                        outOfSpec |= (layoutParams.weight > 0f);
                        totalLength += child.getMeasuredWidth()
                                + layoutParams.leftMargin + layoutParams.rightMargin;
                    } else {
                        outOfSpec = true;
                    }
                }
            }
            // Last constraint test
            outOfSpec |= (ellipsizeView == null) || (totalLength == 0);

            if (!outOfSpec && totalLength > parentWidth) {
                int maxWidth = ellipsizeView.getMeasuredWidth() - (totalLength - parentWidth);
                // TODO: Respect android:minWidth (easy with @TargetApi(16))
                final int minWidth = 0;
                if (maxWidth < minWidth) {
                    maxWidth = minWidth;
                }
                ellipsizeView.setMaxWidth(maxWidth);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
