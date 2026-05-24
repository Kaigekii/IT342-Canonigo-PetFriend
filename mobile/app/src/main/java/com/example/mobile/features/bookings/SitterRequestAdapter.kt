package com.example.mobile.features.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Locale

class SitterRequestAdapter(
    private var bookings: List<Booking>,
    private val onAccept: (Booking) -> Unit,
    private val onDecline: (Booking) -> Unit
) : RecyclerView.Adapter<SitterRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOwner: TextView = view.findViewById(R.id.tvRequestOwner)
        val tvService: TextView = view.findViewById(R.id.tvRequestService)
        val tvDate: TextView = view.findViewById(R.id.tvRequestDate)
        val tvPets: TextView = view.findViewById(R.id.tvRequestPets)
        val btnAccept: MaterialButton = view.findViewById(R.id.btnAcceptRequest)
        val btnDecline: MaterialButton = view.findViewById(R.id.btnDeclineRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sitter_request_card, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvOwner.text = booking.ownerName
        holder.tvService.text = booking.serviceType
        holder.tvPets.text = if (booking.petNames.isEmpty()) "Pets: -" else "Pets: ${booking.petNames.joinToString(", ")}" 

        val dateLabel = try {
            val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val timeParser = SimpleDateFormat("HH:mm:ss", Locale.US)
            val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            val timeFormatter = SimpleDateFormat("h:mm a", Locale.US)

            val parsedDate = dateParser.parse(booking.date)
            val startTime = timeParser.parse(booking.startTime)
            val endTime = timeParser.parse(booking.endTime)

            val dateText = if (parsedDate != null) dateFormatter.format(parsedDate) else booking.date
            val startText = if (startTime != null) timeFormatter.format(startTime) else booking.startTime
            val endText = if (endTime != null) timeFormatter.format(endTime) else booking.endTime

            "$dateText • $startText - $endText"
        } catch (e: Exception) {
            "${booking.date} • ${booking.startTime} - ${booking.endTime}"
        }

        holder.tvDate.text = dateLabel

        holder.btnAccept.setOnClickListener { onAccept(booking) }
        holder.btnDecline.setOnClickListener { onDecline(booking) }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
