package com.zeyad.gadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.zeyad.gadapter.fastscroll.SectionTitleProvider
import com.zeyad.gadapter.observables.ItemClickObservable
import com.zeyad.gadapter.observables.ItemLongClickObservable
import com.zeyad.gadapter.observables.ItemSwipeObservable
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler

abstract class GenericListAdapter : ListAdapter<ItemInfo<*>, GenericViewHolder<*>>, ItemTouchHelperAdapter, StickyHeaderHandler {

    private val genericAdapter: GenericAdapter

    override val adapterData: List<ItemInfo<*>>
        get() = genericAdapter.adapterData

    var sectionTitleProvider: SectionTitleProvider?
        get() = genericAdapter.sectionTitleProvider
        set(sectionTitleProvider) {
            genericAdapter.sectionTitleProvider = sectionTitleProvider
        }

    val firstItem: ItemInfo<*>
        get() = genericAdapter.getItem(0)

    val lastItem: ItemInfo<*>
        get() = genericAdapter.getItem(genericAdapter.adapterData.size - 1)

    val itemClickObservable: ItemClickObservable
        get() = genericAdapter.itemClickObservable

    val itemLongClickObservable: ItemLongClickObservable
        get() = genericAdapter.itemLongClickObservable

    val itemSwipeObservable: ItemSwipeObservable
        get() = genericAdapter.itemSwipeObservable

    val areItemsSelectable: Boolean
        get() = genericAdapter.areItemsSelectable

    val areItemsExpandable: Boolean
        get() = genericAdapter.areItemsExpandable

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


    protected constructor(diffCallback: DiffUtil.ItemCallback<ItemInfo<*>>) : super(diffCallback) {
        genericAdapter = GenericAdapter(this)
    }

    protected constructor(config: AsyncDifferConfig<ItemInfo<*>>) : super(config) {
        genericAdapter = GenericAdapter(this)
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*>

    override fun onBindViewHolder(holder: GenericViewHolder<*>, position: Int) =
            genericAdapter.onBindViewHolder(holder as GenericViewHolder<Any>, position)

    override fun getItemViewType(position: Int): Int = genericAdapter.getItemViewType(position)

    override fun getItemId(position: Int): Long = genericAdapter.getItemId(position)

    override fun onItemDismiss(position: Int) = genericAdapter.onItemDismiss(position)

    fun isSectionHeader(index: Int): Boolean = genericAdapter.isSectionHeader(index)

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        genericAdapter.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        genericAdapter.onItemLongClickListener = onItemLongClickListener
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        genericAdapter.onSwipeListener = onSwipeListener
    }

    fun areItemsClickable(): Boolean = genericAdapter.areItemsClickable

    fun setAreItemsClickable(areItemsClickable: Boolean) {
        genericAdapter.areItemsClickable = areItemsClickable
    }

    fun areItemsExpandable(): Boolean = genericAdapter.areItemsExpandable

    fun setAreItemsExpandable(areItemsExpandable: Boolean) {
        genericAdapter.areItemsExpandable = areItemsExpandable
    }

    fun isSelected(position: Int): Boolean = genericAdapter.isSelected(position)

    fun toggleSelection(position: Int): Boolean = genericAdapter.toggleSelection(position)

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
    private fun removeItem(position: Int): ItemInfo<*> = genericAdapter.removeItem(position)
}
