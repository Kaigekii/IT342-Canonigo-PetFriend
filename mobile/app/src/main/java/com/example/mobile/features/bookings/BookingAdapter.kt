package com.example.mobile.features.bookings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class BookingAdapter(private var bookings: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvBookingService: TextView = view.findViewById(R.id.tvBookingService)
        val tvBookingStatus: TextView = view.findViewById(R.id.tvBookingStatus)
        val tvBookingDate: TextView = view.findViewById(R.id.tvBookingDate)
        val tvBookingSitter: TextView = view.findViewById(R.id.tvBookingSitter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_card, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.tvBookingService.text = booking.serviceType
        holder.tvBookingStatus.text = booking.status
        holder.tvBookingSitter.text = "Sitter: ${booking.sitterFirstName} ${booking.sitterLastName}"

        // Format dates if they are standard ISO-8601 strings
        val dateStr = try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            
            val start = parser.parse(booking.startDate)
            val end = parser.parse(booking.endDate)
            
            if (start != null && end != null) {
                "${formatter.format(start)} - ${formatter.format(end)}"
            } else {
                "Invalid dates"
            }
        } catch (e: Exception) {
            "${booking.startDate} - ${booking.endDate}" // Fallback to raw strings
        }
        
        holder.tvBookingDate.text = dateStr
    }

    override fun getItemCount() = bookings.size
    
    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
