package iss.nus.edu.sg.mygo.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AttractionSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space / 2
        outRect.right = space / 2
        outRect.bottom = space*5 // 设置 item 之间的垂直间距
        if (parent.getChildAdapterPosition(view) < 2) {
            outRect.top = space*3 // 仅为第一行 item 设置顶部间距
        }
    }
}
