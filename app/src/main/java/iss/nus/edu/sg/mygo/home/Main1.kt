package iss.nus.edu.sg.mygo.home

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import com.caverock.androidsvg.SVG
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.models.Attraction
import java.io.InputStream


class Main1 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttractionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main1)

        Init_head()
        setupRecyclerView()  // 初始化 RecyclerView
    }

    fun Init_head() {
        val imageView1: ImageView = findViewById(R.id.main_head_01)
        val imageView2: ImageView = findViewById(R.id.main_head_02)

        val svgInputStream1: InputStream = resources.openRawResource(R.raw.main_head_01)
        val svgInputStream2: InputStream = resources.openRawResource(R.raw.main_head_02)

        val svg1 = SVG.getFromInputStream(svgInputStream1)
        val svg2 = SVG.getFromInputStream(svgInputStream2)

        val pictureDrawable1 = PictureDrawable(svg1.renderToPicture())
        val pictureDrawable2 = PictureDrawable(svg2.renderToPicture())
        imageView1.setImageDrawable(pictureDrawable1)
        imageView2.setImageDrawable(pictureDrawable2)
    }

    // 模拟的 Attraction 数据
    private fun getAttractionData(): List<Attraction> {
        return listOf(
            Attraction("Mount Bromo", "East Java", R.drawable.main1_image1),
            Attraction("Labengki Sombori", "Islands in Sulawesi", R.drawable.main1_image2),
            Attraction("Sailing Komodo","Labuan Bajo",R.drawable.main1_image3)
            // 更多卡片数据
        )
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_view)
        val items = getAttractionData()  // 获取数据

        // 初始化 Adapter
        adapter = AttractionAdapter(items) { position ->
            // 如果需要，可以在这里进行点击事件处理
            Toast.makeText(this, "选中了：${items[position].name}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 添加间隔
        val spaceItemDecoration = SpaceItemDecoration(dpToPx(20))  // 20dp的间隔
        recyclerView.addItemDecoration(spaceItemDecoration)
    }


    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

}
