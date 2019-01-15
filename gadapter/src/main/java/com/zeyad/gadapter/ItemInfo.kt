package com.zeyad.gadapter

data class ItemInfo(private var data: Any,
                    val layoutId: Int,
                    private var id: Long = 0,
                    private var isEnabled: Boolean = true) {


    fun setId(id: Long): ItemInfo {
        this.id = id
        return this
    }

    fun setEnabled(enabled: Boolean): ItemInfo {
        isEnabled = enabled
        return this
    }

    fun <T> getData(): T {
        return data as T
    }

    companion object {
        const val SECTION_HEADER = 1
    }
}
