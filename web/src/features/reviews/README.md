# Reviews Feature - Frontend Documentation

## Overview
The Reviews feature allows pet owners to rate and review completed bookings with pet sitters. Reviews help build sitter reputation and help other pet owners make decisions.

## Directory Structure
```
features/reviews/
├── api.js           # API client functions
├── hooks/
│   └── useReviews.js    # Review operations
├── components/
│   ├── ReviewForm.js
│   ├── ReviewList.js
│   ├── ReviewCard.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { reviewsApi } from "@/features/reviews/api";
```

### Methods

#### `reviewsApi.submitReview(bookingId, rating, comment)`
Submit review for a completed booking.
```javascript
const review = await reviewsApi.submitReview(
  "booking-uuid",
  5,
  "Excellent service! My dog had a great time."
);
```

#### `reviewsApi.getSitterReviews(sitterId)`
Get all reviews for a sitter.
```javascript
const reviews = await reviewsApi.getSitterReviews(sitterId);
// Returns: [{ reviewId, bookingId, sitterId, reviewerName, rating, comment, createdAt }, ...]
```

#### `reviewsApi.getSitterReviewSummary(sitterId)`
Get rating summary for a sitter.
```javascript
const summary = await reviewsApi.getSitterReviewSummary(sitterId);
// Returns: { averageRating: 4.5, reviewCount: 12 }
```

#### `reviewsApi.getReviewedBookings()`
Get bookings already reviewed by current user.
```javascript
const reviewedIds = await reviewsApi.getReviewedBookings();
// Returns: ["booking-1", "booking-2", "booking-3"]
```

## Hooks

### useReviews Hook
```javascript
import { useReviews } from "@/features/reviews/hooks/useReviews";

const {
  reviews,
  reviewSummary,
  loading,
  error,
  getSitterReviews,
  getSitterReviewSummary,
  submitReview
} = useReviews();
```

## Rating Values

Ratings must be between 1 and 5.

```javascript
import { RATING_MIN, RATING_MAX } from "@/shared/constants/statuses";

console.log(RATING_MIN); // 1
console.log(RATING_MAX); // 5
```

## Review Data Structure
```javascript
{
  reviewId: "uuid",
  bookingId: "uuid",
  sitterId: "uuid",
  reviewerId: "uuid",
  reviewerName: "John Smith",
  rating: 5,
  comment: "Excellent service! My dog had a great time.",
  createdAt: "2024-03-20T14:30:00Z"
}
```

## Example Component: ReviewForm

```javascript
"use client";

import { useState } from "react";
import { useReviews } from "@/features/reviews/hooks/useReviews";
import { RATING_MIN, RATING_MAX } from "@/shared/constants/statuses";
import { ErrorBanner, SuccessBanner } from "@/shared/components/Banners";

export default function ReviewForm({ bookingId }) {
  const { submitReview, loading, error } = useReviews();
  const [success, setSuccess] = useState(false);
  const [formData, setFormData] = useState({
    rating: 5,
    comment: ""
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await submitReview(bookingId, formData.rating, formData.comment);
      setSuccess(true);
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      console.error("Review error:", err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <ErrorBanner message={error} />}
      {success && <SuccessBanner message="Review submitted! Thank you!" />}

      <div>
        <label>Rating (1-5 stars)</label>
        <div style={{ display: "flex", gap: "8px" }}>
          {[1, 2, 3, 4, 5].map(star => (
            <button
              key={star}
              type="button"
              onClick={() => setFormData({...formData, rating: star})}
              style={{
                fontSize: "24px",
                background: "none",
                border: "none",
                cursor: "pointer",
                opacity: formData.rating >= star ? 1 : 0.3
              }}
            >
              ⭐
            </button>
          ))}
        </div>
      </div>

      <div>
        <label>Comment</label>
        <textarea
          value={formData.comment}
          onChange={(e) => setFormData({...formData, comment: e.target.value})}
          placeholder="Share your experience..."
          minLength={5}
          maxLength={2000}
          required
          rows="4"
        />
      </div>

      <button type="submit" disabled={loading}>
        {loading ? "Submitting..." : "Submit Review"}
      </button>
    </form>
  );
}
```

## Example Component: ReviewList

```javascript
"use client";

import { useEffect } from "react";
import { useReviews } from "@/features/reviews/hooks/useReviews";
import { formatDateTime, formatRating } from "@/shared/utils/formatting";

export default function ReviewsList({ sitterId }) {
  const { reviews, reviewSummary, loading, getSitterReviews, getSitterReviewSummary } = useReviews();

  useEffect(() => {
    getSitterReviews(sitterId);
    getSitterReviewSummary(sitterId);
  }, [sitterId]);

  return (
    <div>
      {reviewSummary && (
        <div style={{ marginBottom: "24px" }}>
          <h3>Rating: {formatRating(reviewSummary.averageRating)}</h3>
          <p>({reviewSummary.reviewCount} reviews)</p>
        </div>
      )}

      <h3>Recent Reviews</h3>
      {loading ? (
        <p>Loading reviews...</p>
      ) : (
        reviews.map(review => (
          <div key={review.reviewId} style={{ borderBottom: "1px solid #DDD", padding: "12px 0" }}>
            <p style={{ fontWeight: "bold" }}>
              {review.reviewerName} - {'⭐'.repeat(review.rating)}
            </p>
            <p>{review.comment}</p>
            <p style={{ fontSize: "12px", color: "#999" }}>
              {formatDateTime(review.createdAt)}
            </p>
          </div>
        ))
      )}
    </div>
  );
}
```

## Related Features
- [Bookings Feature](../booking/README.md)
- [Sitters Feature](../sitters/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
