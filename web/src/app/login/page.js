"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Login failed");
      }

      const data = await res.json();
      localStorage.setItem("token", data.token);
      router.push("/dashboard");
    } catch (err) {
      setError(err.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

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
      maxWidth: "640px",
      backgroundColor: "#FFF8F0",
      borderRadius: "16px",
      padding: "32px",
      boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
      border: "1px solid #D3D3D3",
    },
    header: {
      textAlign: "center",
      marginBottom: "32px",
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
      marginBottom: "24px",
    },
    label: {
      display: "block",
      fontSize: "14px",
      fontWeight: 600,
      color: "#333333",
      marginBottom: "8px",
    },
    input: {
      width: "100%",
      height: "48px",
      padding: "0 16px",
      fontSize: "14px",
      color: "#333333",
      backgroundColor: "#FFF8F0",
      border: "2px solid #D3D3D3",
      borderRadius: "10px",
      outline: "none",
      transition: "border-color 0.2s ease",
    },
    button: {
      width: "100%",
      height: "48px",
      backgroundColor: "#B6E5D8",
      border: "none",
      borderRadius: "12px",
      fontSize: "14px",
      fontWeight: 600,
      color: "#333333",
      cursor: "pointer",
      transition: "opacity 0.15s ease",
      boxShadow: "0px 2px 6px rgba(0,0,0,0.08)",
    },
    buttonDisabled: {
      width: "100%",
      height: "48px",
      backgroundColor: "#D3D3D3",
      border: "none",
      borderRadius: "12px",
      fontSize: "14px",
      fontWeight: 600,
      color: "#999999",
      cursor: "not-allowed",
      boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    },
    footer: {
      textAlign: "center",
      marginTop: "24px",
      fontSize: "13px",
      color: "#D3D3D3",
    },
    link: {
      fontWeight: 600,
      color: "#333333",
      textDecoration: "underline",
      cursor: "pointer",
      background: "none",
      border: "none",
      fontSize: "13px",
      padding: 0,
    },
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <img src="/petfriend-logo-icon.svg" alt="PetFriend Logo" style={{ width: 80, height: 80, margin: "0 auto 16px" }} />
          <h1 style={styles.title}>Welcome Back</h1>
          <p style={styles.subtitle}>Please login to your account</p>
        </div>

        {error && (
          <div style={styles.errorBox}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <div>
            <label style={styles.label}>Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#B6E5D8"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="you@example.com"
              required
            />
          </div>

          <div>
            <label style={styles.label}>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#B6E5D8"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="••••••••"
              required
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={loading ? styles.buttonDisabled : styles.button}
            onMouseEnter={(e) => !loading && (e.target.style.opacity = "0.9")}
            onMouseLeave={(e) => !loading && (e.target.style.opacity = "1")}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </form>

        <div style={styles.footer}>
          Don't have an account?{" "}
          <button
            type="button"
            onClick={() => router.push("/role-selection")}
            style={styles.link}
          >
            Create one
          </button>
        </div>
      </div>
    </div>
  );
}
