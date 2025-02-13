package iss.nus.edu.sg.mygo.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.home.FlightDetailActivity

class FlightPaymentActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_flight_payment)

        // 处理窗口边距
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container_flights)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取传递的总价格
        val totalPrice = intent.getStringExtra("totalPrice") ?: "$700.00"
        val txtTotalPrice: TextView = findViewById(R.id.txt_p_k_r)
        txtTotalPrice.text = totalPrice

        // 绑定 UI 组件
        val editCardNumber: EditText = findViewById(R.id.txt_card_number_digits)
        val editCardHolder: EditText = findViewById(R.id.txt_card_holder_name_value)
        val editCVV: EditText = findViewById(R.id.txt_c_v_v_value)
        val editExpiryDate: EditText = findViewById(R.id.txt_expiry_date_value)
        val btnBack: ImageView = findViewById(R.id.container_arrow)
        val btnConfirm: ImageView = findViewById(R.id.img_rectangle1)
        val btnClear: ImageView = findViewById(R.id.img_rectangle2)

        val imgPaypal: ImageView = findViewById(R.id.img_paypal)
        val imgVisa: ImageView = findViewById(R.id.img_visa)
        val imgMastercard: ImageView = findViewById(R.id.img_mastercard)

        // 选择支付方式的点击事件
        var selectedImageView: ImageView? = null // 记录当前选中的 ImageView
        val paymentMethods = listOf(imgPaypal, imgVisa, imgMastercard)
        paymentMethods.forEach { imageView ->
            imageView.setOnClickListener {
                // 取消之前选中的背景色
                selectedImageView?.setBackgroundColor(getColor(R.color.transparent))

                // 设置新选中的背景色
                imageView.setBackgroundColor(getColor(R.color.light_gray))

                // 更新选中项
                selectedImageView = imageView
            }
        }

        btnConfirm.setOnClickListener {
            val cardNumber = editCardNumber.text.toString().trim()
            val cardHolder = editCardHolder.text.toString().trim()
            val cvv = editCVV.text.toString().trim()
            val expiryDate = editExpiryDate.text.toString().trim()


            if (cardNumber.isEmpty() || cardHolder.isEmpty() || cvv.isEmpty() || expiryDate.isEmpty()) {
                Toast.makeText(this, "Payment failed, please try again", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Payment is successful!", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, FlightDetailActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000) // 1秒后跳转
            }
        }

        btnClear.setOnClickListener {
            editCardNumber.text.clear()
            editCardHolder.text.clear()
            editCVV.text.clear()
            editExpiryDate.text.clear()
        }


        btnBack.setOnClickListener {
            finish()
        }
    }
}
