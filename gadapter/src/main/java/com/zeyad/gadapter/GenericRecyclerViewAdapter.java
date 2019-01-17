package com.zeyad.gadapter;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zeyad.gadapter.fastscroll.SectionTitleProvider;
import com.zeyad.gadapter.observables.ItemClickObservable;
import com.zeyad.gadapter.observables.ItemLongClickObservable;
import com.zeyad.gadapter.observables.ItemSwipeObservable;
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public abstract class GenericRecyclerViewAdapter extends RecyclerView.Adapter<GenericViewHolder>
        implements ItemTouchHelperAdapter, StickyHeaderHandler {

    private final GenericAdapter genericAdapter;

    public GenericRecyclerViewAdapter(LayoutInflater layoutInflater) {
        genericAdapter = new GenericAdapter(layoutInflater, this);
    }

    public GenericRecyclerViewAdapter(LayoutInflater layoutInflater, List<ItemInfo> list) {
        validateList(list);
        genericAdapter = new GenericAdapter(layoutInflater, list, this);
    }

    @Override
    public abstract GenericViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(@NonNull final GenericViewHolder holder, int position) {
        genericAdapter.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        return genericAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return genericAdapter.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return genericAdapter.getAdapterData() != null ? genericAdapter.getAdapterData().size() : 0;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return genericAdapter.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        genericAdapter.onItemDismiss(position);
    }

    @Override
    public List<ItemInfo> getAdapterData() {
        return genericAdapter.getAdapterData();
    }

    public SectionTitleProvider getSectionTitleProvider() {
        return genericAdapter.getSectionTitleProvider();
    }

    public void setSectionTitleProvider(SectionTitleProvider sectionTitleProvider) {
        genericAdapter.setSectionTitleProvider(sectionTitleProvider);
    }

    public ItemInfo getItem(int index) {
        return genericAdapter.getItem(index);
    }

    public ItemInfo getFirstItem() {
        return genericAdapter.getItem(0);
    }

    public ItemInfo getLastItem() {
        return genericAdapter.getItem(genericAdapter.getAdapterData().size() - 1);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        genericAdapter.setOnItemClickListener(onItemClickListener);
    }

    public ItemClickObservable getItemClickObservable() {
        return genericAdapter.getItemClickObservable();
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        genericAdapter.setOnItemLongClickListener(onItemLongClickListener);
    }

    public ItemLongClickObservable getItemLongClickObservable() {
        return genericAdapter.getItemLongClickObservable();
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        genericAdapter.setOnSwipeListener(onSwipeListener);
    }

    public ItemSwipeObservable getItemSwipeObservable() {
        return genericAdapter.getItemSwipeObservable();
    }

    public boolean hasItemById(long itemId) {
        return genericAdapter.hasItemById(itemId);
    }

    public int getItemIndexById(long itemId) {
        return genericAdapter.getItemIndexById(itemId);
    }

    public ItemInfo getItemById(long itemId) throws IllegalAccessException {
        return genericAdapter.getItemById(itemId);
    }

    public void disableViewHolder(int index) {
        genericAdapter.disableViewHolder(index);
    }

    public void enableViewHolder(int index) {
        genericAdapter.enableViewHolder(index);
    }

    public boolean isSelectionAllowed() {
        return genericAdapter.isSelectionAllowed();
    }

    public void setAllowSelection(boolean allowSelection) {
        genericAdapter.setSelectionAllowed(allowSelection);
    }

    public boolean areItemsClickable() {
        return genericAdapter.getAreItemsClickable();
    }

    public void setAreItemsClickable(boolean areItemsClickable) {
        genericAdapter.setAreItemsClickable(areItemsClickable);
    }

    public boolean areItemsExpandable() {
        return genericAdapter.getAreItemsExpandable();
    }

    public void setAreItemsExpandable(boolean areItemsExpandable) {
        genericAdapter.setAreItemsExpandable(areItemsExpandable);
    }

    public boolean isSectionHeader(int index) {
        return genericAdapter.isSectionHeader(index);
    }

    public List<ItemInfo> getDataList() {
        return genericAdapter.getAdapterData();
    }

    public boolean isSelected(int position) {
        return genericAdapter.isSelected(position);
    }

    public boolean toggleSelection(int position) {
        return genericAdapter.toggleSelection(position);
    }

    public void selectItem(int position) {
        genericAdapter.selectItem(position);
    }

    public void unSelectItem(int position) {
        genericAdapter.unSelectItem(position);
    }

    public void clearSelection() {
        genericAdapter.clearSelection();
    }

    public int getSelectedItemCount() {
        return genericAdapter.getSelectedItemCount();
    }

    public List<Integer> getSelectedItemsIndices() {
        return genericAdapter.getSelectedItemsIndices();
    }

    public List<ItemInfo> getSelectedItems() {
        return genericAdapter.getSelectedItems();
    }

    public List<Long> getSelectedItemsIds() {
        return genericAdapter.getSelectedItemsIds();
    }

    public <T> List<T> getSelectedItemsBundle() {
        return genericAdapter.getSelectedItemsBundle();
    }

    @Deprecated
    private ItemInfo removeItem(int position) {
        return genericAdapter.removeItem(position);
    }

    @Deprecated
    private void moveItem(int fromPosition, int toPosition) {
        genericAdapter.moveItem(fromPosition, toPosition);
    }

    public LayoutInflater getLayoutInflater() {
        return genericAdapter.getLayoutInflater();
    }

    private void validateList(List<ItemInfo> dataList) {
        if (dataList == null) {
            throw new IllegalArgumentException("The list cannot be null");
        }
    }

    public void setDataList(List<ItemInfo> dataList, DiffUtil.DiffResult diffResult) {
        validateList(dataList);
        genericAdapter.setData(dataList);
        if (diffResult != null)
            diffResult.dispatchUpdatesTo(this);
        else notifyDataSetChanged();
    }

    public Disposable setDataFlowable(Flowable<List<ItemInfo>> dataFlowable) {
        return dataFlowable.subscribe(new Consumer<List<ItemInfo>>() {
            @Override
            public void accept(List<ItemInfo> dataSet) {
                setDataList(dataSet, null);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }
}
