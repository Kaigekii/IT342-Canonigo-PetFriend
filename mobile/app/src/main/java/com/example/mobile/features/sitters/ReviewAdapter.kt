package com.example.mobile.features.sitters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile.R

class ReviewAdapter(private var reviews: List<SitterReview>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReviewer: TextView = view.findViewById(R.id.tvReviewerName)
        val tvReviewMeta: TextView = view.findViewById(R.id.tvReviewMeta)
        val tvReviewComment: TextView = view.findViewById(R.id.tvReviewComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_card, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.tvReviewer.text = review.reviewerName
        holder.tvReviewMeta.text = "${review.rating} stars • ${review.date}"
        holder.tvReviewComment.text = review.comment
    }

    override fun getItemCount() = reviews.size

    fun updateData(newReviews: List<SitterReview>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}
