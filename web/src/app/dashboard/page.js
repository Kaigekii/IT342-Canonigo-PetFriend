"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function DashboardPage() {
  const router = useRouter();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const token = typeof window !== "undefined" ? localStorage.getItem("token") : null;
    if (!token) {
      router.replace("/login");
      return;
    }

    const fetchMe = async () => {
      try {
        const res = await fetch(`${API_BASE}/api/user/me`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
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

        if (data?.role === "PET_OWNER") {
          router.replace("/petowner/dashboard");
          return;
        }

        if (data?.role === "PET_SITTER") {
          router.replace("/petsitter/dashboard");
          return;
        }

        if (data?.role === "ADMIN") {
          router.replace("/admin/dashboard");
          return;
        }

        setUser(data);
      } catch (err) {
        setError(err.message || "Failed to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchMe();
  }, [router]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.replace("/login");
  };

  if (loading) {
    return (
      <div style={styles.container}>
        <div style={styles.card}>
          <p style={styles.subtitle}>Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <h1 style={styles.title}>Dashboard</h1>
          <p style={styles.subtitle}>{user ? `Welcome back, ${user.firstName}!` : ""}</p>
        </div>

        {error && <div style={styles.errorBox}>{error}</div>}

        {user ? (
          <div>
            <div style={styles.profileGrid}>
              <div>
                <div style={styles.label}>FIRST NAME</div>
                <div style={styles.value}>{user.firstName}</div>
              </div>
              <div>
                <div style={styles.label}>LAST NAME</div>
                <div style={styles.value}>{user.lastName}</div>
              </div>
              <div style={{ gridColumn: "1 / -1" }}>
                <div style={styles.label}>EMAIL</div>
                <div style={styles.value}>{user.email}</div>
              </div>
              <div>
                <div style={styles.label}>ROLE</div>
                <div style={styles.value}>{user.role}</div>
              </div>
              <div>
                <div style={styles.label}>VERIFIED</div>
                <div style={styles.value}>{String(user.isVerified ?? "N/A")}</div>
              </div>
              <div style={{ gridColumn: "1 / -1" }}>
                <div style={styles.label}>PHONE</div>
                <div style={styles.value}>{user.phoneNumber || "—"}</div>
              </div>
              <div style={{ gridColumn: "1 / -1" }}>
                <div style={styles.label}>ADDRESS</div>
                <div style={styles.value}>{user.address || "—"}</div>
              </div>
            </div>

            <div style={{ marginTop: 24, display: "flex", justifyContent: "flex-end" }}>
              <button type="button" onClick={handleLogout} style={styles.button}>
                Logout
              </button>
            </div>
          </div>
        ) : (
          <p style={styles.subtitle}>No user data available.</p>
        )}
      </div>
    </div>
  );
}

const styles = {
  container: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: "32px 16px",
    backgroundColor: "#FFF8F0",
  },
  card: {
    width: "100%",
    maxWidth: "760px",
    backgroundColor: "#FFF8F0",
    borderRadius: "16px",
    padding: "32px",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    border: "1px solid #D3D3D3",
  },
  header: {
    textAlign: "center",
    marginBottom: "24px",
  },
  title: {
    fontSize: "20px",
    fontWeight: 600,
    color: "#333333",
    marginBottom: "8px",
  },
  subtitle: {
    fontSize: "14px",
    fontWeight: 400,
    color: "#D3D3D3",
  },
  errorBox: {
    padding: "12px",
    borderRadius: "10px",
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: "14px",
    marginBottom: "16px",
  },
  profileGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
    gap: "16px",
  },
  label: {
    fontSize: "12px",
    fontWeight: 600,
    color: "#333333",
    opacity: 0.65,
    marginBottom: "6px",
    letterSpacing: "0.08em",
  },
  value: {
    fontSize: "14px",
    fontWeight: 600,
    color: "#333333",
    lineHeight: 1.5,
  },
  button: {
    minHeight: 44,
    padding: "12px 16px",
    borderRadius: 12,
    backgroundColor: "#FFD8B9",
    border: "2px solid #FFD8B9",
    color: "#333333",
    fontSize: "14px",
    fontWeight: 700,
    cursor: "pointer",
  },
};
