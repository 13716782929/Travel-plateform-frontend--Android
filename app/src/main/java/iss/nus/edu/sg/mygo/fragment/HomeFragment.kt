package iss.nus.edu.sg.mygo.fragment
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.adapter.AttractionAdapter
import iss.nus.edu.sg.mygo.adapter.SpaceItemDecoration
import iss.nus.edu.sg.mygo.models.FlightInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.enum.TicketType
import iss.nus.edu.sg.mygo.models.Attraction

class HomeFragment : Fragment(R.layout.home_fragment) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AttractionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)  // 初始化 RecyclerView
        setupFlightInfoLayout(view)  // 设置航班信息的布局
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.recycler_view)
        val items = getAttractionData()

        adapter = AttractionAdapter(items) { position ->
            Toast.makeText(requireContext(), "选中了：${items[position].attractionName}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val spaceItemDecoration = SpaceItemDecoration(dpToPx(20))  // 20dp的间隔
        recyclerView.addItemDecoration(spaceItemDecoration)
    }

    private fun setupFlightInfoLayout(view: View) {
        val flightInfoLayout: RelativeLayout = view.findViewById(R.id.layout_controller_tickets)
        val departureTextView: TextView = view.findViewById(R.id.departure_location)
        val arrivalTextView: TextView = view.findViewById(R.id.arrival_location)
        val departureTimeTextView: TextView = view.findViewById(R.id.departure_time)
        val arrivalTimeTextView: TextView = view.findViewById(R.id.arrival_time)

        val mockData = FlightInfo(
            departureLocation = "Singapore",
            arrivalLocation = "Tokyo",
            departureTime = "10:00 AM",
            arrivalTime = "3:00 PM"
        )
//        val mockData = FlightInfo(
//            departureLocation = "Singapore",
//            arrivalLocation = "Tokyo",
//            departureTime = null,
//            arrivalTime = "3:00 PM"
//        )

        if (mockData.departureLocation.isNullOrEmpty() ||
            mockData.arrivalLocation.isNullOrEmpty() ||
            mockData.departureTime.isNullOrEmpty() ||
            mockData.arrivalTime.isNullOrEmpty()) {
            flightInfoLayout.visibility = View.GONE
        } else {
            flightInfoLayout.visibility = View.VISIBLE
            departureTextView.text = mockData.departureLocation
            arrivalTextView.text = mockData.arrivalLocation
            departureTimeTextView.text = mockData.departureTime
            arrivalTimeTextView.text = mockData.arrivalTime
        }
    }

    private fun getAttractionData(): List<Attraction> {
        return listOf(
            Attraction(
                "1", "Mount Bromo", "East Java",
                "demo1",
                "09:00",
                true,
                TicketType.TYPE1,
                R.drawable.main1_image1
            ),
            Attraction(
                "2", "Labengki Sombori", "Islands in Sulawesi",
                "demo2",
                "09:30",
                true,
                TicketType.TYPE2,
                R.drawable.main1_image2
            ),
            Attraction(
                "3", "Sailing Komodo", "Labuan Bajo",
                "demo3",
                "07:00",
                true,
                TicketType.TYPE2,
                R.drawable.main1_image3
            )
        )
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}

