package iss.nus.edu.sg.mygo.home

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import iss.nus.edu.sg.mygo.R
import java.io.InputStream
import com.caverock.androidsvg.SVG


class Main1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main1)

        Init_head()

    }

    fun Init_head(){
        // 获取 ImageView
        val imageView1: ImageView = findViewById(R.id.main_head_01)
        val imageView2: ImageView = findViewById(R.id.main_head_02)

        // 从 raw 目录读取 SVG 文件
        val svgInputStream1: InputStream = resources.openRawResource(R.raw.main_head_01)
        val svgInputStream2: InputStream = resources.openRawResource(R.raw.main_head_02)

        // 解析 SVG 文件
        val svg1 = SVG.getFromInputStream(svgInputStream1)
        val svg2 = SVG.getFromInputStream(svgInputStream2)

        // 使用 PictureDrawable 渲染 SVG，并设置到 ImageView
        val pictureDrawable1 = PictureDrawable(svg1.renderToPicture())
        val pictureDrawable2 = PictureDrawable(svg2.renderToPicture())
        imageView1.setImageDrawable(pictureDrawable1)
        imageView2.setImageDrawable(pictureDrawable2)
    }
}