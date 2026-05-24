package com.example.mobile.features.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import java.text.SimpleDateFormat
import java.util.Locale

class SitterScheduleAdapter(private var bookings: List<Booking>) : RecyclerView.Adapter<SitterScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOwner: TextView = view.findViewById(R.id.tvScheduleOwner)
        val tvService: TextView = view.findViewById(R.id.tvScheduleService)
        val tvDate: TextView = view.findViewById(R.id.tvScheduleDate)
        val tvAmount: TextView = view.findViewById(R.id.tvScheduleAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sitter_schedule_card, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvOwner.text = booking.ownerName
        holder.tvService.text = booking.serviceType

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
        val amount = booking.totalAmount?.let { "PHP ${String.format("%.2f", it)}" } ?: "PHP 0.00"
        holder.tvAmount.text = amount
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
