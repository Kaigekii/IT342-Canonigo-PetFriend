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
    background: "none",
    border: "none",
    padding: 0,
    fontSize: 13,
    fontWeight: 600,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
  },
  navItemActive: {
    color: "#333333",
    opacity: 1,
    borderBottom: "2px solid #FFD8B9",
    paddingBottom: 8,
    cursor: "pointer",
    background: "none",
    fontSize: 13,
    fontWeight: 700,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
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
    maxWidth: 1200,
    margin: "0 auto",
    padding: "28px 24px 36px",
  },
  title: {
    fontSize: 40,
    lineHeight: 1.1,
    fontWeight: 800,
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 15,
    color: "#7A7A7A",
    fontWeight: 600,
    marginBottom: 20,
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
  cardList: {
    display: "grid",
    gap: 16,
  },
  sitterCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 16,
  },
  sitterCardRow: {
    display: "grid",
    gridTemplateColumns: "64px 1fr auto",
    gap: 16,
    alignItems: "start",
  },
  idDocPlaceholder: {
    width: 64,
    height: 64,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#D3D3D3",
    color: "#555555",
    fontWeight: 700,
    fontSize: 12,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
  },
  sitterName: {
    fontSize: 30,
    lineHeight: 1,
    fontWeight: 800,
    marginBottom: 8,
  },
  lineText: {
    fontSize: 14,
    color: "#565656",
    fontWeight: 600,
    marginBottom: 6,
  },
  submittedText: {
    fontSize: 14,
    color: "#7A7A7A",
    fontWeight: 600,
    whiteSpace: "nowrap",
  },
  docsRow: {
    marginTop: 10,
    marginBottom: 12,
    display: "flex",
    alignItems: "center",
    gap: 8,
    flexWrap: "wrap",
  },
  docChip: {
    minWidth: 92,
    height: 58,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#7A7A7A",
    fontSize: 12,
    fontWeight: 700,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    textDecoration: "none",
    padding: "0 10px",
  },
  actionsRow: {
    display: "flex",
    gap: 10,
  },
  approveButton: {
    height: 40,
    borderRadius: 8,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 14,
    fontWeight: 800,
    padding: "0 18px",
    cursor: "pointer",
  },
  rejectButton: {
    height: 40,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 14,
    fontWeight: 700,
    padding: "0 18px",
    cursor: "pointer",
  },
  disabledButton: {
    opacity: 0.5,
    cursor: "not-allowed",
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

const navItems = [
  { key: "dashboard", label: "Dashboard" },
  { key: "pending", label: "Pending Sitters", active: true },
  { key: "users", label: "Users" },
  { key: "bookings", label: "All Bookings" },
];

function formatDate(isoText) {
  if (!isoText) return "N/A";
  const dt = new Date(isoText);
  if (Number.isNaN(dt.getTime())) return "N/A";
  return dt.toLocaleDateString("en-US", { month: "short", day: "2-digit", year: "numeric" });
}

function formatRate(value) {
  const num = Number(value);
  if (!Number.isFinite(num)) return "N/A";
  return `P${num.toFixed(0)}/hr`;
}

function parseServices(text) {
  if (!text) return [];
  try {
    const parsed = JSON.parse(text);
    if (!Array.isArray(parsed)) return [];
    return parsed;
  } catch {
    return [];
  }
}

export default function AdminPendingSittersPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [isMobile, setIsMobile] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [pendingSitters, setPendingSitters] = useState([]);
  const [actingId, setActingId] = useState("");

  const loadPendingSitters = useCallback(async (authToken) => {
    setLoading(true);
    setError("");

    try {
      const meRes = await fetch(`${API_BASE}/api/user/me`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (meRes.status === 401) {
        localStorage.removeItem("token");
        router.replace("/login");
        return;
      }

      if (!meRes.ok) {
        throw new Error("Failed to load profile");
      }

      const meData = await meRes.json();
      if (meData?.role === "PET_OWNER") {
        router.replace("/petowner/dashboard");
        return;
      }
      if (meData?.role === "PET_SITTER") {
        router.replace("/petsitter/dashboard");
        return;
      }
      if (meData?.role !== "ADMIN") {
        throw new Error("Admin access required");
      }

      const res = await fetch(`${API_BASE}/api/admin/sitters/pending`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (res.status === 403) {
        throw new Error("You are not allowed to access pending sitters.");
      }

      if (!res.ok) {
        throw new Error("Failed to load pending sitters");
      }

      const data = await res.json();
      setPendingSitters(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err?.message || "Unable to load pending sitters.");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }
    loadPendingSitters(token);
  }, [loadPendingSitters, router, token]);

  useEffect(() => {
    const applyScreenMode = () => {
      setIsMobile(window.innerWidth < 960);
    };

    applyScreenMode();
    window.addEventListener("resize", applyScreenMode);
    return () => window.removeEventListener("resize", applyScreenMode);
  }, []);

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

  const handleNavClick = (key) => {
    if (key === "dashboard") {
      router.push("/admin/dashboard");
      return;
    }
    if (key === "pending") {
      return;
    }
  };

  const handleApprove = async (sitterId) => {
    if (!token) return;
    setActingId(sitterId);
    setError("");
    setSuccess("");

    try {
      const res = await fetch(`${API_BASE}/api/admin/sitters/${sitterId}/approve`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        throw new Error("Failed to approve sitter");
      }

      setPendingSitters((prev) => prev.filter((item) => item.sitterId !== sitterId));
      setSuccess("Sitter approved successfully.");
    } catch (err) {
      setError(err?.message || "Unable to approve sitter.");
    } finally {
      setActingId("");
    }
  };

  const handleReject = async (sitterId) => {
    if (!token) return;
    const reason = window.prompt("Reason for rejection:", "Incomplete verification documents");
    if (!reason || !reason.trim()) {
      return;
    }

    setActingId(sitterId);
    setError("");
    setSuccess("");

    try {
      const res = await fetch(`${API_BASE}/api/admin/sitters/${sitterId}/reject`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ reason: reason.trim() }),
      });

      if (!res.ok) {
        throw new Error("Failed to reject sitter");
      }

      setPendingSitters((prev) => prev.filter((item) => item.sitterId !== sitterId));
      setSuccess("Sitter application rejected.");
    } catch (err) {
      setError(err?.message || "Unable to reject sitter.");
    } finally {
      setActingId("");
    }
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>

        {!isMobile && (
          <nav style={styles.nav}>
            {navItems.map((item) => (
              <button
                key={item.key}
                type="button"
                style={item.active ? styles.navItemActive : styles.navItem}
                onClick={() => handleNavClick(item.key)}
              >
                {item.label}
              </button>
            ))}
          </nav>
        )}

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Admin</span>
          <button
            type="button"
            onClick={() => setShowProfileMenu((prev) => !prev)}
            style={styles.avatarButton}
            aria-label="Open admin menu"
          />

          {showProfileMenu && (
            <div style={styles.profileMenu}>
              <button type="button" style={styles.menuItem}>
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
        <h1 style={styles.title}>Pending Sitter Applications</h1>
        <p style={styles.subtitle}>
          {loading ? "Loading applications..." : `${pendingSitters.length} applications waiting for review`}
        </p>

        {error ? <div style={styles.errorBox}>{error}</div> : null}
        {success ? <div style={styles.successBox}>{success}</div> : null}

        {loading ? (
          <div style={styles.emptyBox}>Loading pending sitters...</div>
        ) : pendingSitters.length === 0 ? (
          <div style={styles.emptyBox}>No pending sitter applications right now.</div>
        ) : (
          <section style={styles.cardList}>
            {pendingSitters.map((sitter) => {
              const services = parseServices(sitter.servicesJson);
              const submitting = actingId === sitter.sitterId;

              return (
                <article key={sitter.sitterId} style={styles.sitterCard}>
                  <div
                    style={{
                      ...styles.sitterCardRow,
                      gridTemplateColumns: isMobile ? "1fr" : styles.sitterCardRow.gridTemplateColumns,
                    }}
                  >
                    {!isMobile ? <div style={styles.idDocPlaceholder}>ID Doc</div> : null}

                    <div>
                      <h2 style={styles.sitterName}>{sitter.fullName || "Unnamed Sitter"}</h2>
                      <p style={styles.lineText}>
                        {sitter.email || "No email"}
                        {sitter.studentId ? ` · Student ID: ${sitter.studentId}` : ""}
                      </p>
                      <p style={styles.lineText}>{sitter.bio || sitter.experience || "No profile description provided."}</p>
                      <p style={styles.lineText}>
                        Rate: {formatRate(sitter.hourlyRate)}
                        {services.length ? ` · Services: ${services.join(", ")}` : ""}
                      </p>

                      <div style={styles.docsRow}>
                        <div style={styles.docChip}>Doc 1</div>
                        {sitter.verificationDocumentUrl ? (
                          <a
                            href={sitter.verificationDocumentUrl}
                            target="_blank"
                            rel="noreferrer"
                            style={styles.docChip}
                          >
                            View Doc 2
                          </a>
                        ) : (
                          <div style={styles.docChip}>No Doc 2</div>
                        )}
                      </div>

                      <div style={styles.actionsRow}>
                        <button
                          type="button"
                          style={{
                            ...styles.approveButton,
                            ...(submitting ? styles.disabledButton : null),
                          }}
                          disabled={submitting}
                          onClick={() => handleApprove(sitter.sitterId)}
                        >
                          {submitting ? "Processing..." : "Approve"}
                        </button>
                        <button
                          type="button"
                          style={{
                            ...styles.rejectButton,
                            ...(submitting ? styles.disabledButton : null),
                          }}
                          disabled={submitting}
                          onClick={() => handleReject(sitter.sitterId)}
                        >
                          Reject
                        </button>
                      </div>
                    </div>

                    {!isMobile ? (
                      <div style={styles.submittedText}>Submitted: {formatDate(sitter.submittedAt)}</div>
                    ) : null}
                  </div>
                </article>
              );
            })}
          </section>
        )}
      </main>
    </div>
  );
}
