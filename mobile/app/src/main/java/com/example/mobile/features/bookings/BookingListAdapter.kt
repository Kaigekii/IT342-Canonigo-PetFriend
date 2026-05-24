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

class BookingListAdapter(
    private var bookings: List<Booking>,
    private val onCancel: (Booking) -> Unit
) : RecyclerView.Adapter<BookingListAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvService: TextView = view.findViewById(R.id.tvBookingService)
        val tvStatus: TextView = view.findViewById(R.id.tvBookingStatus)
        val tvDate: TextView = view.findViewById(R.id.tvBookingDate)
        val tvSitter: TextView = view.findViewById(R.id.tvBookingSitter)
        val tvPets: TextView = view.findViewById(R.id.tvBookingPets)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancelBooking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_list_card, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvService.text = booking.serviceType
        holder.tvStatus.text = booking.status
        holder.tvSitter.text = booking.sitterName ?: "Sitter TBD"
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

        val canCancel = booking.status == "PENDING" || booking.status == "CONFIRMED"
        holder.btnCancel.visibility = if (canCancel) View.VISIBLE else View.GONE
        holder.btnCancel.setOnClickListener { onCancel(booking) }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
