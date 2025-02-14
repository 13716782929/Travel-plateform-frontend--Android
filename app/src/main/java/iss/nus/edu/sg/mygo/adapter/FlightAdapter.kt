package iss.nus.edu.sg.mygo.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import iss.nus.edu.sg.mygo.R
import iss.nus.edu.sg.mygo.models.FlightInfo
import iss.nus.edu.sg.mygo.home.FlightDetailActivity

// Purpose: To display flight results
// FlightAdapter using ListAdapter for efficient list updates

class FlightAdapter(private val onBookClick: (FlightInfo) -> Unit) :
    ListAdapter<FlightInfo, FlightAdapter.FlightViewHolder>(FlightDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flight_card, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val flight = getItem(position)
        holder.bind(flight)

        // 让 bookButton 点击时跳转到 FlightDetailActivity，并传递 flightId
        holder.bookButton.setOnClickListener {
            val context = it.context
            val intent = Intent(context, FlightDetailActivity::class.java).apply {
                putExtra("flightId", flight.flightId) // 传递 flightId
                Log.e("FightId","${flight.flightId}")
            }
            context.startActivity(intent)
        }
    }

    class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flightImage: ImageView = itemView.findViewById(R.id.iv_flight_image)
        private val airlineName: TextView = itemView.findViewById(R.id.airlineName)
        private val flightDate: TextView = itemView.findViewById(R.id.flightDate)
        private val flightDuration: TextView = itemView.findViewById(R.id.flightDuration)
        private val flightPrice: TextView = itemView.findViewById(R.id.tv_flight_price)
        val bookButton: Button = itemView.findViewById(R.id.bookButton)

        fun bind(flight: FlightInfo) {
            airlineName.text = flight.airlineName
            flightDate.text = flight.departureTime
            flightDuration.text = flight.duration
            flightPrice.text = "see in detail"
            flightImage.setImageResource(R.drawable.flight_placeholder_image)
        }
    }
}

// DiffUtil for efficient RecyclerView updates
class FlightDiffCallback : DiffUtil.ItemCallback<FlightInfo>() {
    override fun areItemsTheSame(oldItem: FlightInfo, newItem: FlightInfo): Boolean {
        return oldItem.departureTime == newItem.departureTime && oldItem.airlineName == newItem.airlineName
    }

    override fun areContentsTheSame(oldItem: FlightInfo, newItem: FlightInfo): Boolean {
        return oldItem == newItem
    }
}
