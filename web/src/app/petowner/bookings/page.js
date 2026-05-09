"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

const STATUS_TABS = [
  { key: "ALL", label: "All" },
  { key: "UPCOMING", label: "Upcoming" },
  { key: "COMPLETED", label: "Completed" },
  { key: "CANCELLED", label: "Cancelled" },
];

const styles = {
  page: {
    minHeight: "100vh",
    backgroundColor: "#FFF8F0",
    color: "#333333",
  },
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
  brand: {
    fontSize: 22,
    fontWeight: 700,
    letterSpacing: "0.02em",
    color: "#333333",
    whiteSpace: "nowrap",
  },
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
  navItem: {
    color: "#333333",
    opacity: 0.6,
    cursor: "pointer",
  },
  navItemActive: {
    color: "#333333",
    opacity: 1,
    borderBottom: "2px solid #FFD8B9",
    paddingBottom: 8,
    cursor: "pointer",
  },
  topRightWrap: {
    marginLeft: "auto",
    position: "relative",
    display: "flex",
    alignItems: "center",
    gap: 14,
  },
  topRightRole: {
    fontSize: 12,
    fontWeight: 700,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    color: "#333333",
    opacity: 0.65,
  },
  avatarButton: {
    width: 34,
    height: 34,
    borderRadius: "50%",
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFD8B9",
    cursor: "pointer",
  },
  profileMenu: {
    position: "absolute",
    top: 44,
    right: 0,
    minWidth: 160,
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 12,
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    overflow: "hidden",
  },
  menuItem: {
    width: "100%",
    textAlign: "left",
    border: "none",
    backgroundColor: "transparent",
    padding: "12px 14px",
    fontSize: 13,
    fontWeight: 600,
    color: "#333333",
    cursor: "pointer",
  },
  menuItemDanger: {
    width: "100%",
    textAlign: "left",
    border: "none",
    backgroundColor: "transparent",
    padding: "12px 14px",
    fontSize: 13,
    fontWeight: 700,
    color: "#D8705D",
    cursor: "pointer",
    borderTop: "1px solid #D3D3D3",
  },
  content: {
    maxWidth: 1080,
    margin: "0 auto",
    padding: "28px 24px 36px",
  },
  title: {
    fontSize: 28,
    lineHeight: 1.1,
    fontWeight: 800,
    marginBottom: 16,
  },
  tabs: {
    display: "flex",
    alignItems: "center",
    gap: 10,
    marginBottom: 20,
    flexWrap: "wrap",
  },
  tabButton: {
    height: 34,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.06em",
    textTransform: "uppercase",
    padding: "0 14px",
    cursor: "pointer",
  },
  tabButtonActive: {
    height: 34,
    borderRadius: 8,
    border: "1px solid #1E1E1E",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    letterSpacing: "0.06em",
    textTransform: "uppercase",
    padding: "0 14px",
    cursor: "pointer",
  },
  table: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    overflow: "hidden",
  },
  tableHeader: {
    display: "grid",
    gridTemplateColumns: "120px 180px 120px 120px 160px 110px 120px 80px",
    padding: "12px 16px",
    backgroundColor: "#F2F2F2",
    borderBottom: "1px solid #D3D3D3",
    fontSize: 12,
    fontWeight: 800,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    color: "#777777",
  },
  tableRow: {
    display: "grid",
    gridTemplateColumns: "120px 180px 120px 120px 160px 110px 120px 80px",
    padding: "14px 16px",
    alignItems: "center",
    borderBottom: "1px solid #D3D3D3",
    fontSize: 13,
  },
  tableRowLast: {
    borderBottom: "none",
  },
  cellStrong: {
    fontWeight: 700,
  },
  dateCell: {
    display: "flex",
    flexDirection: "column",
    gap: 4,
    color: "#5D5D5D",
  },
  amount: {
    fontWeight: 800,
  },
  statusBadge: {
    fontSize: 11,
    fontWeight: 800,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    padding: "6px 10px",
    borderRadius: 999,
    border: "1px solid #D3D3D3",
    textAlign: "center",
    width: "fit-content",
  },
  statusPending: {
    backgroundColor: "#FFF9C4",
    borderColor: "#FFF9C4",
  },
  statusConfirmed: {
    backgroundColor: "#B6E5D8",
    borderColor: "#B6E5D8",
  },
  statusCompleted: {
    backgroundColor: "#D3D3D3",
    borderColor: "#D3D3D3",
  },
  statusCancelled: {
    backgroundColor: "#FFCCBC",
    borderColor: "#FFCCBC",
  },
  actionLink: {
    border: "none",
    backgroundColor: "transparent",
    color: "#333333",
    textDecoration: "underline",
    fontSize: 13,
    fontWeight: 700,
    cursor: "pointer",
    padding: 0,
  },
  actionMuted: {
    color: "#B0B0B0",
    textDecoration: "none",
    cursor: "default",
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
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: 14,
    marginBottom: 12,
  },
  successBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#B6E5D8",
    border: "2px solid #B6E5D8",
    color: "#333333",
    fontSize: 14,
    marginBottom: 12,
  },
};

function formatTime(value) {
  if (!value) return "";
  const parts = String(value).split(":");
  if (parts.length >= 2) return `${parts[0]}:${parts[1]}`;
  return String(value);
}

function formatDate(dateStr) {
  if (!dateStr) return "";
  const d = new Date(`${dateStr}T00:00:00`);
  if (Number.isNaN(d.getTime())) return dateStr;
  return d.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

function formatMoney(amount, currency) {
  if (amount === null || amount === undefined) return "P0.00";
  const num = Number(amount);
  if (!Number.isFinite(num)) return "P0.00";
  if (currency && currency !== "PHP") return `${currency} ${num.toFixed(2)}`;
  return `P${num.toFixed(0)}`;
}

function getStatusStyle(status) {
  if (status === "PENDING") return { ...styles.statusBadge, ...styles.statusPending };
  if (status === "CONFIRMED") return { ...styles.statusBadge, ...styles.statusConfirmed };
  if (status === "COMPLETED") return { ...styles.statusBadge, ...styles.statusCompleted };
  if (status === "CANCELLED") return { ...styles.statusBadge, ...styles.statusCancelled };
  return styles.statusBadge;
}

function isUpcoming(status) {
  return status === "PENDING" || status === "CONFIRMED";
}

export default function PetOwnerBookingsPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [bookings, setBookings] = useState([]);
  const [reviewedBookingIds, setReviewedBookingIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [activeTab, setActiveTab] = useState("ALL");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const loadData = useCallback(async (authToken) => {
    const headers = { Authorization: `Bearer ${authToken}` };

    const meRes = await fetch(`${API_BASE}/api/user/me`, { headers });
    if (meRes.status === 401) {
      localStorage.removeItem("token");
      router.replace("/login");
      return;
    }
    if (!meRes.ok) throw new Error("Failed to load profile");

    const me = await meRes.json();
    if (me.role !== "PET_OWNER") {
      router.replace("/dashboard");
      return;
    }

    const [bookingsRes, reviewedRes] = await Promise.all([
      fetch(`${API_BASE}/api/bookings`, { headers }),
      fetch(`${API_BASE}/api/reviews/me/reviewed-bookings`, { headers }),
    ]);

    if (!bookingsRes.ok) throw new Error("Failed to load bookings");
    if (!reviewedRes.ok) throw new Error("Failed to load review stats");

    const [bookingsData, reviewedData] = await Promise.all([
      bookingsRes.json(),
      reviewedRes.json(),
    ]);

    setBookings(Array.isArray(bookingsData) ? bookingsData : []);
    setReviewedBookingIds(Array.isArray(reviewedData) ? reviewedData : []);
  }, [router]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }

    const run = async () => {
      setError("");
      setLoading(true);
      try {
        await loadData(token);
      } catch (e) {
        setError(e?.message || "Something went wrong");
      } finally {
        setLoading(false);
      }
    };

    run();
  }, [router, token, loadData]);

  useEffect(() => {
    const onClick = (event) => {
      if (!menuRef.current) return;
      if (!menuRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
    };

    document.addEventListener("mousedown", onClick);
    return () => document.removeEventListener("mousedown", onClick);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.replace("/login");
  };

  const filteredBookings = useMemo(() => {
    if (activeTab === "ALL") return bookings;
    if (activeTab === "UPCOMING") return bookings.filter((booking) => isUpcoming(booking.status));
    if (activeTab === "COMPLETED") return bookings.filter((booking) => booking.status === "COMPLETED");
    if (activeTab === "CANCELLED") return bookings.filter((booking) => booking.status === "CANCELLED");
    return bookings;
  }, [bookings, activeTab]);

  const getTabCount = useCallback((key) => {
    if (key === "ALL") return bookings.length;
    if (key === "UPCOMING") return bookings.filter((booking) => isUpcoming(booking.status)).length;
    if (key === "COMPLETED") return bookings.filter((booking) => booking.status === "COMPLETED").length;
    if (key === "CANCELLED") return bookings.filter((booking) => booking.status === "CANCELLED").length;
    return 0;
  }, [bookings]);

  const handleCancel = async (bookingId) => {
    if (!token) {
      router.replace("/login");
      return;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const res = await fetch(`${API_BASE}/api/bookings/${bookingId}/owner-status`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ status: "CANCELLED" }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to cancel booking");
      }

      await loadData(token);
      setSuccess("Booking cancelled");
    } catch (e) {
      setError(e?.message || "Failed to cancel booking");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petowner/dashboard")}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/pets")}>My Pets</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/find-sitter")}>Find Sitter</span>
          <span style={styles.navItemActive}>Bookings</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/messages")}>Messages</span>
        </nav>

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Pet Owner</span>
          <button
            type="button"
            style={styles.avatarButton}
            aria-label="Open profile menu"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          />

          {showProfileMenu && (
            <div style={styles.profileMenu}>
              <button
                type="button"
                style={styles.menuItem}
                onClick={() => {
                  setShowProfileMenu(false);
                  router.push("/dashboard");
                }}
              >
                Profile
              </button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>My Bookings</h1>

        {error && <div style={styles.errorBox}>{error}</div>}
        {success && <div style={styles.successBox}>{success}</div>}

        <div style={styles.tabs} role="tablist" aria-label="Booking status">
          {STATUS_TABS.map((tab) => (
            <button
              key={tab.key}
              type="button"
              style={activeTab === tab.key ? styles.tabButtonActive : styles.tabButton}
              onClick={() => setActiveTab(tab.key)}
              role="tab"
              aria-selected={activeTab === tab.key}
            >
              {tab.label} ({getTabCount(tab.key)})
            </button>
          ))}
        </div>

        {loading ? (
          <div style={styles.emptyBox}>Loading bookings...</div>
        ) : filteredBookings.length === 0 ? (
          <div style={styles.emptyBox}>No bookings to show.</div>
        ) : (
          <div style={styles.table}>
            <div style={styles.tableHeader}>
              <div>ID</div>
              <div>Sitter</div>
              <div>Pet</div>
              <div>Service</div>
              <div>Date/Time</div>
              <div>Amount</div>
              <div>Status</div>
              <div />
            </div>
            {filteredBookings.map((booking, index) => {
              const isLast = index === filteredBookings.length - 1;
              const showReview = booking.status === "COMPLETED" && !reviewedBookingIds.includes(booking.bookingId);
              const showCancel = booking.status === "CONFIRMED" || booking.status === "PENDING";

              return (
                <div
                  key={booking.bookingId}
                  style={{
                    ...styles.tableRow,
                    ...(isLast ? styles.tableRowLast : null),
                  }}
                >
                  <div>{booking.bookingId ? `bk_${String(booking.bookingId).slice(0, 5)}` : "-"}</div>
                  <div style={styles.cellStrong}>{booking.sitterName || "Pending Sitter"}</div>
                  <div>{booking.petNames?.[0] || "Pet"}</div>
                  <div>{String(booking.serviceType || "SERVICE").toLowerCase()}</div>
                  <div style={styles.dateCell}>
                    <span>{formatDate(booking.date)}</span>
                    <span>{formatTime(booking.startTime)}-{formatTime(booking.endTime)}</span>
                  </div>
                  <div style={styles.amount}>{formatMoney(booking.totalAmount, booking.currency)}</div>
                  <div>
                    <span style={getStatusStyle(booking.status)}>{booking.status || ""}</span>
                  </div>
                  <div>
                    {showCancel && (
                      <button type="button" style={styles.actionLink} onClick={() => handleCancel(booking.bookingId)} disabled={saving}>
                        Cancel
                      </button>
                    )}
                    {showReview && (
                      <button
                        type="button"
                        style={styles.actionLink}
                        onClick={() => router.push(`/petowner/find-sitter/${booking.sitterId}`)}
                      >
                        Review
                      </button>
                    )}
                    {!showCancel && !showReview && (
                      <span style={{ ...styles.actionLink, ...styles.actionMuted }}>-</span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </main>
    </div>
  );
}
