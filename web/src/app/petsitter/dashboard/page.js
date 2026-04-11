"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

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
    maxWidth: 980,
    margin: "0 auto",
    padding: "28px 24px 36px",
  },
  title: {
    fontSize: 38,
    lineHeight: 1.1,
    fontWeight: 800,
    marginBottom: 10,
  },
  helperRow: {
    display: "flex",
    alignItems: "center",
    gap: 10,
    marginBottom: 18,
    flexWrap: "wrap",
  },
  verifiedBadge: {
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    backgroundColor: "#FFF8F0",
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.06em",
    padding: "4px 10px",
    textTransform: "uppercase",
  },
  ratingText: {
    fontSize: 14,
    color: "#777777",
    fontWeight: 600,
  },
  statsGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(4, minmax(0, 1fr))",
    gap: 14,
    marginBottom: 24,
  },
  statCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    minHeight: 90,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
  },
  statValue: {
    fontSize: 40,
    fontWeight: 800,
    lineHeight: 1,
    marginBottom: 8,
  },
  statLabel: {
    fontSize: 12,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    color: "#777777",
    fontWeight: 700,
  },
  sectionTitle: {
    fontSize: 27,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.02em",
    marginBottom: 10,
  },
  list: {
    display: "grid",
    gap: 12,
    marginBottom: 24,
  },
  rowCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 12,
    display: "grid",
    gridTemplateColumns: "44px 1fr auto",
    alignItems: "center",
    gap: 10,
  },
  rowAvatar: {
    width: 36,
    height: 36,
    borderRadius: "50%",
    backgroundColor: "#FFB6C1",
  },
  rowName: {
    fontSize: 22,
    lineHeight: 1,
    fontWeight: 700,
    marginBottom: 6,
  },
  rowMeta: {
    fontSize: 14,
    color: "#777777",
    fontWeight: 600,
  },
  rowActions: {
    display: "flex",
    alignItems: "center",
    gap: 8,
  },
  amount: {
    fontSize: 28,
    fontWeight: 800,
    lineHeight: 1,
    marginRight: 4,
  },
  buttonDark: {
    height: 32,
    borderRadius: 8,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    padding: "0 12px",
    cursor: "pointer",
  },
  buttonLight: {
    height: 32,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    padding: "0 12px",
    cursor: "pointer",
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
  return `P${num.toFixed(2)}`;
}

export default function PetSitterDashboardPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [user, setUser] = useState(null);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [upcomingSessions, setUpcomingSessions] = useState([]);
  const [todaySchedule, setTodaySchedule] = useState([]);
  const [allBookings, setAllBookings] = useState([]);
  const [reviewSummary, setReviewSummary] = useState({ averageRating: 0, reviewCount: 0 });

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
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
    if (me.role !== "PET_SITTER") {
      router.replace("/dashboard");
      return;
    }

    setUser(me);

    const [pendingRes, upcomingRes, todayRes, allRes, reviewSummaryRes] = await Promise.all([
      fetch(`${API_BASE}/api/bookings/sitter/pending`, { headers }),
      fetch(`${API_BASE}/api/bookings/sitter/upcoming`, { headers }),
      fetch(`${API_BASE}/api/bookings/sitter/today`, { headers }),
      fetch(`${API_BASE}/api/bookings/sitter`, { headers }),
      fetch(`${API_BASE}/api/reviews/sitter/${me.userId}/summary`, { headers }),
    ]);

    if (!pendingRes.ok || !upcomingRes.ok || !todayRes.ok || !allRes.ok || !reviewSummaryRes.ok) {
      throw new Error("Failed to load sitter bookings");
    }

    const [pendingData, upcomingData, todayData, allData, reviewSummaryData] = await Promise.all([
      pendingRes.json(),
      upcomingRes.json(),
      todayRes.json(),
      allRes.json(),
      reviewSummaryRes.json(),
    ]);

    setPendingRequests(Array.isArray(pendingData) ? pendingData : []);
    setUpcomingSessions(Array.isArray(upcomingData) ? upcomingData : []);
    setTodaySchedule(Array.isArray(todayData) ? todayData : []);
    setAllBookings(Array.isArray(allData) ? allData : []);
    setReviewSummary({
      averageRating: Number(reviewSummaryData?.averageRating ?? 0),
      reviewCount: Number(reviewSummaryData?.reviewCount ?? 0),
    });
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

  const updateStatus = async (bookingId, status) => {
    if (!token) {
      router.replace("/login");
      return;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const res = await fetch(`${API_BASE}/api/bookings/${bookingId}/sitter-status`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ status }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to update booking status");
      }

      await loadData(token);
      if (status === "CONFIRMED") setSuccess("Request accepted");
      if (status === "CANCELLED") setSuccess("Request declined");
      if (status === "COMPLETED") setSuccess("Session marked as complete");
    } catch (e) {
      setError(e?.message || "Failed to update booking status");
    } finally {
      setSaving(false);
    }
  };

  const completedCount = allBookings.filter((b) => b.status === "COMPLETED").length;
  const totalEarned = allBookings
    .filter((b) => b.status === "COMPLETED")
    .reduce((sum, b) => sum + (Number(b.totalAmount) || 0), 0);

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItemActive}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/profile")}>My Profile</span>
          <span style={styles.navItem}>Requests</span>
          <span style={styles.navItem}>Messages</span>
        </nav>

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Pet Sitter</span>
          <button
            type="button"
            style={styles.avatarButton}
            aria-label="Open profile menu"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          />

          {showProfileMenu && (
            <div style={styles.profileMenu}>
              <button type="button" style={styles.menuItem} onClick={() => router.push("/petsitter/profile")}>Profile</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>Welcome, {user?.firstName || "Pet Sitter"}</h1>
        <div style={styles.helperRow}>
          <span style={styles.verifiedBadge}>{user?.isVerified ? "Verified" : "Pending Verification"}</span>
          <span style={styles.ratingText}>⭐ {reviewSummary.averageRating.toFixed(1)} ({reviewSummary.reviewCount} reviews)</span>
        </div>

        {error && <div style={styles.errorBox}>{error}</div>}
        {success && <div style={styles.successBox}>{success}</div>}

        <section style={styles.statsGrid} aria-label="Sitter metrics">
          <div style={styles.statCard}>
            <div style={styles.statValue}>{pendingRequests.length}</div>
            <div style={styles.statLabel}>Pending Requests</div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statValue}>{upcomingSessions.length}</div>
            <div style={styles.statLabel}>Upcoming Sessions</div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statValue}>{completedCount}</div>
            <div style={styles.statLabel}>Completed</div>
          </div>
          <div style={styles.statCard}>
            <div style={styles.statValue}>{formatMoney(totalEarned, "PHP")}</div>
            <div style={styles.statLabel}>Total Earned</div>
          </div>
        </section>

        <section aria-label="Pending Requests">
          <h2 style={styles.sectionTitle}>Pending Requests</h2>
          <div style={styles.list}>
            {loading ? (
              <div style={styles.emptyBox}>Loading requests...</div>
            ) : pendingRequests.length === 0 ? (
              <div style={styles.emptyBox}>No pending requests.</div>
            ) : (
              pendingRequests.map((request) => (
                <div key={request.bookingId} style={styles.rowCard}>
                  <div style={styles.rowAvatar} />
                  <div>
                    <div style={styles.rowName}>{request.ownerName || "Pet Owner"}</div>
                    <div style={styles.rowMeta}>
                      {(request.petNames?.[0] || "Pet")} . {String(request.serviceType || "SERVICE").toLowerCase()} . {formatDate(request.date)} . {formatTime(request.startTime)}-{formatTime(request.endTime)}
                    </div>
                  </div>
                  <div style={styles.rowActions}>
                    <span style={styles.amount}>{formatMoney(request.totalAmount, request.currency)}</span>
                    <button type="button" style={styles.buttonDark} onClick={() => updateStatus(request.bookingId, "CONFIRMED")} disabled={saving}>
                      Accept
                    </button>
                    <button type="button" style={styles.buttonLight} onClick={() => updateStatus(request.bookingId, "CANCELLED")} disabled={saving}>
                      Decline
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>

        <section aria-label="Today's Schedule">
          <h2 style={styles.sectionTitle}>Today&apos;s Schedule</h2>
          <div style={styles.list}>
            {loading ? (
              <div style={styles.emptyBox}>Loading schedule...</div>
            ) : todaySchedule.length === 0 ? (
              <div style={styles.emptyBox}>No sessions for today.</div>
            ) : (
              todaySchedule.map((session) => (
                <div key={session.bookingId} style={styles.rowCard}>
                  <div style={styles.rowAvatar} />
                  <div>
                    <div style={styles.rowName}>{session.ownerName || "Pet Owner"} - {session.petNames?.[0] || "Pet"}</div>
                    <div style={styles.rowMeta}>
                      {String(session.serviceType || "SERVICE").toLowerCase()} . {formatTime(session.startTime)}-{formatTime(session.endTime)}
                    </div>
                  </div>
                  <div style={styles.rowActions}>
                    <button type="button" style={styles.buttonDark} onClick={() => updateStatus(session.bookingId, "COMPLETED")} disabled={saving}>
                      Mark Complete
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>
      </main>
    </div>
  );
}
