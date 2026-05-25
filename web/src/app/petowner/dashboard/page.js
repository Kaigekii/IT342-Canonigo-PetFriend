"use client";

import { useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function buildImageUrl(url) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return `${API_BASE}${url}`;
  return url;
}

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
  },
  navItemActive: {
    color: "#333333",
    opacity: 1,
    borderBottom: "2px solid #FFD8B9",
    paddingBottom: 8,
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
    border: "none",
    backgroundColor: "#FFD8B9",
    border: "1px solid #D3D3D3",
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
  avatar: {
    marginLeft: "auto",
    width: 44,
    height: 44,
    borderRadius: "50%",
    backgroundColor: "#333333",
  },
  content: {
    maxWidth: 980,
    margin: "0 auto",
    padding: "28px 24px 32px",
  },
  heroRow: {
    display: "grid",
    gridTemplateColumns: "1fr auto",
    gap: 16,
    alignItems: "start",
    marginTop: 4,
  },
  welcomeTitle: {
    fontSize: 38,
    lineHeight: 1.1,
    fontWeight: 800,
    letterSpacing: "0.01em",
    marginBottom: 6,
  },
  welcomeSub: {
    fontSize: 15,
    color: "#7A7A7A",
    fontWeight: 500,
  },
  addPetButton: {
    height: 44,
    padding: "0 18px",
    borderRadius: 8,
    border: "2px solid #FFD8B9",
    backgroundColor: "#FFD8B9",
    color: "#333333",
    fontSize: 13,
    fontWeight: 800,
    letterSpacing: "0.07em",
    textTransform: "uppercase",
    cursor: "pointer",
  },
  metricGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(4, minmax(0, 1fr))",
    gap: 14,
    marginTop: 22,
  },
  metricCard: {
    backgroundColor: "#FFF8F0",
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    height: 90,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
  },
  metricValue: {
    fontSize: 40,
    lineHeight: 1,
    fontWeight: 800,
    marginBottom: 8,
  },
  metricLabel: {
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    color: "#777777",
  },
  addPetPanel: {
    marginTop: 14,
    padding: 12,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
  },
  addPetRow: {
    display: "grid",
    gridTemplateColumns: "1.2fr 1fr 1fr auto",
    gap: 10,
    alignItems: "center",
  },
  sectionTitle: {
    marginTop: 24,
    marginBottom: 10,
    fontSize: 27,
    fontWeight: 800,
    letterSpacing: "0.02em",
    color: "#333333",
    textTransform: "uppercase",
  },
  petGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
    gap: 14,
  },
  petCard: {
    backgroundColor: "#FFF8F0",
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 16,
    display: "flex",
    alignItems: "center",
    gap: 12,
  },
  petPhoto: {
    width: 62,
    height: 62,
    borderRadius: 8,
    backgroundColor: "#FFB6C1",
    position: "relative",
    overflow: "hidden",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 12,
    fontWeight: 700,
    color: "#333333",
  },
  petName: {
    fontSize: 26,
    fontWeight: 700,
    marginBottom: 4,
    lineHeight: 1,
  },
  petMeta: {
    fontSize: 19,
    color: "#7A7A7A",
    fontWeight: 600,
  },
  bookingCard: {
    backgroundColor: "#FFF8F0",
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 16,
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 12,
  },
  bookingLeft: {
    display: "flex",
    alignItems: "center",
    gap: 12,
    minWidth: 0,
  },
  bookingAvatar: {
    width: 40,
    height: 40,
    borderRadius: "50%",
    backgroundColor: "#FFB6C1",
    position: "relative",
    overflow: "hidden",
    flexShrink: 0,
  },
  bookingName: {
    fontSize: 26,
    fontWeight: 700,
    lineHeight: 1,
    marginBottom: 6,
  },
  bookingMeta: {
    fontSize: 19,
    color: "#7A7A7A",
    fontWeight: 600,
  },
  bookingRight: {
    display: "flex",
    alignItems: "center",
    gap: 10,
    flexShrink: 0,
  },
  bookingStatus: {
    fontSize: 12,
    fontWeight: 700,
    borderRadius: 6,
    border: "1px solid #B6E5D8",
    padding: "4px 10px",
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    color: "#333333",
    backgroundColor: "#B6E5D8",
  },
  bookingAmount: {
    fontSize: 28,
    fontWeight: 800,
    lineHeight: 1,
  },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: 14,
    marginTop: 16,
  },
  successBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#B6E5D8",
    border: "2px solid #B6E5D8",
    color: "#333333",
    fontSize: 14,
    marginTop: 16,
  },
  button: {
    height: 42,
    padding: "0 14px",
    borderRadius: 8,
    border: "2px solid #FFD8B9",
    backgroundColor: "#FFD8B9",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.07em",
    textTransform: "uppercase",
    cursor: "pointer",
  },
  buttonDanger: {
    height: 42,
    padding: "0 14px",
    borderRadius: 8,
    border: "2px solid #FFCCBC",
    backgroundColor: "#FFCCBC",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.07em",
    textTransform: "uppercase",
    cursor: "pointer",
  },
  input: {
    width: "100%",
    height: 42,
    padding: "0 12px",
    fontSize: 13,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    outline: "none",
  },
  select: {
    width: "100%",
    height: 42,
    padding: "0 12px",
    fontSize: 13,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    outline: "none",
  },
  emptyBox: {
    backgroundColor: "#FFF8F0",
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 16,
    fontSize: 14,
    color: "#5E5E5E",
    fontWeight: 600,
  },
};

function formatTime(t) {
  if (!t) return "";
  const parts = String(t).split(":");
  if (parts.length >= 2) return `${parts[0]}:${parts[1]}`;
  return String(t);
}

function formatMoney(amount, currency) {
  if (typeof amount !== "number") return "-";
  const symbol = currency === "PHP" || !currency ? "P" : `${currency} `;
  return `${symbol}${amount.toFixed(2)}`;
}

export default function PetOwnerDashboardPage() {
  const router = useRouter();

  const [user, setUser] = useState(null);
  const [pets, setPets] = useState([]);
  const [upcomingBookings, setUpcomingBookings] = useState([]);
  const [allBookings, setAllBookings] = useState([]);
  const [reviewedBookingIds, setReviewedBookingIds] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }

    const authHeaders = {
      Authorization: `Bearer ${token}`,
    };

    const load = async () => {
      setError("");
      setLoading(true);

      try {
        const meRes = await fetch(`${API_BASE}/api/user/me`, { headers: authHeaders });
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

        setUser(me);

        const [petsRes, upcomingRes, allRes, reviewedRes] = await Promise.all([
          fetch(`${API_BASE}/api/pets`, { headers: authHeaders }),
          fetch(`${API_BASE}/api/bookings?upcoming=true`, { headers: authHeaders }),
          fetch(`${API_BASE}/api/bookings`, { headers: authHeaders }),
          fetch(`${API_BASE}/api/reviews/me/reviewed-bookings`, { headers: authHeaders }),
        ]);

        if (petsRes.status === 401 || upcomingRes.status === 401 || allRes.status === 401 || reviewedRes.status === 401) {
          localStorage.removeItem("token");
          router.replace("/login");
          return;
        }

        if (!petsRes.ok) throw new Error("Failed to load pets");
        if (!upcomingRes.ok || !allRes.ok) throw new Error("Failed to load bookings");
        if (!reviewedRes.ok) throw new Error("Failed to load review stats");

        const [petsData, upcomingData, allData, reviewedData] = await Promise.all([
          petsRes.json(),
          upcomingRes.json(),
          allRes.json(),
          reviewedRes.json(),
        ]);

        setPets(Array.isArray(petsData) ? petsData : []);
        setUpcomingBookings(Array.isArray(upcomingData) ? upcomingData : []);
        setAllBookings(Array.isArray(allData) ? allData : []);
        setReviewedBookingIds(Array.isArray(reviewedData) ? reviewedData : []);
      } catch (e) {
        setError(e?.message || "Something went wrong");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [router, token]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.replace("/login");
  };

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

  const completedCount = allBookings.filter((b) => b.status === "COMPLETED").length;
  const pendingReviews = allBookings
    .filter((b) => b.status === "COMPLETED")
    .filter((b) => !reviewedBookingIds.includes(b.bookingId)).length;
  const primaryUpcoming = upcomingBookings[0] || null;

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItemActive}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/pets")}>My Pets</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/find-sitter")}>Find Sitters</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/bookings")}>Bookings</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/messages")}>Messages</span>
        </nav>

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Pet Owner</span>
          <button
            type="button"
            style={{
              ...styles.avatarButton,
              backgroundImage: user?.profilePhotoUrl ? `url(${buildImageUrl(user.profilePhotoUrl)})` : undefined,
              backgroundSize: "cover",
              backgroundPosition: "center",
            }}
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
                  router.push("/petowner/profile");
                }}
              >
                Profile
              </button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>
                Logout
              </button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <div style={styles.heroRow}>
          <div>
            <div style={styles.welcomeTitle}>Welcome back, {user?.firstName || "Pet Owner"}</div>
            <div style={styles.welcomeSub}>Pet Owner Dashboard</div>
          </div>

          <button
            type="button"
            style={styles.addPetButton}
            onClick={() => router.push("/petowner/pets")}
            disabled={loading}
          >
            + Add Pet
          </button>
        </div>

        <div style={styles.metricGrid}>
          <div style={styles.metricCard}>
            <div style={styles.metricValue}>{pets.length}</div>
            <div style={styles.metricLabel}>My Pets</div>
          </div>
          <div style={styles.metricCard}>
            <div style={styles.metricValue}>{upcomingBookings.length}</div>
            <div style={styles.metricLabel}>Upcoming Bookings</div>
          </div>
          <div style={styles.metricCard}>
            <div style={styles.metricValue}>{completedCount}</div>
            <div style={styles.metricLabel}>Completed</div>
          </div>
          <div style={styles.metricCard}>
            <div style={styles.metricValue}>{pendingReviews}</div>
            <div style={styles.metricLabel}>Pending Reviews</div>
          </div>
        </div>

        {error && <div style={styles.errorBox}>{error}</div>}

        <section aria-label="My Pets">
          <h2 style={styles.sectionTitle}>My Pets</h2>

          <div style={styles.petGrid}>
            {loading ? (
              <div style={styles.emptyBox}>Loading pets...</div>
            ) : pets.length === 0 ? (
              <div style={styles.emptyBox}>No pets yet.</div>
            ) : (
              pets.slice(0, 4).map((p) => (
                <div key={p.petId} style={styles.petCard}>
                  <div style={styles.petPhoto}>
                    <span>No Photo</span>
                    {p.photoUrl && (
                      <img
                        src={buildImageUrl(p.photoUrl)}
                        alt={p.name}
                        style={{ position: "absolute", inset: 0, width: "100%", height: "100%", objectFit: "cover" }}
                        onError={(e) => {
                          e.currentTarget.style.display = "none";
                        }}
                      />
                    )}
                  </div>
                  <div>
                    <div style={styles.petName}>{p.name}</div>
                    <div style={styles.petMeta}>
                      {p.breed || "Pet"} . {p.species || "OTHER"} . {typeof p.age === "number" ? `${p.age} yrs` : "-"}
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </section>

        <section aria-label="Upcoming Bookings">
          <h2 style={styles.sectionTitle}>Upcoming Bookings</h2>

          {loading ? (
            <div style={styles.emptyBox}>Loading bookings...</div>
          ) : !primaryUpcoming ? (
            <div style={styles.emptyBox}>No upcoming bookings.</div>
          ) : (
            <div style={styles.bookingCard}>
              <div style={styles.bookingLeft}>
                <div style={styles.bookingAvatar}>
                  {primaryUpcoming.sitterProfilePhotoUrl && (
                    <img
                      src={buildImageUrl(primaryUpcoming.sitterProfilePhotoUrl)}
                      alt={primaryUpcoming.sitterName || "Pet sitter"}
                      style={{ position: "absolute", inset: 0, width: "100%", height: "100%", objectFit: "cover" }}
                      onError={(e) => {
                        e.currentTarget.style.display = "none";
                      }}
                    />
                  )}
                </div>
                <div>
                  <div style={styles.bookingName}>{primaryUpcoming.sitterName || "Pending Sitter"}</div>
                  <div style={styles.bookingMeta}>
                    {(primaryUpcoming.serviceType || "SERVICE").toLowerCase()} . {primaryUpcoming.date} . {formatTime(primaryUpcoming.startTime)}-{formatTime(primaryUpcoming.endTime)}
                  </div>
                </div>
              </div>

              <div style={styles.bookingRight}>
                <span style={styles.bookingStatus}>{primaryUpcoming.status || "PENDING"}</span>
                <span style={styles.bookingAmount}>{formatMoney(primaryUpcoming.totalAmount, primaryUpcoming.currency)}</span>
              </div>
            </div>
          )}
        </section>

        <div style={{ height: 24 }} />
      </main>
    </div>
  );
}
