package com.zeyad.gadapter

import android.support.v7.recyclerview.extensions.AsyncDifferConfig
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zeyad.gadapter.fastscroll.SectionTitleProvider
import com.zeyad.gadapter.observables.ItemClickObservable
import com.zeyad.gadapter.observables.ItemLongClickObservable
import com.zeyad.gadapter.observables.ItemSwipeObservable
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler

abstract class GenericListAdapter : ListAdapter<ItemInfo<*>, GenericViewHolder>, ItemTouchHelperAdapter, StickyHeaderHandler {

    private val genericAdapter: GenericAdapter

    override val adapterData: List<ItemInfo<*>>
        get() = genericAdapter.adapterData

    var sectionTitleProvider: SectionTitleProvider?
        get() = genericAdapter.sectionTitleProvider
        set(sectionTitleProvider) {
            genericAdapter.sectionTitleProvider = sectionTitleProvider
        }

    val itemClickObservable: ItemClickObservable
        get() = genericAdapter.itemClickObservable

    val itemLongClickObservable: ItemLongClickObservable
        get() = genericAdapter.itemLongClickObservable

    val itemSwipeObservable: ItemSwipeObservable
        get() = genericAdapter.itemSwipeObservable

    val dataList: List<ItemInfo<*>>
        get() = genericAdapter.adapterData

    val selectedItemCount: Int
        get() = genericAdapter.selectedItemCount

    val selectedItemsIndices: List<Int>
        get() = genericAdapter.selectedItemsIndices

    val selectedItems: List<ItemInfo<*>>
        get() = genericAdapter.getSelectedItems()

    val selectedItemsIds: List<Long>
        get() = genericAdapter.selectedItemsIds

    val layoutInflater: LayoutInflater
        get() = genericAdapter.layoutInflater

    protected constructor(diffCallback: DiffUtil.ItemCallback<ItemInfo<*>>, layoutInflater: LayoutInflater) : super(diffCallback) {
        genericAdapter = GenericAdapter(layoutInflater, this)
    }

    protected constructor(config: AsyncDifferConfig<ItemInfo<*>>, layoutInflater: LayoutInflater) : super(config) {
        genericAdapter = GenericAdapter(layoutInflater, this)
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        genericAdapter.onBindViewHolder(holder, position)
    }

    override fun getItemViewType(position: Int): Int {
        return genericAdapter.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        return genericAdapter.getItemId(position)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return genericAdapter.onItemMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        genericAdapter.onItemDismiss(position)
    }

    fun isSectionHeader(index: Int): Boolean {
        return genericAdapter.isSectionHeader(index)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        genericAdapter.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        genericAdapter.onItemLongClickListener = onItemLongClickListener
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        genericAdapter.onSwipeListener = onSwipeListener
    }

    fun areItemsClickable(): Boolean {
        return genericAdapter.areItemsClickable
    }

    fun setAreItemsClickable(areItemsClickable: Boolean) {
        genericAdapter.areItemsClickable = areItemsClickable
    }

    fun areItemsExpandable(): Boolean {
        return genericAdapter.areItemsExpandable
    }

    fun setAreItemsExpandable(areItemsExpandable: Boolean) {
        genericAdapter.areItemsExpandable = areItemsExpandable
    }

    fun isSelected(position: Int): Boolean {
        return genericAdapter.isSelected(position)
    }

    fun toggleSelection(position: Int): Boolean {
        return genericAdapter.toggleSelection(position)
    }

    fun selectItem(position: Int) {
        genericAdapter.selectItem(position)
    }

    fun unSelectItem(position: Int) {
        genericAdapter.unSelectItem(position)
    }

    fun clearSelection() {
        genericAdapter.clearSelection()
    }

    fun getSelectedItemsBundle(): List<*> {
        return genericAdapter.getSelectedItemsBundle()
    }

    @Deprecated("")
    private fun removeItem(position: Int): ItemInfo<*> {
        return genericAdapter.removeItem(position)
    }

    @Deprecated("")
    private fun moveItem(fromPosition: Int, toPosition: Int) {
        genericAdapter.moveItem(fromPosition, toPosition)
    }
}
