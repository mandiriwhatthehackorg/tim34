package com.mandiri.binding.bindingcollectionadapter;

import android.view.Gravity;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A collection of factories to create RecyclerView LayoutManagers so that you can easily set them
 * in your layout.
 */
public class LayoutManagers {
    protected LayoutManagers() {
    }

    public interface LayoutManagerFactory {
        RecyclerView.LayoutManager create(RecyclerView recyclerView);
    }

    /**
     * A {@link LinearLayoutManager}.
     */
    public static LayoutManagerFactory linear() {
        return new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new LinearLayoutManager(recyclerView.getContext());
            }
        };
    }

    /**
     * A {@link LinearLayoutManager} with the given orientation and reverseLayout.
     */
    public static LayoutManagerFactory linear(@Orientation final int orientation, final boolean reverseLayout) {
        return new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new LinearLayoutManager(recyclerView.getContext(), orientation, reverseLayout);
            }
        };
    }

    /**
     * A {@link GridLayoutManager} with the given spanCount.
     */
    public static LayoutManagerFactory grid(final int spanCount) {
        return new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new GridLayoutManager(recyclerView.getContext(), spanCount);
            }
        };
    }

    /**
     * A {@link GridLayoutManager} with the given spanCount, orientation and reverseLayout.
     **/
    public static LayoutManagerFactory grid(final int spanCount, @Orientation final int orientation, final boolean reverseLayout) {
        return new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new GridLayoutManager(recyclerView.getContext(), spanCount, orientation, reverseLayout);
            }
        };
    }

    /**
     * A {@link StaggeredGridLayoutManager} with the given spanCount and orientation.
     */
    public static LayoutManagerFactory staggeredGrid(final int spanCount, @Orientation final int orientation) {
        return new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                return new StaggeredGridLayoutManager(spanCount, orientation);
            }
        };
    }


    /**
     * A {@link ChipsLayoutManager} with the given spanCount and orientation.
     */
    public static LayoutManagerFactory chips(final int maxViewsInRow) {
        return new LayoutManagerFactory() {
            @Override
            public RecyclerView.LayoutManager create(RecyclerView recyclerView) {
                ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(recyclerView.getContext())
                        //set vertical gravity for all items in a row. Default = Gravity.CENTER_VERTICAL
                        .setChildGravity(Gravity.CENTER)
                        //whether RecyclerView can scroll. TRUE by default
                        .setScrollingEnabled(true)
                        //set maximum views count in a particular row
                        .setMaxViewsInRow(maxViewsInRow)
                        //set gravity resolver where you can determine gravity for item in position.
                        //This method have priority over previous one
//                        .setGravityResolver(new IChildGravityResolver() {
//                            @Override
//                            public int getItemGravity(int position) {
//                                return Gravity.CENTER;
//                            }
//                        })
                        //you are able to break row due to your conditions. Row breaker should return true for that views
//                        .setRowBreaker(new IRowBreaker() {
//                            @Override
//                            public boolean isItemBreakRow(@IntRange(from = 0) int position) {
//                                return position == 6 || position == 11 || position == 2;
//                            }
//                        })
                        //a layoutOrientation of layout manager, could be VERTICAL OR HORIZONTAL. HORIZONTAL by default
                        .setOrientation(ChipsLayoutManager.HORIZONTAL)
                        // row strategy for views in completed row, could be STRATEGY_DEFAULT, STRATEGY_FILL_VIEW,
                        //STRATEGY_FILL_SPACE or STRATEGY_CENTER
                        .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
                        // whether strategy is applied to last row. FALSE by default
//                        .withLastRow(true)
                        .build();
                return chipsLayoutManager;
            }
        };
    }


    @IntDef({LinearLayoutManager.HORIZONTAL, LinearLayoutManager.VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Orientation {
    }
}
