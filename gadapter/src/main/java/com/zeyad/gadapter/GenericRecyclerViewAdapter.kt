package com.zeyad.gadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zeyad.gadapter.fastscroll.SectionTitleProvider
import com.zeyad.gadapter.observables.ItemClickObservable
import com.zeyad.gadapter.observables.ItemLongClickObservable
import com.zeyad.gadapter.observables.ItemSwipeObservable
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

abstract class GenericRecyclerViewAdapter(list: List<ItemInfo<*>> = emptyList()) : ItemTouchHelperAdapter,
        StickyHeaderHandler, RecyclerView.Adapter<GenericViewHolder<*>>() {

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

    init {
        validateList(list)
        genericAdapter = GenericAdapter(list, this)
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*>

    override fun onBindViewHolder(holder: GenericViewHolder<*>, position: Int) =
            genericAdapter.onBindViewHolder(holder as GenericViewHolder<Any>, position)

    override fun getItemViewType(position: Int): Int = genericAdapter.getItemViewType(position)

    override fun getItemId(position: Int): Long = genericAdapter.getItemId(position)

    override fun getItemCount(): Int = genericAdapter.adapterData.size

    override fun onItemDismiss(position: Int) = genericAdapter.onItemDismiss(position)

    fun getItem(index: Int): ItemInfo<*> = genericAdapter.getItem(index)

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        genericAdapter.onItemClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        genericAdapter.onItemLongClickListener = onItemLongClickListener
    }

    fun setOnSwipeListener(onSwipeListener: OnSwipeListener) {
        genericAdapter.onSwipeListener = onSwipeListener
    }

    fun hasItemById(itemId: Long): Boolean = genericAdapter.hasItemById(itemId)

    fun getItemIndexById(itemId: Long): Int = genericAdapter.getItemIndexById(itemId)

    @Throws(IllegalAccessException::class)
    fun getItemById(itemId: Long): ItemInfo<*> = genericAdapter.getItemById(itemId)

    fun disableViewHolder(index: Int) = genericAdapter.disableViewHolder(index)

    fun enableViewHolder(index: Int) = genericAdapter.enableViewHolder(index)

    fun setAllowSelection(allowSelection: Boolean) {
        genericAdapter.areItemsSelectable = allowSelection
    }

    fun areItemsClickable(): Boolean = genericAdapter.areItemsClickable

    fun setAreItemsClickable(areItemsClickable: Boolean) {
        genericAdapter.areItemsClickable = areItemsClickable
    }

    fun areItemsExpandable(): Boolean = genericAdapter.areItemsExpandable

    fun setAreItemsExpandable(areItemsExpandable: Boolean) {
        genericAdapter.areItemsExpandable = areItemsExpandable
    }

    fun isSectionHeader(index: Int): Boolean = genericAdapter.isSectionHeader(index)

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

    private fun validateList(dataList: List<ItemInfo<*>>?) {
        requireNotNull(dataList) { "The list cannot be null" }
    }

    fun setDataList(dataList: List<ItemInfo<*>>, diffResult: DiffUtil.DiffResult?) {
        validateList(dataList)
        genericAdapter.setData(dataList)
        if (diffResult != null)
            diffResult.dispatchUpdatesTo(this)
        else
            notifyDataSetChanged()
    }

    fun setDataFlowable(dataFlowable: Flowable<List<ItemInfo<*>>>): Disposable =
            dataFlowable.subscribe({ dataSet -> setDataList(dataSet, null) }, { throwable -> throwable.printStackTrace() })
}
