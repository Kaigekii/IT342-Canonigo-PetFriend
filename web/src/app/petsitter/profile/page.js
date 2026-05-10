"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const DAYS = ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"];
const SERVICE_OPTIONS = ["walk", "feeding", "overnight", "playtime"];
const PROFILE_BASE_PATHS = ["/api/sitter-profile", "/api/sitters/profile"];

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
    padding: "24px 24px 36px",
  },
  title: {
    fontSize: 32,
    fontWeight: 800,
    marginBottom: 14,
  },
  panel: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 14,
    marginBottom: 14,
  },
  sectionLabel: {
    fontSize: 18,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.06em",
    marginBottom: 10,
  },
  photoRow: {
    display: "flex",
    alignItems: "center",
    gap: 14,
  },
  photoCircle: {
    width: 66,
    height: 66,
    borderRadius: "50%",
    backgroundColor: "#FFB6C1",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 12,
    fontWeight: 700,
  },
  helperText: {
    fontSize: 12,
    color: "#777777",
    marginTop: 6,
    fontWeight: 600,
  },
  fieldLabel: {
    fontSize: 12,
    color: "#7A7A7A",
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    fontWeight: 700,
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
  textarea: {
    width: "100%",
    minHeight: 72,
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    padding: 10,
    fontSize: 13,
    outline: "none",
    resize: "vertical",
  },
  twoCol: {
    display: "grid",
    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
    gap: 10,
  },
  servicesRow: {
    display: "flex",
    alignItems: "center",
    gap: 8,
    flexWrap: "wrap",
  },
  serviceOn: {
    border: "none",
    height: 30,
    borderRadius: 6,
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 700,
    padding: "0 10px",
    cursor: "pointer",
  },
  serviceOff: {
    border: "1px solid #D3D3D3",
    height: 30,
    borderRadius: 6,
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    padding: "0 10px",
    cursor: "pointer",
  },
  dayRow: {
    display: "grid",
    gridTemplateColumns: "80px 95px 26px 95px 28px",
    alignItems: "center",
    gap: 6,
    marginBottom: 8,
  },
  dayText: {
    fontSize: 13,
    fontWeight: 700,
    textTransform: "capitalize",
  },
  dayHint: {
    fontSize: 13,
    color: "#777777",
    fontWeight: 600,
    textDecoration: "underline",
    cursor: "pointer",
  },
  uploadBox: {
    width: "100%",
    minHeight: 64,
    borderRadius: 6,
    border: "1px dashed #D3D3D3",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: "#FFF8F0",
    fontSize: 12,
    color: "#7A7A7A",
    fontWeight: 600,
    textAlign: "center",
    padding: 8,
  },
  actionsRow: {
    marginTop: 16,
    display: "flex",
    gap: 8,
  },
  buttonPrimary: {
    height: 42,
    borderRadius: 8,
    border: "2px solid #FFD8B9",
    backgroundColor: "#FFD8B9",
    color: "#333333",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    padding: "0 14px",
    cursor: "pointer",
  },
  buttonDark: {
    height: 42,
    borderRadius: 8,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    padding: "0 14px",
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
};

function emptySchedule() {
  return DAYS.reduce((acc, d) => {
    acc[d] = { startTime: "", endTime: "" };
    return acc;
  }, {});
}

async function fetchProfileWithFallback(pathSuffix, options) {
  let lastResponse = null;
  for (const basePath of PROFILE_BASE_PATHS) {
    const res = await fetch(`${API_BASE}${basePath}${pathSuffix}`, options);
    if (res.status !== 404) {
      return res;
    }
    lastResponse = res;
  }
  return lastResponse;
}

export default function PetSitterProfilePage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [user, setUser] = useState(null);
  const [profilePhotoUrl, setProfilePhotoUrl] = useState("");
  const [bio, setBio] = useState("");
  const [experience, setExperience] = useState("");
  const [location, setLocation] = useState("");
  const [hourlyRate, setHourlyRate] = useState("");
  const [servicesOffered, setServicesOffered] = useState(["walk", "feeding", "overnight"]);
  const [availabilitySchedule, setAvailabilitySchedule] = useState(emptySchedule());
  const [studentId, setStudentId] = useState("");
  const [referenceContact, setReferenceContact] = useState("");
  const [verificationDocumentUrl, setVerificationDocumentUrl] = useState("");

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

    const profileRes = await fetchProfileWithFallback("", { headers });
    if (!profileRes.ok) throw new Error("Failed to load sitter profile");

    const data = await profileRes.json();
    setProfilePhotoUrl(data.profilePhotoUrl || "");
    setBio(data.bio || "");
    setExperience(data.experience || "");
    setLocation(data.location || me.address || "");
    setHourlyRate(data.hourlyRate !== null && data.hourlyRate !== undefined ? String(data.hourlyRate) : "");
    setServicesOffered(Array.isArray(data.servicesOffered) ? data.servicesOffered : []);
    setAvailabilitySchedule({ ...emptySchedule(), ...(data.availabilitySchedule || {}) });
    setStudentId(data.studentId || "");
    setReferenceContact(data.referenceContact || "");
    setVerificationDocumentUrl(data.verificationDocumentUrl || "");
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

  const toggleService = (service) => {
    setServicesOffered((prev) => (prev.includes(service) ? prev.filter((s) => s !== service) : [...prev, service]));
  };

  const updateDay = (day, key, value) => {
    setAvailabilitySchedule((prev) => ({
      ...prev,
      [day]: {
        ...(prev[day] || { startTime: "", endTime: "" }),
        [key]: value,
      },
    }));
  };

  const clearDay = (day) => {
    setAvailabilitySchedule((prev) => ({
      ...prev,
      [day]: { startTime: "", endTime: "" },
    }));
  };

  const quickAddHours = (day) => {
    setAvailabilitySchedule((prev) => ({
      ...prev,
      [day]: { startTime: "09:00", endTime: "17:00" },
    }));
  };

  const handleSaveProfile = async () => {
    if (!token) {
      router.replace("/login");
      return;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const res = await fetchProfileWithFallback("", {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          profilePhotoUrl: profilePhotoUrl.trim() || null,
          bio: bio.trim() || null,
          experience: experience.trim() || null,
          hourlyRate: hourlyRate ? Number(hourlyRate) : null,
          servicesOffered,
          availabilitySchedule,
          location: location.trim() || null,
          studentId: studentId.trim() || null,
          referenceContact: referenceContact.trim() || null,
          verificationDocumentUrl: verificationDocumentUrl.trim() || null,
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to save profile");
      }

      setSuccess("Profile saved successfully");
      await loadData(token);
    } catch (e) {
      setError(e?.message || "Failed to save profile");
    } finally {
      setSaving(false);
    }
  };

  const handleSubmitVerification = async () => {
    if (!token) {
      router.replace("/login");
      return;
    }

    setSaving(true);
    setError("");
    setSuccess("");

    try {
      const res = await fetchProfileWithFallback("/submit-verification", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to submit verification");
      }

      setSuccess("Verification submitted successfully");
      await loadData(token);
    } catch (e) {
      setError(e?.message || "Failed to submit verification");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petsitter/dashboard")}>Dashboard</span>
          <span style={styles.navItemActive}>My Profile</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/requests")}>Requests</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/messages")}>Messages</span>
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
        <h1 style={styles.title}>My Sitter Profile</h1>

        {error && <div style={styles.errorBox}>{error}</div>}
        {success && <div style={styles.successBox}>{success}</div>}

        <section style={styles.panel}>
          <div style={styles.photoRow}>
            <div style={styles.photoCircle}>Photo</div>
            <div>
              <button type="button" style={styles.buttonPrimary} disabled={saving}>Upload New Photo</button>
              <div style={styles.helperText}>Max 5MB . JPG, PNG</div>
            </div>
          </div>
          <div style={{ marginTop: 10 }}>
            <div style={styles.fieldLabel}>Photo URL</div>
            <input style={styles.input} value={profilePhotoUrl} onChange={(e) => setProfilePhotoUrl(e.target.value)} placeholder="https://..." disabled={saving} />
          </div>
        </section>

        <section style={styles.panel}>
          <div style={styles.sectionLabel}>Profile Info</div>
          <div style={styles.fieldLabel}>Bio</div>
          <textarea style={styles.textarea} value={bio} onChange={(e) => setBio(e.target.value)} disabled={saving} placeholder="Tell owners about your pet care experience..." />

          <div style={{ ...styles.twoCol, marginTop: 10 }}>
            <div>
              <div style={styles.fieldLabel}>Experience</div>
              <input style={styles.input} value={experience} onChange={(e) => setExperience(e.target.value)} disabled={saving} placeholder="e.g. 2 years pet sitting" />
            </div>
            <div>
              <div style={styles.fieldLabel}>Hourly Rate (PHP)</div>
              <input style={styles.input} type="number" min="0" step="0.01" value={hourlyRate} onChange={(e) => setHourlyRate(e.target.value)} disabled={saving} placeholder="150.00" />
            </div>
          </div>
          <div style={{ marginTop: 10 }}>
            <div style={styles.fieldLabel}>Location</div>
            <input style={styles.input} value={location} onChange={(e) => setLocation(e.target.value)} disabled={saving} placeholder="City or neighborhood" />
          </div>
        </section>

        <section style={styles.panel}>
          <div style={styles.sectionLabel}>Services Offered</div>
          <div style={styles.servicesRow}>
            {SERVICE_OPTIONS.map((service) => {
              const enabled = servicesOffered.includes(service);
              return (
                <button
                  key={service}
                  type="button"
                  style={enabled ? styles.serviceOn : styles.serviceOff}
                  onClick={() => toggleService(service)}
                  disabled={saving}
                >
                  {enabled ? "✓ " : ""}{service.charAt(0).toUpperCase() + service.slice(1)}
                </button>
              );
            })}
          </div>
        </section>

        <section style={styles.panel}>
          <div style={styles.sectionLabel}>Availability Schedule</div>
          {DAYS.map((day) => {
            const row = availabilitySchedule[day] || { startTime: "", endTime: "" };
            const hasHours = row.startTime || row.endTime;
            return (
              <div key={day} style={styles.dayRow}>
                <div style={styles.dayText}>{day}</div>
                {hasHours ? (
                  <>
                    <input type="time" style={styles.input} value={row.startTime || ""} onChange={(e) => updateDay(day, "startTime", e.target.value)} disabled={saving} />
                    <div style={{ textAlign: "center" }}>to</div>
                    <input type="time" style={styles.input} value={row.endTime || ""} onChange={(e) => updateDay(day, "endTime", e.target.value)} disabled={saving} />
                    <button type="button" style={styles.buttonPrimary} onClick={() => clearDay(day)} disabled={saving}>x</button>
                  </>
                ) : (
                  <>
                    <span style={styles.dayHint} onClick={() => quickAddHours(day)}>+ Add hours</span>
                    <div />
                    <div />
                    <div />
                  </>
                )}
              </div>
            );
          })}
        </section>

        <section style={styles.panel}>
          <div style={styles.sectionLabel}>Verification</div>
          <div style={styles.twoCol}>
            <div>
              <div style={styles.fieldLabel}>Student ID</div>
              <input style={styles.input} value={studentId} onChange={(e) => setStudentId(e.target.value)} disabled={saving} placeholder="e.g. 2021-12345" />
            </div>
            <div>
              <div style={styles.fieldLabel}>Reference Contact</div>
              <input style={styles.input} value={referenceContact} onChange={(e) => setReferenceContact(e.target.value)} disabled={saving} placeholder="prof@university.edu" />
            </div>
          </div>

          <div style={{ marginTop: 10 }}>
            <div style={styles.fieldLabel}>Upload Documents</div>
            <div style={styles.uploadBox}>Drop files here or click to upload</div>
          </div>

          <div style={{ marginTop: 10 }}>
            <div style={styles.fieldLabel}>Document URL (optional)</div>
            <input style={styles.input} value={verificationDocumentUrl} onChange={(e) => setVerificationDocumentUrl(e.target.value)} disabled={saving} placeholder="https://..." />
          </div>
        </section>

        <div style={styles.actionsRow}>
          <button type="button" style={styles.buttonDark} onClick={handleSaveProfile} disabled={saving || loading}>
            Save Profile
          </button>
          <button type="button" style={styles.buttonPrimary} onClick={handleSubmitVerification} disabled={saving || loading}>
            Submit for Verification
          </button>
          <span style={{ alignSelf: "center", fontSize: 12, color: "#777777", fontWeight: 700 }}>
            Status: {user?.isVerified ? "Verified" : "Pending"}
          </span>
        </div>
      </main>
    </div>
  );
}
