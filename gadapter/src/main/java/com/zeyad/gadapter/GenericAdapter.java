package com.zeyad.gadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;

import com.zeyad.gadapter.fastscroll.SectionTitleProvider;
import com.zeyad.gadapter.observables.ItemClickObservable;
import com.zeyad.gadapter.observables.ItemLongClickObservable;
import com.zeyad.gadapter.observables.ItemSwipeObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.v7.widget.RecyclerView.NO_POSITION;
import static com.zeyad.gadapter.ItemInfo.SECTION_HEADER;

public class GenericAdapter {

    private static final String SELECTION_DISABLED = "Selection mode is disabled!";
    private final SparseBooleanArray selectedItems;
    private final LayoutInflater layoutInflater;
    private List<ItemInfo> dataList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnSwipeListener onSwipeListener;
    private ArrayList<Integer> expandedPositions;
    private SectionTitleProvider sectionTitleProvider;
    private boolean allowSelection;
    private boolean areItemsExpandable;
    private boolean areItemsClickable;
    private RecyclerView.Adapter adapter;

    public GenericAdapter(LayoutInflater layoutInflater, RecyclerView.Adapter adapter) {
        this.layoutInflater = layoutInflater;
        this.selectedItems = new SparseBooleanArray();
        this.dataList = new ArrayList<>();
        this.expandedPositions = new ArrayList<>();
        this.allowSelection = false;
        this.areItemsExpandable = false;
        this.areItemsClickable = true;
        this.adapter = adapter;
    }

    public GenericAdapter(LayoutInflater layoutInflater, List<ItemInfo> dataList, RecyclerView.Adapter adapter) {
        this(layoutInflater, adapter);
        this.dataList = dataList;
    }

    public void onBindViewHolder(@NonNull final GenericViewHolder holder, int position) {
        final ItemInfo itemInfo = dataList.get(position);
        holder.bindData(itemInfo.getData(), selectedItems.get(position, false), position,
                itemInfo.isEnabled());
        if (areItemsClickable && !isSectionHeader(position)) {
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {

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
                    adapter.notifyItemChanged(adapterPosition);
                }
            });
        }
    }

    public int getItemViewType(int position) {
        return dataList.get(position).getLayoutId();
    }

    public long getItemId(int position) {
        return dataList.get(position).getId();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        moveItem(fromPosition, toPosition);
        return false;
    }

    public void onItemDismiss(int position) {
        if (onSwipeListener != null)
            onSwipeListener.onItemSwipe(getItem(position));
        removeItem(position);
    }

    public List<ItemInfo> getAdapterData() {
        return dataList;
    }

    public ItemInfo getItem(int index) {
        return dataList.get(index);
    }

    public SectionTitleProvider getSectionTitleProvider() {
        return sectionTitleProvider;
    }

    public void setSectionTitleProvider(SectionTitleProvider mSectionTitleProvider) {
        this.sectionTitleProvider = mSectionTitleProvider;
    }

    public boolean isSectionHeader(int index) {
        return dataList.get(index).getId() == SECTION_HEADER || dataList.get(index).getLayoutId() == SECTION_HEADER;
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

    public boolean isSelected(int position) {
        if (allowSelection) {
            return getSelectedItemsIndices().contains(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

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
            adapter.notifyItemChanged(position);
            return isSelected;
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public void selectItem(int position) {
        if (allowSelection) {
            selectedItems.put(position, true);
            adapter.notifyItemChanged(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public void unSelectItem(int position) {
        if (allowSelection) {
            selectedItems.delete(position);
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public void clearSelection() {
        if (allowSelection) {
            List<Integer> selection = getSelectedItemsIndices();
            selectedItems.clear();
            for (Integer i : selection) {
                adapter.notifyItemChanged(i);
            }
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

    public int getSelectedItemCount() {
        if (allowSelection) {
            return selectedItems.size();
        } else {
            throw new IllegalStateException(SELECTION_DISABLED);
        }
    }

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

    public LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public ItemInfo removeItem(int position) {
        ItemInfo itemInfo = dataList.remove(position);
        adapter.notifyItemRemoved(position);
        return itemInfo;
    }

    public void moveItem(int fromPosition, int toPosition) {
        Collections.swap(dataList, fromPosition, toPosition);
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    public void setData(List<ItemInfo> dataList) {
        this.dataList = dataList;
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
        adapter.notifyItemChanged(index);
    }
}
