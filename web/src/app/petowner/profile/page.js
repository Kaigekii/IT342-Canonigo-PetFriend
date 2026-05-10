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
    flexWrap: "wrap",
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
    maxWidth: 760,
    margin: "0 auto",
    padding: "28px 24px 40px",
  },
  title: {
    fontSize: 24,
    fontWeight: 800,
    marginBottom: 18,
    letterSpacing: "0.01em",
  },
  card: {
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    borderRadius: 8,
    padding: 16,
    marginBottom: 18,
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    marginBottom: 14,
  },
  photoRow: {
    display: "flex",
    alignItems: "center",
    gap: 14,
    flexWrap: "wrap",
  },
  photoCircle: {
    width: 66,
    height: 66,
    borderRadius: "50%",
    backgroundColor: "#BEBEBE",
    color: "#333333",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.04em",
  },
  helperText: {
    fontSize: 11,
    color: "#7A7A7A",
    marginTop: 6,
    fontWeight: 600,
  },
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
    height: 40,
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    padding: "0 10px",
    fontSize: 13,
    outline: "none",
  },
  select: {
    width: "100%",
    height: 40,
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    padding: "0 10px",
    fontSize: 13,
    outline: "none",
  },
  grid2: {
    display: "grid",
    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
    gap: 10,
  },
  grid3: {
    display: "grid",
    gridTemplateColumns: "repeat(3, minmax(0, 1fr))",
    gap: 10,
  },
  textarea: {
    width: "100%",
    minHeight: 84,
    resize: "vertical",
    padding: 10,
    fontSize: 13,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    outline: "none",
  },
  toggleList: {
    display: "grid",
    gap: 10,
  },
  toggleRow: {
    display: "flex",
    alignItems: "center",
    gap: 10,
    fontSize: 12,
    color: "#555555",
    fontWeight: 600,
  },
  toggle: {
    width: 36,
    height: 20,
    borderRadius: 999,
    border: "1px solid #D3D3D3",
    backgroundColor: "#E8E8E8",
    position: "relative",
    cursor: "pointer",
    flexShrink: 0,
  },
  toggleOn: {
    backgroundColor: "#1E1E1E",
    borderColor: "#1E1E1E",
  },
  toggleKnob: {
    width: 14,
    height: 14,
    borderRadius: "50%",
    backgroundColor: "#FFFFFF",
    position: "absolute",
    top: 2,
    left: 2,
    transition: "transform 0.15s ease",
  },
  toggleKnobOn: {
    transform: "translateX(16px)",
  },
  actions: {
    display: "flex",
    gap: 12,
    flexWrap: "wrap",
    marginTop: 4,
  },
  primaryBtn: {
    height: 42,
    padding: "0 18px",
    borderRadius: 8,
    border: "2px solid #1E1E1E",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    cursor: "pointer",
  },
  secondaryBtn: {
    height: 42,
    padding: "0 18px",
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    cursor: "pointer",
  },
  dangerBtn: {
    height: 42,
    padding: "0 18px",
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#7A7A7A",
    fontSize: 12,
    fontWeight: 700,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    cursor: "pointer",
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
  subtleText: {
    fontSize: 12,
    color: "#7A7A7A",
    fontWeight: 600,
  },
};

const NOTIFICATION_ITEMS = [
  "Email me about booking updates",
  "Email me about new messages",
  "Push notifications for booking reminders",
  "SMS notifications for urgent updates",
];

function splitName(fullName) {
  const trimmed = (fullName || "").trim();
  if (!trimmed) {
    return { firstName: "", lastName: "" };
  }

  const parts = trimmed.split(/\s+/);
  return {
    firstName: parts[0] || "",
    lastName: parts.slice(1).join(" ") || "",
  };
}

function combineName(firstName, lastName) {
  return [firstName, lastName].filter(Boolean).join(" ").trim();
}

export default function PetOwnerProfilePage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [user, setUser] = useState(null);
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [location, setLocation] = useState("");
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");
  const [notifications, setNotifications] = useState([true, true, false, false]);
  const [profilePhotoUrl, setProfilePhotoUrl] = useState("");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const loadProfile = useCallback(async (authToken) => {
    const res = await fetch(`${API_BASE}/api/user/me`, {
      headers: { Authorization: `Bearer ${authToken}` },
    });

    if (res.status === 401) {
      localStorage.removeItem("token");
      router.replace("/login");
      return;
    }

    if (!res.ok) {
      throw new Error("Failed to load profile");
    }

    const data = await res.json();
    setUser(data);
    setFullName(combineName(data?.firstName, data?.lastName));
    setEmail(data?.email || "");
    setPhoneNumber(data?.phoneNumber || "");
    setLocation(data?.address || "");
    setProfilePhotoUrl(data?.profilePhotoUrl || "");
  }, [router]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }

    const run = async () => {
      setLoading(true);
      setError("");
      try {
        await loadProfile(token);
      } catch (err) {
        setError(err?.message || "Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    run();
  }, [loadProfile, router, token]);

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

  const handleSaveChanges = async () => {
    if (!token) return;

    const trimmedName = fullName.trim();
    const nameParts = splitName(trimmedName);

    if (!trimmedName) {
      setError("Full name is required.");
      return;
    }

    if (newPassword || confirmNewPassword || currentPassword) {
      if (!currentPassword || !newPassword || !confirmNewPassword) {
        setError("Complete all password fields to change your password.");
        return;
      }
      if (newPassword !== confirmNewPassword) {
        setError("New passwords do not match.");
        return;
      }
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const res = await fetch(`${API_BASE}/api/user/me`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          firstName: nameParts.firstName,
          lastName: nameParts.lastName,
          phoneNumber: phoneNumber.trim() || null,
          address: location.trim() || null,
          profilePhotoUrl: profilePhotoUrl.trim() || null,
          currentPassword: currentPassword || null,
          newPassword: newPassword || null,
          confirmNewPassword: confirmNewPassword || null,
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to save profile");
      }

      const updated = await res.json();
      setUser(updated);
      setFullName(combineName(updated?.firstName, updated?.lastName));
      setEmail(updated?.email || email);
      setPhoneNumber(updated?.phoneNumber || "");
      setLocation(updated?.address || "");
      setCurrentPassword("");
      setNewPassword("");
      setConfirmNewPassword("");
      setSuccess("Profile updated successfully.");
    } catch (err) {
      setError(err?.message || "Failed to save profile");
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteAccount = () => {
    setError("Account deletion is not available in this build.");
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petowner/dashboard")}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/pets")}>My Pets</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/find-sitter")}>Find Sitter</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/bookings")}>Bookings</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/messages")}>Messages</span>
          <span style={styles.navItemActive}>Profile</span>
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
              <button type="button" style={styles.menuItem} onClick={() => router.push("/petowner/profile")}>Profile</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>My Profile</h1>

        {error ? <div style={styles.errorBox}>{error}</div> : null}
        {success ? <div style={styles.successBox}>{success}</div> : null}

        <section style={styles.card}>
          <div style={styles.photoRow}>
            <div style={styles.photoCircle}>{profilePhotoUrl ? "Photo" : "Photo"}</div>
            <div>
              <button type="button" style={styles.secondaryBtn} disabled={saving}>Upload New Photo</button>
              <div style={styles.helperText}>Max 5MB . JPG, PNG</div>
            </div>
          </div>
        </section>

        <section style={styles.card}>
          <div style={styles.sectionTitle}>Personal Information</div>
          <div style={styles.grid2}>
            <div>
              <div style={styles.fieldLabel}>Full Name</div>
              <input style={styles.input} value={fullName} onChange={(e) => setFullName(e.target.value)} disabled={loading || saving} />
            </div>
            <div>
              <div style={styles.fieldLabel}>Email</div>
              <input style={styles.input} value={email} disabled readOnly />
            </div>
            <div>
              <div style={styles.fieldLabel}>Phone</div>
              <input style={styles.input} value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} disabled={loading || saving} />
            </div>
            <div>
              <div style={styles.fieldLabel}>Location / Campus</div>
              <input style={styles.input} value={location} onChange={(e) => setLocation(e.target.value)} disabled={loading || saving} />
            </div>
          </div>
        </section>

        <section style={styles.card}>
          <div style={styles.sectionTitle}>Account Settings</div>
          <div style={styles.fieldLabel}>Current Password</div>
          <input
            style={styles.input}
            type="password"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            disabled={loading || saving}
          />
          <div style={{ ...styles.grid2, marginTop: 10 }}>
            <div>
              <div style={styles.fieldLabel}>New Password</div>
              <input
                style={styles.input}
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                disabled={loading || saving}
              />
            </div>
            <div>
              <div style={styles.fieldLabel}>Confirm New Password</div>
              <input
                style={styles.input}
                type="password"
                value={confirmNewPassword}
                onChange={(e) => setConfirmNewPassword(e.target.value)}
                disabled={loading || saving}
              />
            </div>
          </div>
        </section>

        <section style={styles.card}>
          <div style={styles.sectionTitle}>Notification Preferences</div>
          <div style={styles.toggleList}>
            {NOTIFICATION_ITEMS.map((item, index) => {
              const enabled = notifications[index];
              return (
                <div key={item} style={styles.toggleRow}>
                  <button
                    type="button"
                    style={{ ...styles.toggle, ...(enabled ? styles.toggleOn : null) }}
                    onClick={() => setNotifications((prev) => prev.map((value, currentIndex) => (currentIndex === index ? !value : value)))}
                    aria-pressed={enabled}
                    disabled={loading || saving}
                  >
                    <span style={{ ...styles.toggleKnob, ...(enabled ? styles.toggleKnobOn : null) }} />
                  </button>
                  <span>{item}</span>
                </div>
              );
            })}
          </div>
          <div style={{ ...styles.subtleText, marginTop: 10 }}>Notification settings are saved locally in this version.</div>
        </section>

        <div style={styles.actions}>
          <button type="button" style={styles.primaryBtn} onClick={handleSaveChanges} disabled={loading || saving}>
            {saving ? "Saving..." : "Save Changes"}
          </button>
          <button type="button" style={styles.secondaryBtn} onClick={() => router.push("/petowner/dashboard")} disabled={loading || saving}>
            Back to Dashboard
          </button>
          <button type="button" style={styles.dangerBtn} onClick={handleDeleteAccount} disabled={loading || saving}>
            Delete Account
          </button>
        </div>
      </main>
    </div>
  );
}
