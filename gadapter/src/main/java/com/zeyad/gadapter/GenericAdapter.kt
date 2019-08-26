package com.zeyad.gadapter

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.util.SparseBooleanArray
import com.zeyad.gadapter.fastscroll.SectionTitleProvider
import com.zeyad.gadapter.observables.ItemClickObservable
import com.zeyad.gadapter.observables.ItemLongClickObservable
import com.zeyad.gadapter.observables.ItemSwipeObservable
import java.util.Collections

class GenericAdapter(private val adapter: RecyclerView.Adapter<*>) {
    private val selectedItems: SparseBooleanArray = SparseBooleanArray()
    private val dataList: MutableList<ItemInfo<*>> = mutableListOf()
    private val expandedPositions: MutableList<Int> = mutableListOf()
    var onItemClickListener: OnItemClickListener? = null
    var onItemLongClickListener: OnItemLongClickListener? = null
    var onSwipeListener: OnSwipeListener? = null
    var sectionTitleProvider: SectionTitleProvider? = null
    var isSelectionAllowed: Boolean = false
    var areItemsExpandable: Boolean = false
    var areItemsClickable: Boolean = false

    val adapterData: List<ItemInfo<*>>
        get() = dataList

    val itemClickObservable: ItemClickObservable
        get() = ItemClickObservable(this)

    val itemLongClickObservable: ItemLongClickObservable
        get() = ItemLongClickObservable(this)

    val itemSwipeObservable: ItemSwipeObservable
        get() = ItemSwipeObservable(this)

    val selectedItemCount: Int
        get() = if (isSelectionAllowed) {
            selectedItems.size()
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }

    val selectedItemsIndices: List<Int>
        get() {
            if (isSelectionAllowed) {
                val items = ArrayList<Int>(selectedItems.size())
                for (i in 0 until selectedItems.size()) {
                    items.add(selectedItems.keyAt(i))
                }
                return items
            } else {
                throw IllegalStateException(SELECTION_DISABLED)
            }
        }

    val selectedItemsIds: List<Long>
        get() {
            val selectedItems = getSelectedItems()
            val ids = ArrayList<Long>(selectedItems.size)
            for (itemInfo in selectedItems) {
                ids.add(itemInfo.id)
            }
            return ids
        }

    init {
        this.isSelectionAllowed = false
        this.areItemsExpandable = false
        this.areItemsClickable = true
    }

    constructor(dataList: List<ItemInfo<*>>, adapter: RecyclerView.Adapter<*>) : this(adapter) {
        setData(dataList)
    }

    fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        val itemInfo = dataList[position]
        holder.bindData(itemInfo.data, position, selectedItems.get(position, false),
                itemInfo.isEnabled, expandedPositions.contains(position))
        if (areItemsClickable && !isSectionHeader(position)) {
            holder.itemView.setOnClickListener {
                val adapterPosition = holder.adapterPosition
                if (areItemsExpandable && holder is OnExpandListener) {
                    holder.expand(expandedPositions.contains(position))
                    holder.itemView.isActivated = true
                    if (expandedPositions.contains(adapterPosition)) {
                        for (i in expandedPositions.indices) {
                            if (expandedPositions[i] == adapterPosition) {
                                expandedPositions.removeAt(i)
                                break
                            }
                        }
                    } else {
                        expandedPositions.add(adapterPosition)
                    }
                    adapter.notifyItemChanged(adapterPosition)
                } else if (adapterPosition > NO_POSITION) {
                    onItemClickListener?.onItemClicked(adapterPosition, itemInfo, holder)
                }
            }
            holder.itemView.setOnLongClickListener {
                val adapterPosition = holder.adapterPosition
                onItemLongClickListener?.let {
                    it.onItemLongClicked(adapterPosition, itemInfo, holder) && adapterPosition != NO_POSITION
                } ?: run { false }
            }
        }
    }

    fun getItemViewType(position: Int): Int = dataList[position].layoutId

    fun getItemId(position: Int): Long = dataList[position].id

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        moveItem(fromPosition, toPosition)
        return false
    }

    fun onItemDismiss(position: Int) {
        if (onSwipeListener != null)
            onSwipeListener?.onItemSwipe(getItem(position))
        removeItem(position)
    }

    fun getItem(index: Int): ItemInfo<*> = dataList[index]

    fun isSectionHeader(index: Int): Boolean =
            dataList[index].id == SECTION_HEADER.toLong() || dataList[index].layoutId == SECTION_HEADER

    fun isSelected(position: Int): Boolean {
        return if (isSelectionAllowed) {
            selectedItemsIndices.contains(position)
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }
    }

    fun toggleSelection(position: Int): Boolean {
        if (isSelectionAllowed) {
            val isSelected = if (selectedItems.get(position, false)) {
                selectedItems.delete(position)
                false
            } else {
                selectedItems.put(position, true)
                true
            }
            adapter.notifyItemChanged(position)
            return isSelected
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }
    }

    fun selectItem(position: Int) {
        if (isSelectionAllowed) {
            selectedItems.put(position, true)
            adapter.notifyItemChanged(position)
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }
    }

    fun unSelectItem(position: Int) {
        if (isSelectionAllowed) {
            selectedItems.delete(position)
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }
    }

    fun clearSelection() {
        if (isSelectionAllowed) {
            val selection = selectedItemsIndices
            selectedItems.clear()
            for (i in selection) {
                adapter.notifyItemChanged(i)
            }
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }
    }

    fun getSelectedItems(): List<ItemInfo<*>> {
        if (isSelectionAllowed) {
            val items = ArrayList<ItemInfo<*>>(selectedItems.size())
            for (i in 0 until selectedItems.size()) {
                items.add(dataList[selectedItems.keyAt(i)])
            }
            return items
        } else {
            throw IllegalStateException(SELECTION_DISABLED)
        }
    }

    fun getSelectedItemsBundle(): List<*> {
        val selectedItems = getSelectedItems()
        val bundles = mutableListOf<Any?>()
        for (itemInfo in selectedItems) {
            bundles.add(itemInfo.data)
        }
        return bundles
    }

    fun removeItem(position: Int): ItemInfo<*> {
        val itemInfo = dataList.removeAt(position)
        adapter.notifyItemRemoved(position)
        return itemInfo
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        Collections.swap(dataList, fromPosition, toPosition)
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    fun setData(dataList: List<ItemInfo<*>>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
    }

    fun hasItemById(itemId: Long): Boolean {
        for (itemInfo in dataList) {
            if (itemInfo.id == itemId) {
                return true
            }
        }
        return false
    }

    fun getItemIndexById(itemId: Long): Int {
        for (i in dataList.indices) {
            if (dataList[i].id == itemId) {
                return i
            }
        }
        return -1
    }

    @Throws(IllegalAccessException::class)
    fun getItemById(itemId: Long): ItemInfo<*> {
        for (itemInfo in dataList) {
            if (itemInfo.id == itemId) {
                return itemInfo
            }
        }
        throw IllegalAccessException("Item with id $itemId does not exist!")
    }

    fun disableViewHolder(index: Int) {
        dataList[index].isEnabled = false
    }

    fun enableViewHolder(index: Int) {
        dataList[index].isEnabled = true
        adapter.notifyItemChanged(index)
    }

    companion object {

        private const val SELECTION_DISABLED = "Selection mode is disabled!"
        const val SECTION_HEADER = Int.MIN_VALUE
    }
}
