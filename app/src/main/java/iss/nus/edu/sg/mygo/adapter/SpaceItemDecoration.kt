package iss.nus.edu.sg.mygo.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
/**
 * @ClassName SpaceItemDecoration
 * @Description
 * @Author Wang Chang
 * @StudentID A0310544R
 * @Date 2025/1/25
 * @Version 1.3
 */

class SpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        // 设置上下左右的间距
        outRect.left = space
        outRect.right = space
        outRect.top = space
        outRect.bottom = space
    }
}


//class SpaceItemDecoration(private val space_l: Int,private val space_r: Int,private val space_t: Int,private val space_b: Int) : RecyclerView.ItemDecoration() {
//    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
//        super.getItemOffsets(outRect, view, parent, state)
//        // 设置上下左右的间距
//        outRect.left = space_l
//        outRect.right = space_r
//        outRect.top = space_t
//        outRect.bottom = space_b
//    }
//}
