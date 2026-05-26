"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function PaymentCancelPage() {
  const router = useRouter();
  const [returnUrl, setReturnUrl] = useState("/petowner/bookings");

  useEffect(() => {
    if (typeof window === "undefined") return;
    const stored = localStorage.getItem("paymongoReturnUrl");
    if (stored) {
      setReturnUrl(stored);
    }
    const timer = setTimeout(() => {
      router.replace(stored || "/petowner/bookings");
    }, 2500);
    return () => clearTimeout(timer);
  }, [router]);

  return (
    <div style={{ minHeight: "100vh", display: "grid", placeItems: "center", background: "#FFF8F0", color: "#333333" }}>
      <div style={{ maxWidth: 520, textAlign: "center", padding: 24 }}>
        <h1 style={{ fontSize: 28, marginBottom: 12 }}>Payment Cancelled</h1>
        <p style={{ fontSize: 14, marginBottom: 18 }}>Your booking is still pending. You can try payment again.</p>
        <button
          type="button"
          onClick={() => router.replace(returnUrl)}
          style={{ border: "none", background: "#1E1E1E", color: "#FFFFFF", padding: "10px 16px", borderRadius: 6, cursor: "pointer" }}
        >
          Back to booking
        </button>
      </div>
    </div>
  );
}
