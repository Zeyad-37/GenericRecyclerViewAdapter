package com.zeyad.gadapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zeyad.gadapter.fastscroll.SectionTitleProvider
import com.zeyad.gadapter.observables.ItemClickObservable
import com.zeyad.gadapter.observables.ItemLongClickObservable
import com.zeyad.gadapter.observables.ItemSwipeObservable
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable

abstract class GenericRecyclerViewAdapter : RecyclerView.Adapter<GenericViewHolder>, ItemTouchHelperAdapter, StickyHeaderHandler {

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

    val isSelectionAllowed: Boolean
        get() = genericAdapter.isSelectionAllowed

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

    constructor(layoutInflater: LayoutInflater) {
        genericAdapter = GenericAdapter(layoutInflater, this)
    }

    constructor(layoutInflater: LayoutInflater, list: List<ItemInfo<*>>) {
        validateList(list)
        genericAdapter = GenericAdapter(layoutInflater, list, this)
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

    override fun getItemCount(): Int {
        return genericAdapter.adapterData.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return genericAdapter.onItemMove(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        genericAdapter.onItemDismiss(position)
    }

    fun getItem(index: Int): ItemInfo<*> {
        return genericAdapter.getItem(index)
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

    fun hasItemById(itemId: Long): Boolean {
        return genericAdapter.hasItemById(itemId)
    }

    fun getItemIndexById(itemId: Long): Int {
        return genericAdapter.getItemIndexById(itemId)
    }

    @Throws(IllegalAccessException::class)
    fun getItemById(itemId: Long): ItemInfo<*> {
        return genericAdapter.getItemById(itemId)
    }

    fun disableViewHolder(index: Int) {
        genericAdapter.disableViewHolder(index)
    }

    fun enableViewHolder(index: Int) {
        genericAdapter.enableViewHolder(index)
    }

    fun setAllowSelection(allowSelection: Boolean) {
        genericAdapter.isSelectionAllowed = allowSelection
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

    fun isSectionHeader(index: Int): Boolean {
        return genericAdapter.isSectionHeader(index)
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

    private fun validateList(dataList: List<ItemInfo<*>>?) {
        if (dataList == null) {
            throw IllegalArgumentException("The list cannot be null")
        }
    }

    fun setDataList(dataList: List<ItemInfo<*>>, diffResult: DiffUtil.DiffResult?) {
        validateList(dataList)
        genericAdapter.setData(dataList)
        if (diffResult != null)
            diffResult.dispatchUpdatesTo(this)
        else
            notifyDataSetChanged()
    }

    fun setDataFlowable(dataFlowable: Flowable<List<ItemInfo<*>>>): Disposable {
        return dataFlowable.subscribe({ dataSet -> setDataList(dataSet, null) }, { throwable -> throwable.printStackTrace() })
    }
}
