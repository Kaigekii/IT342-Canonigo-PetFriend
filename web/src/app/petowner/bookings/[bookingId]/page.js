"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter, useParams } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function buildImageUrl(url) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return `${API_BASE}${url}`;
  return url;
}

const styles = {
  page: { minHeight: "100vh", backgroundColor: "#FFF8F0", color: "#333333" },
  topBar: {
    height: 64,
    backgroundColor: "#FFF8F0",
    borderBottom: "1px solid #D3D3D3",
    display: "flex",
    alignItems: "center",
    padding: "0 16px",
    position: "sticky",
    top: 0,
    zIndex: 20,
  },
  brand: { fontSize: 22, fontWeight: 700, color: "#333333", whiteSpace: "nowrap" },
  nav: {
    display: "flex",
    alignItems: "center",
    gap: 24,
    marginLeft: 32,
    fontSize: 13,
    fontWeight: 600,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
  },
  navItem: { color: "#333333", opacity: 0.6, cursor: "pointer" },
  navItemActive: { color: "#333333", opacity: 1, borderBottom: "2px solid #FFD8B9", paddingBottom: 8, cursor: "pointer" },
  content: { maxWidth: 900, margin: "0 auto", padding: "28px 24px 36px" },
  title: { fontSize: 28, fontWeight: 800, marginBottom: 16 },
  card: {
    border: "1px solid #D3D3D3",
    borderRadius: 10,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 18,
  },
  row: { display: "grid", gridTemplateColumns: "160px 1fr", gap: 12, padding: "8px 0", borderBottom: "1px solid #E6E6E6" },
  rowLast: { borderBottom: "none" },
  label: { fontSize: 12, fontWeight: 700, letterSpacing: "0.08em", textTransform: "uppercase", color: "#777777" },
  value: { fontSize: 14, fontWeight: 600 },
  valueRow: { display: "flex", alignItems: "center", gap: 10 },
  sitterAvatar: {
    width: 36,
    height: 36,
    borderRadius: "50%",
    backgroundColor: "#D3D3D3",
    backgroundSize: "cover",
    backgroundPosition: "center",
    backgroundRepeat: "no-repeat",
    flexShrink: 0,
  },
  backLink: {
    border: "none",
    background: "transparent",
    color: "#333333",
    textDecoration: "underline",
    fontSize: 13,
    fontWeight: 700,
    cursor: "pointer",
    padding: 0,
    marginBottom: 12,
  },
  errorBox: {
    marginBottom: 12,
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: 14,
  },
  emptyBox: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 12,
    fontSize: 14,
    color: "#666666",
    fontWeight: 600,
  },
};

function formatDate(dateStr) {
  if (!dateStr) return "";
  const d = new Date(`${dateStr}T00:00:00`);
  if (Number.isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

function formatTime(value) {
  if (!value) return "";
  const parts = String(value).split(":");
  if (parts.length >= 2) return `${parts[0]}:${parts[1]}`;
  return String(value);
}

export default function BookingDetailsPage() {
  const router = useRouter();
  const params = useParams();
  const bookingId = params?.bookingId;

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }

    const load = async () => {
      setError("");
      setLoading(true);
      try {
        const res = await fetch(`${API_BASE}/api/bookings/${bookingId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.status === 401) {
          localStorage.removeItem("token");
          router.replace("/login");
          return;
        }
        if (!res.ok) throw new Error("Failed to load booking");
        const data = await res.json();
        setBooking(data);
      } catch (err) {
        setError(err?.message || "Something went wrong");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [router, token, bookingId]);

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petowner/dashboard")}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/pets")}>My Pets</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/find-sitter")}>Find Sitter</span>
          <span style={styles.navItemActive} onClick={() => router.push("/petowner/bookings")}>Bookings</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/messages")}>Messages</span>
        </nav>
      </header>

      <main style={styles.content}>
        <button type="button" style={styles.backLink} onClick={() => router.push("/petowner/bookings")}>
          Back to bookings
        </button>
        <h1 style={styles.title}>Booking Details</h1>

        {error && <div style={styles.errorBox}>{error}</div>}

        {loading ? (
          <div style={styles.emptyBox}>Loading booking...</div>
        ) : !booking ? (
          <div style={styles.emptyBox}>Booking not found.</div>
        ) : (
          <div style={styles.card}>
            <div style={styles.row}>
              <div style={styles.label}>Booking ID</div>
              <div style={styles.value}>{booking.bookingId}</div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Sitter</div>
              <div style={styles.valueRow}>
                <div
                  style={{
                    ...styles.sitterAvatar,
                    backgroundImage: booking.sitterProfilePhotoUrl
                      ? `url(${buildImageUrl(booking.sitterProfilePhotoUrl)})`
                      : undefined,
                  }}
                />
                <span style={styles.value}>{booking.sitterName || "Pending"}</span>
              </div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Pet</div>
              <div style={styles.value}>{booking.petNames?.join(", ") || "Pet"}</div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Service</div>
              <div style={styles.value}>{booking.serviceType}</div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Date/Time</div>
              <div style={styles.value}>{formatDate(booking.date)} {formatTime(booking.startTime)}-{formatTime(booking.endTime)}</div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Amount</div>
              <div style={styles.value}>{booking.totalAmount} {booking.currency || "PHP"}</div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Status</div>
              <div style={styles.value}>{booking.status}</div>
            </div>
            <div style={styles.row}>
              <div style={styles.label}>Payment ID</div>
              <div style={styles.value}>{booking.paymentId || "-"}</div>
            </div>
            <div style={{ ...styles.row, ...styles.rowLast }}>
              <div style={styles.label}>Payment Status</div>
              <div style={styles.value}>{booking.paymentStatus || "PENDING"}</div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
