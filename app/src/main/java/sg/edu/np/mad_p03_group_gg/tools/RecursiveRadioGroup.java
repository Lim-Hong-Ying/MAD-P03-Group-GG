package sg.edu.np.mad_p03_group_gg.tools;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Credits to the author: https://stackoverflow.com/users/2914696/ivan-ku%c5%a1t
 * Source of Code: https://stackoverflow.com/questions/10461005/how-to-group-radiobutton-from-different-linearlayouts
 *
 * This class is a replacement for android RadioGroup - it supports
 * child layouts which standard RadioGroup doesn't.
 */
public class RecursiveRadioGroup extends LinearLayout {

    public interface OnCheckedChangeListener {
        void onCheckedChanged(RecursiveRadioGroup group, @IdRes int checkedId);
    }

    /**
     * For generating unique view IDs on API < 17 with {@link #generateViewId()}.
     */
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private CompoundButton checkedView;

    private CompoundButton.OnCheckedChangeListener childOnCheckedChangeListener;

    /**
     * When this flag is true, onCheckedChangeListener discards events.
     */
    private boolean mProtectFromCheckedChange = false;

    private OnCheckedChangeListener onCheckedChangeListener;

    private PassThroughHierarchyChangeListener mPassThroughListener;

    public RecursiveRadioGroup(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        init();
    }

    public RecursiveRadioGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecursiveRadioGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        childOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();

        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        if (checkedView != null) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(checkedView, true);
            mProtectFromCheckedChange = false;
            setCheckedView(checkedView);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        parseChild(child);

        super.addView(child, index, params);
    }

    private void parseChild(final View child) {
        if (child instanceof CompoundButton) {
            final CompoundButton checkable = (CompoundButton) child;

            if (checkable.isChecked()) {
                mProtectFromCheckedChange = true;
                if (checkedView != null) {
                    setCheckedStateForView(checkedView, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedView(checkable);
            }
        } else if (child instanceof ViewGroup) {
            parseChildren((ViewGroup) child);
        }
    }

    private void parseChildren(final ViewGroup child) {
        for (int i = 0; i < child.getChildCount(); i++) {
            parseChild(child.getChildAt(i));
        }
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param view the radio button to select in this group
     * @see #getCheckedItemId()
     * @see #clearCheck()
     */
    public void check(CompoundButton view) {
        if(checkedView != null) {
            setCheckedStateForView(checkedView, false);
        }

        if(view != null) {
            setCheckedStateForView(view, true);
        }

        setCheckedView(view);
    }

    private void setCheckedView(CompoundButton view) {
        checkedView = view;

        if(onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, checkedView.getId());
        }
    }

    private void setCheckedStateForView(View checkedView, boolean checked) {
        if (checkedView != null && checkedView instanceof CompoundButton) {
            ((CompoundButton) checkedView).setChecked(checked);
        }
    }

    /**
     * <p>Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is -1.</p>
     *
     * @return the unique id of the selected radio button in this group
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     * @see #check(CompoundButton)
     * @see #clearCheck()
     */
    @IdRes
    public int getCheckedItemId() {
        return checkedView.getId();
    }

    public CompoundButton getCheckedItem() {
        return checkedView;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedItemId()} returns
     * null.</p>
     *
     * @see #check(CompoundButton)
     * @see #getCheckedItemId()
     */
    public void clearCheck() {
        check(null);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(RecursiveRadioGroup.OnCheckedChangeListener listener) {
        onCheckedChangeListener = listener;
    }

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton view, boolean b) {
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if (checkedView != null) {
                setCheckedStateForView(checkedView, false);
            }
            mProtectFromCheckedChange = false;

            int id = view.getId();
            setCheckedView(view);
        }
    }

    private class PassThroughHierarchyChangeListener implements OnHierarchyChangeListener {

        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        @Override
        public void onChildViewAdded(View parent, View child) {
            if (child instanceof CompoundButton) {
                int id = child.getId();

                if (id == View.NO_ID) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        child.setId(generateViewId());
                    } else {
                        child.setId(View.generateViewId());
                    }
                }

                ((CompoundButton) child).setOnCheckedChangeListener(childOnCheckedChangeListener);

                if (mOnHierarchyChangeListener != null) {
                    mOnHierarchyChangeListener.onChildViewAdded(parent, child);
                }
            } else if(child instanceof ViewGroup) {
                // View hierarchy seems to be constructed from the bottom up,
                // so all child views are already added. That's why we
                // manually call the listener for all children of ViewGroup.
                for(int i = 0; i < ((ViewGroup) child).getChildCount(); i++) {
                    onChildViewAdded(child, ((ViewGroup) child).getChildAt(i));
                }
            }
        }

        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (child instanceof RadioButton) {
                ((CompoundButton) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }

}
