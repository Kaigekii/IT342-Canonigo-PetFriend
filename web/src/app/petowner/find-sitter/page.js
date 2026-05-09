"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

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
  navItemActive: { color: "#333333", opacity: 1, borderBottom: "2px solid #FFD8B9", paddingBottom: 8 },
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
  content: { maxWidth: 1200, margin: "0 auto", padding: "28px 24px 36px" },
  title: { fontSize: 42, lineHeight: 1.05, fontWeight: 800, marginBottom: 20 },
  searchCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 14,
    marginBottom: 20,
  },
  searchGrid: { display: "grid", gridTemplateColumns: "1fr 1fr auto", gap: 14, alignItems: "end" },
  fieldLabel: {
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
  searchBtn: {
    height: 44,
    padding: "0 22px",
    borderRadius: 6,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 13,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    cursor: "pointer",
  },
  resultHeading: {
    fontSize: 24,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.05em",
    marginBottom: 12,
    color: "#777777",
  },
  sitterList: { display: "grid", gap: 12 },
  sitterCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 14,
    display: "grid",
    gridTemplateColumns: "78px 1fr auto",
    gap: 14,
    alignItems: "center",
    cursor: "pointer",
  },
  sitterAvatar: {
    width: 64,
    height: 64,
    borderRadius: "50%",
    backgroundColor: "#D3D3D3",
  },
  sitterName: { fontSize: 30, fontWeight: 800, lineHeight: 1, marginBottom: 8 },
  sitterBio: { fontSize: 18, color: "#606060", fontWeight: 600, marginBottom: 6 },
  sitterMeta: { fontSize: 20, color: "#5D5D5D", fontWeight: 700 },
  sitterRate: { fontSize: 34, fontWeight: 800, whiteSpace: "nowrap" },
  errorBox: {
    marginBottom: 12,
    padding: "12px",
    borderRadius: "10px",
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: "14px",
  },
  emptyBox: {
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

function displayServices(services) {
  if (!Array.isArray(services) || services.length === 0) {
    return "No services listed";
  }
  return services.map((s) => s.charAt(0).toUpperCase() + s.slice(1).toLowerCase()).join(", ");
}

function formatRate(rate) {
  const num = Number(rate);
  if (!Number.isFinite(num)) return "P0/hr";
  return `P${num.toFixed(0)}/hr`;
}

export default function FindSitterPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [location, setLocation] = useState("Cebu City");
  const [serviceType, setServiceType] = useState("ALL");
  const [sitters, setSitters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searching, setSearching] = useState(false);
  const [error, setError] = useState("");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

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

  const searchSitters = useCallback(async (authToken, nextLoadingState = false) => {
    if (nextLoadingState) setLoading(true);
    setSearching(true);
    setError("");

    try {
      const ok = await ensureOwner(authToken);
      if (!ok) return;

      const params = new URLSearchParams();
      params.set("location", location);
      params.set("serviceType", serviceType);

      const res = await fetch(`${API_BASE}/api/sitters/search?${params.toString()}`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (!res.ok) {
        throw new Error("Failed to search sitters");
      }

      const data = await res.json();
      setSitters(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err?.message || "Unable to search sitters.");
    } finally {
      setSearching(false);
      setLoading(false);
    }
  }, [ensureOwner, location, serviceType]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }
    searchSitters(token, true);
  }, [router, searchSitters, token]);

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

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petowner/dashboard")}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/pets")}>My Pets</span>
          <span style={styles.navItemActive}>Find Sitter</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/bookings")}>Bookings</span>
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
              <button type="button" style={styles.menuItem} onClick={() => router.push("/dashboard")}>Profile</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>Find a Pet Sitter</h1>

        {error ? <div style={styles.errorBox}>{error}</div> : null}

        <section style={styles.searchCard}>
          <div style={styles.searchGrid}>
            <div>
              <div style={styles.fieldLabel}>Location</div>
              <input style={styles.input} value={location} onChange={(e) => setLocation(e.target.value)} />
            </div>
            <div>
              <div style={styles.fieldLabel}>Service Type</div>
              <select style={styles.input} value={serviceType} onChange={(e) => setServiceType(e.target.value)}>
                <option value="ALL">All Services</option>
                <option value="WALK">Walk</option>
                <option value="FEEDING">Feeding</option>
                <option value="OVERNIGHT">Overnight</option>
                <option value="PLAYTIME">Playtime</option>
              </select>
            </div>
            <button
              type="button"
              style={styles.searchBtn}
              disabled={searching || loading}
              onClick={() => searchSitters(token)}
            >
              {searching ? "Searching..." : "Search"}
            </button>
          </div>
        </section>

        <h2 style={styles.resultHeading}>{loading ? "Loading..." : `${sitters.length} sitters found`}</h2>

        {loading ? (
          <div style={styles.emptyBox}>Loading sitters...</div>
        ) : sitters.length === 0 ? (
          <div style={styles.emptyBox}>No sitters match your current filters.</div>
        ) : (
          <section style={styles.sitterList}>
            {sitters.map((sitter) => (
              <article
                key={sitter.sitterId}
                style={styles.sitterCard}
                onClick={() => router.push(`/petowner/find-sitter/${sitter.sitterId}`)}
              >
                <div style={styles.sitterAvatar} />
                <div>
                  <div style={styles.sitterName}>{sitter.fullName}</div>
                  <div style={styles.sitterBio}>{sitter.bio || sitter.experience || "Pet care enthusiast"}</div>
                  <div style={styles.sitterMeta}>
                    ⭐ {sitter.rating ?? 4.5} ({sitter.reviewCount ?? 0} reviews) · {displayServices(sitter.servicesOffered)}
                  </div>
                </div>
                <div style={styles.sitterRate}>{formatRate(sitter.hourlyRate)}</div>
              </article>
            ))}
          </section>
        )}
      </main>
    </div>
  );
}
