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
    private val onDecline: (Booking) -> Unit,
    private val onComplete: (Booking) -> Unit
) : RecyclerView.Adapter<SitterRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOwner: TextView = view.findViewById(R.id.tvRequestOwner)
        val tvService: TextView = view.findViewById(R.id.tvRequestService)
        val tvDate: TextView = view.findViewById(R.id.tvRequestDate)
        val tvPets: TextView = view.findViewById(R.id.tvRequestPets)
        val tvAmount: TextView = view.findViewById(R.id.tvRequestAmount)
        val tvStatus: TextView = view.findViewById(R.id.tvRequestStatus)
        val btnAccept: MaterialButton = view.findViewById(R.id.btnAcceptRequest)
        val btnDecline: MaterialButton = view.findViewById(R.id.btnDeclineRequest)
        val btnComplete: MaterialButton = view.findViewById(R.id.btnCompleteRequest)
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
        val amount = booking.totalAmount?.let { "PHP ${String.format("%.2f", it)}" } ?: "PHP 0.00"
        holder.tvAmount.text = amount

        val status = booking.status
        holder.tvStatus.text = status
        val statusBg = when (status) {
            "CONFIRMED" -> R.drawable.bg_status_confirmed
            "COMPLETED" -> R.drawable.bg_status_completed
            else -> R.drawable.bg_status_pending
        }
        holder.tvStatus.setBackgroundResource(statusBg)

        holder.btnAccept.visibility = if (status == "PENDING") View.VISIBLE else View.GONE
        holder.btnDecline.visibility = if (status == "PENDING") View.VISIBLE else View.GONE
        holder.btnComplete.visibility = if (status == "CONFIRMED") View.VISIBLE else View.GONE

        holder.btnAccept.setOnClickListener { onAccept(booking) }
        holder.btnDecline.setOnClickListener { onDecline(booking) }
        holder.btnComplete.setOnClickListener { onComplete(booking) }
    }

    override fun getItemCount() = bookings.size

    fun updateData(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }
}
