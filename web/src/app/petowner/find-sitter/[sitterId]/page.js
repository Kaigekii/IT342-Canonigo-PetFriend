"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useParams, useRouter, useSearchParams } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

const DAYS = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];

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
  brand: { fontSize: 22, fontWeight: 700, letterSpacing: "0.02em", color: "#333333", whiteSpace: "nowrap" },
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
  content: { maxWidth: 1200, margin: "0 auto", padding: "24px 24px 36px" },
  backLink: {
    border: "none",
    background: "none",
    color: "#666666",
    fontSize: 14,
    fontWeight: 700,
    cursor: "pointer",
    marginBottom: 14,
    padding: 0,
  },
  topPanel: {
    display: "grid",
    gridTemplateColumns: "96px 1fr auto",
    gap: 18,
    alignItems: "start",
    marginBottom: 14,
  },
  sitterAvatar: { width: 88, height: 88, borderRadius: "50%", backgroundColor: "#D3D3D3" },
  sitterName: { fontSize: 44, fontWeight: 800, lineHeight: 1, marginBottom: 10 },
  sitterBio: { fontSize: 20, color: "#5A5A5A", fontWeight: 600, marginBottom: 10 },
  ratingRow: { display: "flex", alignItems: "center", gap: 10, marginBottom: 8, flexWrap: "wrap" },
  ratingText: { fontSize: 32, fontWeight: 800 },
  verifiedBadge: {
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    backgroundColor: "#FFF8F0",
    padding: "4px 10px",
    fontSize: 16,
    fontWeight: 800,
    letterSpacing: "0.05em",
    textTransform: "uppercase",
  },
  experienceText: { fontSize: 17, color: "#666666", fontWeight: 600 },
  priceWrap: { textAlign: "right" },
  priceText: { fontSize: 52, fontWeight: 800, lineHeight: 1.1 },
  perHourText: { fontSize: 16, color: "#777777", fontWeight: 800, textTransform: "uppercase", letterSpacing: "0.06em" },
  mainGrid: { display: "grid", gridTemplateColumns: "1.5fr 1fr", gap: 14 },
  panel: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 14,
    marginBottom: 12,
  },
  panelTitle: {
    fontSize: 28,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.05em",
    marginBottom: 10,
  },
  chipsRow: { display: "flex", flexWrap: "wrap", gap: 8 },
  chip: {
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    padding: "8px 12px",
    fontSize: 14,
    fontWeight: 700,
    backgroundColor: "#FFF8F0",
  },
  scheduleGrid: { display: "grid", gridTemplateColumns: "repeat(7, minmax(0, 1fr))", gap: 8 },
  dayCell: { textAlign: "center" },
  dayLabel: { fontSize: 12, color: "#888888", fontWeight: 800, marginBottom: 6, textTransform: "uppercase" },
  dayHours: {
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    padding: "8px 4px",
    backgroundColor: "#FFF8F0",
    fontSize: 13,
    fontWeight: 700,
    color: "#555555",
  },
  reviewList: { display: "grid", gap: 12 },
  reviewCard: { borderBottom: "1px solid #D3D3D3", paddingBottom: 10 },
  reviewHead: { display: "flex", justifyContent: "space-between", gap: 8, marginBottom: 6 },
  reviewName: { fontSize: 17, fontWeight: 800 },
  reviewDate: { fontSize: 13, color: "#777777", fontWeight: 700 },
  reviewRating: { fontSize: 16, color: "#D8A900", fontWeight: 700, marginBottom: 5 },
  reviewText: { fontSize: 15, color: "#666666", fontWeight: 600, lineHeight: 1.45 },
  formLabel: {
    fontSize: 12,
    color: "#7A7A7A",
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    fontWeight: 800,
    marginBottom: 6,
  },
  input: {
    width: "100%",
    height: 44,
    padding: "0 12px",
    fontSize: 14,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    outline: "none",
  },
  textArea: {
    width: "100%",
    minHeight: 84,
    resize: "vertical",
    padding: 10,
    fontSize: 14,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    outline: "none",
  },
  formGrid2: { display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 },
  totalsWrap: { marginTop: 12, marginBottom: 12 },
  totalRow: { display: "flex", justifyContent: "space-between", gap: 10, marginBottom: 6, fontSize: 15, fontWeight: 700 },
  totalRowStrong: { display: "flex", justifyContent: "space-between", gap: 10, marginTop: 8, fontSize: 20, fontWeight: 800 },
  confirmBtn: {
    width: "100%",
    height: 44,
    borderRadius: 6,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 14,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    cursor: "pointer",
  },
  errorBox: {
    marginBottom: 12,
    padding: "12px",
    borderRadius: "10px",
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: "14px",
  },
  successBox: {
    marginBottom: 12,
    padding: "12px",
    borderRadius: "10px",
    backgroundColor: "#B6E5D8",
    border: "2px solid #B6E5D8",
    color: "#333333",
    fontSize: "14px",
  },
  empty: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: "14px",
    fontSize: "14px",
    color: "#666666",
    fontWeight: 600,
  },
};

function toServiceLabel(s) {
  if (!s) return "-";
  return s.charAt(0).toUpperCase() + s.slice(1).toLowerCase();
}

function stars(n) {
  return "⭐".repeat(Math.max(1, Math.min(5, Number(n) || 5)));
}

function getDayHours(schedule, day) {
  const row = schedule?.[day] || schedule?.[day.slice(0, 3)] || { startTime: "", endTime: "" };
  if (!row.startTime || !row.endTime) return "-";
  return `${row.startTime.slice(0, 5)}-${row.endTime.slice(0, 5)}`;
}

function computeHours(start, end) {
  const [sh, sm] = (start || "00:00").split(":").map(Number);
  const [eh, em] = (end || "00:00").split(":").map(Number);
  const minutes = (eh * 60 + em) - (sh * 60 + sm);
  if (!Number.isFinite(minutes) || minutes <= 0) return 1;
  return minutes / 60;
}

function formatMoney(v) {
  const n = Number(v);
  if (!Number.isFinite(n)) return "P0.00";
  return `P${n.toFixed(2)}`;
}

function todayString() {
  const d = new Date();
  const yyyy = d.getFullYear();
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const dd = String(d.getDate()).padStart(2, "0");
  return `${yyyy}-${mm}-${dd}`;
}

export default function SitterDetailPage() {
  const router = useRouter();
  const params = useParams();
  const searchParams = useSearchParams();
  const menuRef = useRef(null);

  const sitterId = params?.sitterId;
  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [sitter, setSitter] = useState(null);
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const [serviceType, setServiceType] = useState("WALK");
  const [date, setDate] = useState(searchParams.get("date") || todayString());
  const [startTime, setStartTime] = useState("09:00");
  const [endTime, setEndTime] = useState("10:00");
  const [petId, setPetId] = useState("");
  const [specialInstructions, setSpecialInstructions] = useState("");

  const ensureOwner = useCallback(async (authToken) => {
    const meRes = await fetch(`${API_BASE}/api/user/me`, {
      headers: { Authorization: `Bearer ${authToken}` },
    });

    if (meRes.status === 401) {
      localStorage.removeItem("token");
      router.replace("/login");
      return false;
    }

    if (!meRes.ok) {
      throw new Error("Failed to load profile");
    }

    const me = await meRes.json();
    if (me?.role === "PET_SITTER") {
      router.replace("/petsitter/dashboard");
      return false;
    }
    if (me?.role === "ADMIN") {
      router.replace("/admin/dashboard");
      return false;
    }
    if (me?.role !== "PET_OWNER") {
      throw new Error("Pet owner access required");
    }

    return true;
  }, [router]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }

    const load = async () => {
      setLoading(true);
      setError("");

      try {
        const ok = await ensureOwner(token);
        if (!ok) return;

        const [sitterRes, petsRes] = await Promise.all([
          fetch(`${API_BASE}/api/sitters/${sitterId}`, { headers: { Authorization: `Bearer ${token}` } }),
          fetch(`${API_BASE}/api/pets`, { headers: { Authorization: `Bearer ${token}` } }),
        ]);

        if (!sitterRes.ok) throw new Error("Failed to load sitter details");
        if (!petsRes.ok) throw new Error("Failed to load pets");

        const sitterData = await sitterRes.json();
        const petsData = await petsRes.json();

        setSitter(sitterData);
        setPets(Array.isArray(petsData) ? petsData : []);
        if (Array.isArray(petsData) && petsData.length > 0) {
          setPetId(petsData[0].petId);
        }

        const services = Array.isArray(sitterData?.servicesOffered) ? sitterData.servicesOffered : [];
        if (services.length > 0) {
          setServiceType(String(services[0]).toUpperCase());
        }
      } catch (err) {
        setError(err?.message || "Unable to load sitter details.");
      } finally {
        setLoading(false);
      }
    };

    load();
  }, [ensureOwner, router, sitterId, token]);

  useEffect(() => {
    const onClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
    };

    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.replace("/login");
  };

  const hourlyRate = Number(sitter?.hourlyRate || 0);
  const durationHours = computeHours(startTime, endTime);
  const base = durationHours * hourlyRate;
  const fee = base * 0.1;
  const total = base + fee;

  const handleConfirmPay = async () => {
    if (!token) return;
    if (!petId) {
      setError("Select a pet before booking.");
      return;
    }
    if (!serviceType) {
      setError("Select a service type.");
      return;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const res = await fetch(`${API_BASE}/api/bookings`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          sitterId,
          petIds: [petId],
          serviceType,
          date,
          startTime,
          endTime,
          specialInstructions,
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to create booking");
      }

      setSuccess("Booking created successfully. Payment is simulated in sandbox mode.");
    } catch (err) {
      setError(err?.message || "Unable to create booking.");
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
          <span style={styles.navItemActive} onClick={() => router.push("/petowner/find-sitter")}>Find Sitter</span>
          <span style={styles.navItem}>Bookings</span>
          <span style={styles.navItem}>Messages</span>
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
              <button type="button" style={styles.menuItem} onClick={() => router.push("/dashboard")}>Profile</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <button type="button" style={styles.backLink} onClick={() => router.push("/petowner/find-sitter")}>← Back to search</button>

        {error ? <div style={styles.errorBox}>{error}</div> : null}
        {success ? <div style={styles.successBox}>{success}</div> : null}

        {loading ? (
          <div style={styles.empty}>Loading sitter details...</div>
        ) : !sitter ? (
          <div style={styles.empty}>Sitter not found.</div>
        ) : (
          <>
            <section style={styles.topPanel}>
              <div style={styles.sitterAvatar} />
              <div>
                <h1 style={styles.sitterName}>{sitter.fullName}</h1>
                <p style={styles.sitterBio}>{sitter.bio || "Loves dogs and cats."}</p>
                <div style={styles.ratingRow}>
                  <span style={styles.ratingText}>⭐ {sitter.rating} ({sitter.reviewCount} reviews)</span>
                  {sitter.verified ? <span style={styles.verifiedBadge}>✓ Verified</span> : null}
                </div>
                <p style={styles.experienceText}>{sitter.experience || "Experienced pet sitter"}</p>
              </div>
              <div style={styles.priceWrap}>
                <div style={styles.priceText}>{formatMoney(sitter.hourlyRate || 0)}</div>
                <div style={styles.perHourText}>Per Hour</div>
              </div>
            </section>

            <section style={styles.mainGrid}>
              <div>
                <div style={styles.panel}>
                  <h2 style={styles.panelTitle}>Services Offered</h2>
                  <div style={styles.chipsRow}>
                    {(sitter.servicesOffered || []).map((svc) => (
                      <span key={svc} style={styles.chip}>{toServiceLabel(svc)}</span>
                    ))}
                  </div>
                </div>

                <div style={styles.panel}>
                  <h2 style={styles.panelTitle}>Availability</h2>
                  <div style={styles.scheduleGrid}>
                    {DAYS.map((day) => (
                      <div key={day} style={styles.dayCell}>
                        <div style={styles.dayLabel}>{day.slice(0, 3)}</div>
                        <div style={styles.dayHours}>{getDayHours(sitter.availabilitySchedule, day)}</div>
                      </div>
                    ))}
                  </div>
                </div>

                <div style={styles.panel}>
                  <h2 style={styles.panelTitle}>Reviews</h2>
                  <div style={styles.reviewList}>
                    {(sitter.reviews || []).map((review, idx) => (
                      <div key={`${review.reviewerName}-${idx}`} style={{ ...styles.reviewCard, borderBottom: idx === (sitter.reviews.length - 1) ? "none" : styles.reviewCard.borderBottom }}>
                        <div style={styles.reviewHead}>
                          <div style={styles.reviewName}>{review.reviewerName}</div>
                          <div style={styles.reviewDate}>{review.date}</div>
                        </div>
                        <div style={styles.reviewRating}>{stars(review.rating)}</div>
                        <div style={styles.reviewText}>{review.comment}</div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div>
                <div style={styles.panel}>
                  <h2 style={styles.panelTitle}>Book This Sitter</h2>

                  <div style={styles.formLabel}>Service Type</div>
                  <select style={styles.input} value={serviceType} onChange={(e) => setServiceType(e.target.value)}>
                    {(sitter.servicesOffered || []).map((svc) => (
                      <option key={svc} value={String(svc).toUpperCase()}>{toServiceLabel(svc)}</option>
                    ))}
                  </select>

                  <div style={styles.formLabel}>Date</div>
                  <input style={styles.input} type="date" value={date} onChange={(e) => setDate(e.target.value)} />

                  <div style={styles.formGrid2}>
                    <div>
                      <div style={styles.formLabel}>Start</div>
                      <input style={styles.input} type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
                    </div>
                    <div>
                      <div style={styles.formLabel}>End</div>
                      <input style={styles.input} type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
                    </div>
                  </div>

                  <div style={styles.formLabel}>Select Pet</div>
                  <select style={styles.input} value={petId} onChange={(e) => setPetId(e.target.value)}>
                    {pets.length === 0 ? <option value="">No pets available</option> : null}
                    {pets.map((pet) => (
                      <option key={pet.petId} value={pet.petId}>{pet.name}</option>
                    ))}
                  </select>

                  <div style={styles.formLabel}>Special Instructions</div>
                  <textarea
                    style={styles.textArea}
                    value={specialInstructions}
                    onChange={(e) => setSpecialInstructions(e.target.value)}
                    placeholder="Optional notes..."
                  />

                  <div style={styles.totalsWrap}>
                    <div style={styles.totalRow}>
                      <span>{durationHours.toFixed(1)}hr × {formatMoney(hourlyRate)}</span>
                      <span>{formatMoney(base)}</span>
                    </div>
                    <div style={styles.totalRow}>
                      <span>Service fee (10%)</span>
                      <span>{formatMoney(fee)}</span>
                    </div>
                    <div style={styles.totalRowStrong}>
                      <span>Total</span>
                      <span>{formatMoney(total)}</span>
                    </div>
                  </div>

                  <button type="button" style={styles.confirmBtn} onClick={handleConfirmPay} disabled={saving || !petId}>
                    {saving ? "Processing..." : "Confirm & Pay"}
                  </button>
                </div>
              </div>
            </section>
          </>
        )}
      </main>
    </div>
  );
}
