package com.zeyad.gadapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.ArraySet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zeyad.gadapter.Observables.ItemClickObservable;
import com.zeyad.gadapter.Observables.ItemLongClickObservable;
import com.zeyad.gadapter.Observables.ItemSwipeObservable;
import com.zeyad.gadapter.fastscroll.SectionTitleProvider;
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static android.os.Build.VERSION_CODES.M;
import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static com.zeyad.gadapter.ItemInfo.SECTION_HEADER;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericRecyclerViewAdapter
        extends RecyclerView.Adapter<GenericRecyclerViewAdapter.ViewHolder>
        implements ItemTouchHelperAdapter, StickyHeaderHandler {

    public static final int DEFAULT_INITIAL_ELEVATION = 1;
    public static final int DEFAULT_FINAL_ELEVATION = 6;
    public static final int DEFAULT_SCROLL_FINAL_ELEVATION = 6;
    private static final String SELECTION_DISABLED = "Selection mode is disabled!";
    private final LayoutInflater mLayoutInflater;
    private final SparseBooleanArray mSelectedItems;
    private List<ItemInfo> mDataList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private OnSwipeListener mOnSwipeListener;
    private SectionTitleProvider mSectionTitleProvider;
    private ArrayList<Integer> expandedPositions;
    private boolean mIsLoadingFooterAdded,
            mHasHeader,
            mHasFooter,
            allowSelection,
            areItemsExpandable,
            areItemsClickable;
    private int mInitialElevation;
    private int mFinalElevation;
    private int mScrollFinalPosition;

    public GenericRecyclerViewAdapter(LayoutInflater layoutInflater) {
        mLayoutInflater = layoutInflater;
        mDataList = new ArrayList<>();
        mSelectedItems = new SparseBooleanArray();
        expandedPositions = new ArrayList<>();
        areItemsClickable = true;
    }

    public GenericRecyclerViewAdapter(LayoutInflater layoutInflater, List<ItemInfo> list) {
        validateList(list);
        mLayoutInflater = layoutInflater;
        mDataList = list;
        mSelectedItems = new SparseBooleanArray();
        expandedPositions = new ArrayList<>();
        areItemsClickable = true;
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemInfo itemInfo = mDataList.get(position);
        holder.bindData(itemInfo.getData(), mSelectedItems.get(position, false), position,
                itemInfo.isEnabled());
        if (areItemsClickable && !(isHeader(position) || isFooter(position) || isSectionHeader(position))) {
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != NO_POSITION) {
                            mOnItemClickListener.onItemClicked(adapterPosition, itemInfo, holder);
                        }
                    }
                });
            }
            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int adapterPosition = holder.getAdapterPosition();
                        return adapterPosition != NO_POSITION
                                && mOnItemLongClickListener.onItemLongClicked(adapterPosition, itemInfo, holder);
                    }
                });
            }
        }
        if (areItemsExpandable && holder instanceof OnExpandListener) {
            ((OnExpandListener) holder).expand(expandedPositions.contains(position));
            holder.itemView.setActivated(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (expandedPositions.contains(adapterPosition)) {
                        for (int i = 0; i < expandedPositions.size(); i++) {
                            if (expandedPositions.get(i) == adapterPosition) {
                                expandedPositions.remove(i);
                            }
                        }
                    } else {
                        expandedPositions.add(adapterPosition);
                    }
                    //                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //                        TransitionManager.beginDelayedTransition((ViewGroup) holder.itemView.getParent());
                    //                    }
                    notifyItemChanged(adapterPosition);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).getLayoutId();
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        if (mOnSwipeListener != null)
            mOnSwipeListener.onItemSwipe(getItem(position));
        removeItem(position);
    }

    @Override
    public List<ItemInfo> getAdapterData() {
        return mDataList;
    }

    public SectionTitleProvider getSectionTitleProvider() {
        return mSectionTitleProvider;
    }

    public void setSectionTitleProvider(SectionTitleProvider mSectionTitleProvider) {
        this.mSectionTitleProvider = mSectionTitleProvider;
    }

    public ItemInfo getItem(int index) {
        return mDataList.get(index);
    }

    public ItemInfo getFirstItem() {
        return mDataList.get(0);
    }

    public ItemInfo getLastItem() {
        return mDataList.get(mDataList.size() - 1);
    }

    public int getPureSize() {
        return getPureDataList().size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public ItemClickObservable getItemClickObservable() {
        return new ItemClickObservable(this);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        mOnItemLongClickListener = onItemLongClickListener;
    }

    public ItemLongClickObservable getItemLongClickObservable() {
        return new ItemLongClickObservable(this);
    }

    public void setOnItemSwipeListener(OnSwipeListener onSwipeListener) {
        mOnSwipeListener = onSwipeListener;
    }

    public ItemSwipeObservable getItemSwipeObservable() {
        return new ItemSwipeObservable(this);
    }

    public boolean hasItemById(long itemId) {
        for (ItemInfo itemInfo : mDataList) {
            if (itemInfo.getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public int getItemIndexById(long itemId) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public ItemInfo getItemById(long itemId) throws IllegalAccessException {
        for (ItemInfo itemInfo : mDataList) {
            if (itemInfo.getId() == itemId) {
                return itemInfo;
            }
        }
        throw new IllegalAccessException("Item with id " + itemId + " does not exist!");
    }

    public void disableViewHolder(int index) {
        mDataList.get(index).setEnabled(false);
    }

    public void enableViewHolder(int index) {
        mDataList.get(index).setEnabled(true);
        notifyItemChanged(index);
    }

    public boolean hasHeader() {
        return mHasHeader;
    }

    public void setHasHeader(boolean hasHeader, String label) {
        if (!mHasHeader && hasHeader) {
            mHasHeader = true;
            mDataList.add(0, new ItemInfo(label, ItemInfo.HEADER).setId(ItemInfo.HEADER));
            notifyDataSetChanged();
        }
    }

    public boolean hasFooter() {
        return mHasFooter;
    }

    public void setHasFooter(boolean hasFooter, String label) {
        if (!mHasFooter && hasFooter) {
            mHasFooter = true;
            int position;
            position = mDataList.size();
            mDataList.add(position, new ItemInfo(label, ItemInfo.FOOTER).setId(ItemInfo.FOOTER));
            notifyItemInserted(position);
        }
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
        if (!mDataList.isEmpty()) {
            int index = mDataList.size() - 1;
            mDataList.add(index, new ItemInfo(null, ItemInfo.LOADING).setId(ItemInfo.LOADING));
            notifyItemInserted(index);
        }
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        if (!mDataList.isEmpty()) {
            ItemInfo itemInfo;
            for (int i = 0; i < mDataList.size(); i++) {
                itemInfo = mDataList.get(i);
                if (itemInfo.getId() == ItemInfo.LOADING) {
                    mDataList.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }
    }

    public boolean isSelectionAllowed() {
        return allowSelection;
    }

    public void setAllowSelection(boolean allowSelection) {
        this.allowSelection = allowSelection;
    }

    public boolean areItemsClickable() {
        return areItemsClickable;
    }

    public void setAreItemsClickable(boolean areItemsClickable) {
        this.areItemsClickable = areItemsClickable;
    }

    public boolean areItemsExpandable() {
        return areItemsExpandable;
    }

    public void setAreItemsExpandable(boolean areItemsExpandable) {
        this.areItemsExpandable = areItemsExpandable;
    }

    /**
     * Clears data from the mDataList without removing the header, footer and loading views!
     */
    public void clearPureItemList() {
        int startIndex = 0;
        int endIndex = 0;
        if (hasHeader()) {
            startIndex = 1;
        }
        if (hasFooter()) {
            endIndex++;
        }
        if (mIsLoadingFooterAdded) {
            endIndex++;
        }
        for (int i = startIndex; i < mDataList.size() - endIndex; i++) {
            removeItem(i);
        }
    }

    /**
     * Clears data from the mDataList.
     */
    public void clearItemList() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    public void appendWithoutDuplicateIds(List<ItemInfo> itemInfoList) {
        validateList(itemInfoList);
        if (android.os.Build.VERSION.SDK_INT >= M) {
            ArraySet<ItemInfo> arraySet = new ArraySet<>();
            arraySet.addAll(itemInfoList);
            itemInfoList.clear();
            itemInfoList.addAll(arraySet);
        } else {
            Set<ItemInfo> set = new HashSet<>(itemInfoList);
            itemInfoList.clear();
            itemInfoList.addAll(set);
        }
        ItemInfo item;
        for (int i = 0, size = itemInfoList.size(); i < size; i++) {
            item = mDataList.get(i);
            if (mDataList.contains(item)) {
                replaceItem(i, item);
            } else {
                addItem(i, item);
            }
        }
    }

    public void appendList(List<ItemInfo> dataSet) {
        validateList(dataSet);
        mDataList.addAll(dataSet);
        notifyItemRangeInserted(getItemCount(), mDataList.size());
    }

    public void appendList(int position, List<ItemInfo> dataSet) {
        validateList(dataSet);
        mDataList.addAll(position, dataSet);
        notifyItemRangeInserted(position, mDataList.size());
    }

    public boolean isSectionHeader(int index) {
        return mDataList.get(index).getId() == SECTION_HEADER || mDataList.get(index).getLayoutId() == SECTION_HEADER;
    }

    public boolean isFooter(int index) {
        return mDataList.get(index).getId() == ItemInfo.FOOTER || mDataList.get(index).getLayoutId() == ItemInfo.FOOTER;
    }

    public boolean isHeader(int index) {
        return mDataList.get(index).getId() == ItemInfo.HEADER || mDataList.get(index).getLayoutId() == ItemInfo.HEADER;
    }

    public boolean isLoading(int index) {
        return mDataList.get(index).getId() == ItemInfo.LOADING || mDataList.get(index).getLayoutId() == ItemInfo.LOADING;
    }

    public void addSectionHeader(int index, String title) {
        addItem(index, new ItemInfo(title, SECTION_HEADER).setId(SECTION_HEADER));
    }

    public void addSectionHeaderWithId(int index, String title, long id) {
        addItem(index, new ItemInfo(title, SECTION_HEADER).setId(id));
    }

    public void removeSectionHeader(int index) throws IllegalAccessException {
        if (mDataList.get(index).getLayoutId() == SECTION_HEADER) {
            removeItem(index);
        } else {
            throw new IllegalAccessException("item at given index is not a section header!");
        }
    }

    public List<ItemInfo> getPureDataList() {
        List<ItemInfo> pureSet = new ArrayList<>();
        pureSet.addAll(mDataList);
        ItemInfo item;
        for (int i = 0; i < pureSet.size(); i++) {
            item = pureSet.get(i);
            if (item.getId() == SECTION_HEADER
                    || item.getId() == ItemInfo.FOOTER
                    || item.getId() == ItemInfo.HEADER
                    || item.getId() == ItemInfo.LOADING) {
                pureSet.remove(item);
            }
        }
        return pureSet;
    }

    public List<ItemInfo> getDataList() {
        return mDataList;
    }

    /**
     * Using a {@link Flowable} as a data source to push changes while returning {@link Disposable} for the calling component to handle the life
     * cycle.
     *
     * @param dataFlowable data source
     * @return {@link Disposable}
     */
    public Disposable setDataFlowable(Flowable<List<ItemInfo>> dataFlowable) {
        return dataFlowable.subscribe(new Consumer<List<ItemInfo>>() {
            @Override
            public void accept(List<ItemInfo> dataSet) throws Exception {
                animateTo(dataSet);
            }
        });
    }

    /**
     * Using a {@link Observable} as a data source to push changes while returning {@link Subscription} for the calling component to handle the life
     * cycle.
     *
     * @param dataObservable data source
     * @return {@link Subscription}
     */
    public Subscription setDataObservable(Observable<List<ItemInfo>> dataObservable) {
        return dataObservable.subscribe(new Action1<List<ItemInfo>>() {
            @Override
            public void call(List<ItemInfo> dataSet) {
                animateTo(dataSet);
            }
        });
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) throws IllegalStateException {
        if (allowSelection) {
            return getSelectedItemsIndices().contains(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public boolean toggleSelection(int position) throws IllegalStateException {
        if (allowSelection) {
            boolean isSelected;
            if (mSelectedItems.get(position, false)) {
                mSelectedItems.delete(position);
                isSelected = false;
            } else {
                mSelectedItems.put(position, true);
                isSelected = true;
            }
            notifyItemChanged(position);
            return isSelected;
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Set an item as selected at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void selectItem(int position) throws IllegalStateException {
        if (allowSelection) {
            mSelectedItems.put(position, true);
            notifyItemChanged(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Set an item as un-selected at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void unSelectItem(int position) throws IllegalStateException {
        if (allowSelection) {
            mSelectedItems.delete(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() throws IllegalStateException {
        if (allowSelection) {
            List<Integer> selection = getSelectedItemsIndices();
            mSelectedItems.clear();
            for (Integer i : selection) {
                notifyItemChanged(i);
            }
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() throws IllegalStateException {
        if (allowSelection) {
            return mSelectedItems.size();
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items
     */
    public List<Integer> getSelectedItemsIndices() throws IllegalStateException {
        if (allowSelection) {
            List<Integer> items = new ArrayList<>(mSelectedItems.size());
            for (int i = 0; i < mSelectedItems.size(); ++i) {
                items.add(mSelectedItems.keyAt(i));
            }
            return items;
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public List<ItemInfo> getSelectedItems() throws IllegalStateException {
        if (allowSelection) {
            List<ItemInfo> items = new ArrayList<>(mSelectedItems.size());
            for (int i = 0; i < mSelectedItems.size(); ++i) {
                items.add(mDataList.get(mSelectedItems.keyAt(i)));
            }
            return items;
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public List<Long> getSelectedItemsIds() throws IllegalStateException {
        List<ItemInfo> selectedItems = getSelectedItems();
        List<Long> ids = new ArrayList<>(selectedItems.size());
        for (ItemInfo itemInfo : selectedItems) {
            ids.add(itemInfo.getId());
        }
        return ids;
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs - rhs;
            }
        });
        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count
                        && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }
                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }
                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    //-----------------animations--------------------------//

    private void validateList(List<ItemInfo> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("The list cannot be null");
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            mDataList.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public void animateTo(List<ItemInfo> models) {
        validateList(models);
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
        mDataList = models;
    }

    public void reloadData(List<ItemInfo> newModels) {
        for (ItemInfo item : mDataList) {
            if (newModels.contains(item)) {
                newModels.remove(item);
            }
        }
        appendList(newModels);
    }

    public void removeItemById(Long id) {
        for (ItemInfo item : mDataList) {
            if (item.getId() == id) {
                removeItem(mDataList.indexOf(item));
            }
        }
    }

    public void removeItemsById(List<Long> ids) {
        List<ItemInfo> newList = new ArrayList<>(mDataList.size() - ids.size());
        for (ItemInfo item : mDataList) {
            if (!ids.contains(item.getId())) {
                newList.add(item);
            }
        }
        animateTo(newList);
    }

    private void onScroll(int position) {
        // shadow shall not increase if current position
        // is higher than scroll's final position
        if (position <= mScrollFinalPosition) {
//            setCardElevation(calculateElevation(position));
        } else {
            // thread below fixes issue #1, avoiding elevation
            // setting problems when fast scrolling
            final int mPositionBackup = position;
            position = mScrollFinalPosition;
            final int properElevation = calculateElevation(position);
            position = mPositionBackup;
//            if (getCardElevation() != properElevation) {
//                setCardElevation(properElevation);
//            }
        }
    }

    /**
     * @param value The percentage of the screen's height that is
     *              going to be scrolled to reach the final elevation
     * @return Own object
     */
    public void setScrollFinalPosition(final int value) {
        final int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        mScrollFinalPosition = dp2px((int) (screenHeight * (value / 100.0)));
    }

    /**
     * <pre>
     *     author: Blankj
     *     blog  : http://blankj.com
     *     time  : 2016/08/13
     *     desc  : 转换相关工具类
     * </pre>
     * <p>
     * Method got from:
     * https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/src/main/java/com/blankj/utilcode/util/ConvertUtils.java
     */
    private int dp2px(final int dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int calculateElevation(int position) {
        // getting back to rule of three:
        // mFinalElevation (px) = mScrollFinalPosition (px)
        // newElevation    (px) = position            (px)
        int newElevation = (mFinalElevation * position) / mScrollFinalPosition;
        // avoid values under minimum value
        if (newElevation < mInitialElevation)
            newElevation = mInitialElevation;
        return newElevation;
    }

    public ItemInfo removeItem(int position) {
        ItemInfo itemInfo = mDataList.remove(position);
        notifyItemRemoved(position);
        return itemInfo;
    }

    public void replaceItem(int position, ItemInfo itemInfo) {
        mDataList.set(position, itemInfo);
        notifyItemChanged(position, itemInfo);
    }

    public void addItem(int position, ItemInfo model) {
        mDataList.add(position, model);
        notifyItemInserted(position);
        notifyItemChanged(position, mDataList.size());
    }

    public void appendItem(ItemInfo model) {
        addItem(getItemCount(), model);
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(mDataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateRemovals(List<ItemInfo> newModels) {
        ItemInfo model;
        for (int i = mDataList.size() - 1; i >= 0; i--) {
            model = mDataList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ItemInfo> newModels) {
        ItemInfo model;
        int count = newModels.size();
        for (int i = 0; i < count; i++) {
            model = newModels.get(i);
            if (!mDataList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ItemInfo> newModels) {
        ItemInfo model;
        int fromPosition;
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            model = newModels.get(toPosition);
            fromPosition = mDataList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    private Context getContext() {
        return mLayoutInflater.getContext();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, ItemInfo itemInfo, ViewHolder holder);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClicked(int position, ItemInfo itemInfo, ViewHolder holder);
    }

    public interface OnSwipeListener {

        /**
         * Called when a view is requested a swipe.
         *
         * @param itemInfo The {@link ItemInfo} of the view to swipe.
         */
        void onItemSwipe(ItemInfo itemInfo);
    }

    public interface OnExpandListener {

        /**
         * Called when a view is requested an expand.
         *
         * @param isExpanded a boolean to indicate whether to expand or collapse.
         */
        void expand(boolean isExpanded);
    }

    public abstract static class ViewHolder<T> extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bindData(
                @NonNull T data, boolean itemSelected, int position, boolean isEnabled);
    }
}
