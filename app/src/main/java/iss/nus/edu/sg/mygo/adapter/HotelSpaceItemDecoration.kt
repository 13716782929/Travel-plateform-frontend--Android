package iss.nus.edu.sg.mygo.adapter

/**
 * @Description
 * @Author YAO YIYANG
 * @StudentID A0294873L
 * @Date 2025/2/11
 * @Version 1.0
 */

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HotelSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = space // 设置底部间距
    }
}


