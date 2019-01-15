package com.zeyad.gadapter

data class ItemInfo(private val data: Any,
                    val layoutId: Int,
                    val id: Long = 0,
                    var isEnabled: Boolean = true) {

    fun <T> getData(): T {
        return data as T
    }

    companion object {
        const val SECTION_HEADER = 1
    }
}
