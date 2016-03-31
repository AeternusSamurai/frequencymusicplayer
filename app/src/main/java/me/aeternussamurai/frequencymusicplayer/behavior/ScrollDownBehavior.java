package me.aeternussamurai.frequencymusicplayer.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import me.aeternussamurai.frequencymusicplayer.Utils;

/**
 * Created by Chase on 3/29/2016.
 */
public class ScrollDownBehavior extends CoordinatorLayout.Behavior<Toolbar> {

    private int toolbarHeight;

    public ScrollDownBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        toolbarHeight = Utils.getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, Toolbar child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, Toolbar child, View dependency) {
        boolean retVal = super.onDependentViewChanged(parent, child, dependency);
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
            int childBottomMargin = lp.bottomMargin;
            int distanceToScroll = child.getHeight() + childBottomMargin;
            float ratio = dependency.getY() / (float) toolbarHeight;
            child.setTranslationY((-distanceToScroll * ratio)+toolbarHeight/2.25f);
        }
        return retVal;
    }
}
