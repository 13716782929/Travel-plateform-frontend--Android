package iss.nus.edu.sg.mygo.adapter

/**
 * @ClassName FlightAdapter
 * @Description
 * @Author Siti Alifah Binte Yahya
 * @StudentID A0295324B
 * @Date 13 Feb 2025
 * @Version 1.0
 */


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

//Purpose: To display flight results
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
        holder.bookButton.setOnClickListener { onBookClick(flight) }
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
            flightDate.text = flight.flightDate
            flightDuration.text = flight.duration
            flightPrice.text = flight.price
            flightImage.setImageResource(R.drawable.flight_placeholder_image) //
        }
    }
}

// DiffUtil for efficient RecyclerView updates
class FlightDiffCallback : DiffUtil.ItemCallback<FlightInfo>() {
    override fun areItemsTheSame(oldItem: FlightInfo, newItem: FlightInfo): Boolean {
        return oldItem.flightDate == newItem.flightDate && oldItem.airlineName == newItem.airlineName
    }

    override fun areContentsTheSame(oldItem: FlightInfo, newItem: FlightInfo): Boolean {
        return oldItem == newItem
    }
}
