package com.zeyad.gadapter;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.AsyncDifferConfig;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zeyad.gadapter.fastscroll.SectionTitleProvider;
import com.zeyad.gadapter.observables.ItemClickObservable;
import com.zeyad.gadapter.observables.ItemLongClickObservable;
import com.zeyad.gadapter.observables.ItemSwipeObservable;
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler;

import java.util.List;

@SuppressWarnings("unused")
public abstract class GenericListAdapter extends ListAdapter<ItemInfo, GenericViewHolder>
        implements ItemTouchHelperAdapter, StickyHeaderHandler {

    private final GenericAdapter genericAdapter;

    protected GenericListAdapter(@NonNull DiffUtil.ItemCallback<ItemInfo> diffCallback, LayoutInflater layoutInflater) {
        super(diffCallback);
        genericAdapter = new GenericAdapter(layoutInflater, this);

    }

    protected GenericListAdapter(@NonNull AsyncDifferConfig<ItemInfo> config, LayoutInflater layoutInflater) {
        super(config);
        genericAdapter = new GenericAdapter(layoutInflater, this);
    }

    @NonNull
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
    public boolean onItemMove(int fromPosition, int toPosition) {
        return genericAdapter.onItemMove(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        genericAdapter.onItemDismiss(position);
    }

    @NonNull
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

    public boolean isSectionHeader(int index) {
        return genericAdapter.isSectionHeader(index);
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
}
