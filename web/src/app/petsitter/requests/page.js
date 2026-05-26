"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function buildImageUrl(url) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return `${API_BASE}${url}`;
  return url;
}

const STATUS_TABS = [
  { key: "ALL", label: "All" },
  { key: "PENDING", label: "Pending" },
  { key: "CONFIRMED", label: "Confirmed" },
  { key: "COMPLETED", label: "Completed" },
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
  list: {
    display: "grid",
    gap: 16,
  },
  rowCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 10,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 16,
    display: "grid",
    gridTemplateColumns: "48px 1fr auto",
    alignItems: "center",
    gap: 12,
  },
  rowAvatar: {
    width: 44,
    height: 44,
    borderRadius: "50%",
    backgroundColor: "#D3D3D3",
    backgroundSize: "cover",
    backgroundPosition: "center",
    backgroundRepeat: "no-repeat",
  },
  rowName: {
    fontSize: 18,
    lineHeight: 1.1,
    fontWeight: 700,
    marginBottom: 6,
  },
  rowMeta: {
    fontSize: 13,
    color: "#777777",
    fontWeight: 600,
  },
  rowMetaSecondary: {
    fontSize: 12,
    color: "#999999",
    marginTop: 4,
  },
  rowActions: {
    display: "flex",
    alignItems: "center",
    gap: 10,
    flexWrap: "wrap",
    justifyContent: "flex-end",
  },
  amount: {
    fontSize: 18,
    fontWeight: 800,
    lineHeight: 1,
  },
  statusBadge: {
    fontSize: 11,
    fontWeight: 800,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    padding: "6px 10px",
    borderRadius: 999,
    border: "1px solid #D3D3D3",
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
  buttonDark: {
    height: 34,
    borderRadius: 8,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    padding: "0 14px",
    cursor: "pointer",
  },
  buttonLight: {
    height: 34,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    padding: "0 14px",
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

function getStatusLabel(status) {
  if (!status) return "UNKNOWN";
  return String(status).toUpperCase();
}

function getStatusStyle(status) {
  if (status === "PENDING") return { ...styles.statusBadge, ...styles.statusPending };
  if (status === "CONFIRMED") return { ...styles.statusBadge, ...styles.statusConfirmed };
  if (status === "COMPLETED") return { ...styles.statusBadge, ...styles.statusCompleted };
  return styles.statusBadge;
}

export default function PetSitterRequestsPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [user, setUser] = useState(null);
  const [requests, setRequests] = useState([]);
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
    if (me.role !== "PET_SITTER") {
      router.replace("/dashboard");
      return;
    }

    setUser(me);

    const bookingsRes = await fetch(`${API_BASE}/api/bookings/sitter`, { headers });
    if (!bookingsRes.ok) throw new Error("Failed to load sitter bookings");

    const bookingsData = await bookingsRes.json();
    setRequests(Array.isArray(bookingsData) ? bookingsData : []);
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

  const filteredRequests = useMemo(() => {
    if (activeTab === "ALL") return requests;
    return requests.filter((request) => request.status === activeTab);
  }, [requests, activeTab]);

  const getTabCount = useCallback((key) => {
    if (key === "ALL") return requests.length;
    return requests.filter((request) => request.status === key).length;
  }, [requests]);

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petsitter/dashboard")}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/profile")}>My Profile</span>
          <span style={styles.navItemActive}>Requests</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/messages")}>Messages</span>
        </nav>

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Pet Sitter</span>
          <button
            type="button"
            style={{
              ...styles.avatarButton,
              backgroundImage: user?.profilePhotoUrl ? `url(${buildImageUrl(user.profilePhotoUrl)})` : undefined,
              backgroundSize: "cover",
              backgroundPosition: "center",
              backgroundRepeat: "no-repeat",
              backgroundColor: user?.profilePhotoUrl ? "transparent" : styles.avatarButton.backgroundColor,
              border: user?.profilePhotoUrl ? "none" : styles.avatarButton.border,
            }}
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
        <h1 style={styles.title}>Booking Requests</h1>

        {error && <div style={styles.errorBox}>{error}</div>}
        {success && <div style={styles.successBox}>{success}</div>}

        <div style={styles.tabs} role="tablist" aria-label="Request status">
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

        <div style={styles.list}>
          {loading ? (
            <div style={styles.emptyBox}>Loading requests...</div>
          ) : filteredRequests.length === 0 ? (
            <div style={styles.emptyBox}>No requests to show.</div>
          ) : (
            filteredRequests.map((request) => (
              <div key={request.bookingId} style={styles.rowCard}>
                <div
                  style={{
                    ...styles.rowAvatar,
                    backgroundImage: request.ownerProfilePhotoUrl
                      ? `url(${buildImageUrl(request.ownerProfilePhotoUrl)})`
                      : undefined,
                  }}
                />
                <div>
                  <div style={styles.rowName}>{request.ownerName || "Pet Owner"}</div>
                  <div style={styles.rowMeta}>
                    {(request.petNames?.[0] || "Pet")} . {String(request.serviceType || "SERVICE").toLowerCase()} . {formatDate(request.date)} . {formatTime(request.startTime)}-{formatTime(request.endTime)}
                  </div>
                  <div style={styles.rowMetaSecondary}>{request.bookingId || ""}</div>
                </div>
                <div style={styles.rowActions}>
                  <span style={styles.amount}>{formatMoney(request.totalAmount, request.currency)}</span>
                  <span style={getStatusStyle(request.status)}>{getStatusLabel(request.status)}</span>
                  {request.status === "PENDING" && (
                    <>
                      <button type="button" style={styles.buttonDark} onClick={() => updateStatus(request.bookingId, "CONFIRMED")} disabled={saving}>
                        Accept
                      </button>
                      <button type="button" style={styles.buttonLight} onClick={() => updateStatus(request.bookingId, "CANCELLED")} disabled={saving}>
                        Decline
                      </button>
                    </>
                  )}
                  {request.status === "CONFIRMED" && (
                    <button type="button" style={styles.buttonDark} onClick={() => updateStatus(request.bookingId, "COMPLETED")} disabled={saving}>
                      Complete
                    </button>
                  )}
                </div>
              </div>
            ))
          )}
        </div>
      </main>
    </div>
  );
}
