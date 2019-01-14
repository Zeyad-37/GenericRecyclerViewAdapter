package com.zeyad.gadapter;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static com.zeyad.gadapter.ItemInfo.SECTION_HEADER;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericRecyclerViewAdapter extends RecyclerView.Adapter<GenericViewHolder>
        implements ItemTouchHelperAdapter, StickyHeaderHandler {

    private static final String SELECTION_DISABLED = "Selection mode is disabled!";
    private final LayoutInflater layoutInflater;
    private final SparseBooleanArray selectedItems;
    private List<ItemInfo> dataList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnSwipeListener onSwipeListener;
    private SectionTitleProvider sectionTitleProvider;
    private ArrayList<Integer> expandedPositions;
    private boolean allowSelection;
    private boolean areItemsExpandable;
    private boolean areItemsClickable;

    public GenericRecyclerViewAdapter(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        dataList = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
        expandedPositions = new ArrayList<>();
        areItemsClickable = true;
    }

    public GenericRecyclerViewAdapter(LayoutInflater layoutInflater, List<ItemInfo> list) {
        validateList(list);
        this.layoutInflater = layoutInflater;
        dataList = list;
        selectedItems = new SparseBooleanArray();
        expandedPositions = new ArrayList<>();
        areItemsClickable = true;
    }

    @Override
    public abstract GenericViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(final GenericViewHolder holder, int position) {
        final ItemInfo itemInfo = dataList.get(position);
        holder.bindData(itemInfo.getData(), selectedItems.get(position, false), position,
                itemInfo.isEnabled());
        if (areItemsClickable && !isSectionHeader(position)) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int adapterPosition = holder.getAdapterPosition();
                        if (adapterPosition != NO_POSITION) {
                            onItemClickListener.onItemClicked(adapterPosition, itemInfo, holder);
                        }
                    }
                });
            }
            if (onItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int adapterPosition = holder.getAdapterPosition();
                        return adapterPosition != NO_POSITION
                                && onItemLongClickListener.onItemLongClicked(adapterPosition, itemInfo, holder);
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
                    notifyItemChanged(adapterPosition);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getLayoutId();
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        if (onSwipeListener != null)
            onSwipeListener.onItemSwipe(getItem(position));
        removeItem(position);
    }

    @Override
    public List<ItemInfo> getAdapterData() {
        return dataList;
    }

    public SectionTitleProvider getSectionTitleProvider() {
        return sectionTitleProvider;
    }

    public void setSectionTitleProvider(SectionTitleProvider mSectionTitleProvider) {
        this.sectionTitleProvider = mSectionTitleProvider;
    }

    public ItemInfo getItem(int index) {
        return dataList.get(index);
    }

    public ItemInfo getFirstItem() {
        return dataList.get(0);
    }

    public ItemInfo getLastItem() {
        return dataList.get(dataList.size() - 1);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ItemClickObservable getItemClickObservable() {
        return new ItemClickObservable(this);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public ItemLongClickObservable getItemLongClickObservable() {
        return new ItemLongClickObservable(this);
    }

    public void setOnItemSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public ItemSwipeObservable getItemSwipeObservable() {
        return new ItemSwipeObservable(this);
    }

    public boolean hasItemById(long itemId) {
        for (ItemInfo itemInfo : dataList) {
            if (itemInfo.getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public int getItemIndexById(long itemId) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public ItemInfo getItemById(long itemId) throws IllegalAccessException {
        for (ItemInfo itemInfo : dataList) {
            if (itemInfo.getId() == itemId) {
                return itemInfo;
            }
        }
        throw new IllegalAccessException("Item with id " + itemId + " does not exist!");
    }

    public void disableViewHolder(int index) {
        dataList.get(index).setEnabled(false);
    }

    public void enableViewHolder(int index) {
        dataList.get(index).setEnabled(true);
        notifyItemChanged(index);
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

    public boolean isSectionHeader(int index) {
        return dataList.get(index).getId() == SECTION_HEADER || dataList.get(index).getLayoutId() == SECTION_HEADER;
    }

    public List<ItemInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<ItemInfo> dataList, DiffUtil.DiffResult diffResult) {
        validateList(dataList);
        this.dataList = dataList;
        if (diffResult != null)
            diffResult.dispatchUpdatesTo(this);
        else notifyDataSetChanged();
    }

    /**
     * Using a {@link Flowable} as a data source to push changes while returning {@link Disposable}
     * for the calling component to handle the life-cycle.
     *
     * @param dataFlowable data source
     * @return {@link Disposable}
     */
    public Disposable setDataFlowable(Flowable<List<ItemInfo>> dataFlowable) {
        return dataFlowable.subscribe(new Consumer<List<ItemInfo>>() {
            @Override
            public void accept(List<ItemInfo> dataSet) {
                setDataList(dataSet, null);
            }
        });
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
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
    public boolean toggleSelection(int position) {
        if (allowSelection) {
            boolean isSelected;
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
                isSelected = false;
            } else {
                selectedItems.put(position, true);
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
    public void selectItem(int position) {
        if (allowSelection) {
            selectedItems.put(position, true);
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
    public void unSelectItem(int position) {
        if (allowSelection) {
            selectedItems.delete(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() {
        if (allowSelection) {
            List<Integer> selection = getSelectedItemsIndices();
            selectedItems.clear();
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
    public int getSelectedItemCount() {
        if (allowSelection) {
            return selectedItems.size();
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items
     */
    public List<Integer> getSelectedItemsIndices() {
        if (allowSelection) {
            List<Integer> items = new ArrayList<>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); ++i) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public List<ItemInfo> getSelectedItems() {
        if (allowSelection) {
            List<ItemInfo> items = new ArrayList<>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); ++i) {
                items.add(dataList.get(selectedItems.keyAt(i)));
            }
            return items;
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public List<Long> getSelectedItemsIds() {
        List<ItemInfo> selectedItems = getSelectedItems();
        List<Long> ids = new ArrayList<>(selectedItems.size());
        for (ItemInfo itemInfo : selectedItems) {
            ids.add(itemInfo.getId());
        }
        return ids;
    }

    public <T> List<T> getSelectedItemsBundle() {
        List<ItemInfo> selectedItems = getSelectedItems();
        List<T> bundles = new ArrayList<>(selectedItems.size());
        for (ItemInfo itemInfo : selectedItems) {
            bundles.add(itemInfo.<T>getData());
        }
        return bundles;
    }

    private void validateList(List<ItemInfo> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("The list cannot be null");
        }
    }

    @Deprecated
    private ItemInfo removeItem(int position) {
        ItemInfo itemInfo = dataList.remove(position);
        notifyItemRemoved(position);
        return itemInfo;
    }

    @Deprecated
    private void moveItem(int fromPosition, int toPosition) {
        Collections.swap(dataList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }
}
