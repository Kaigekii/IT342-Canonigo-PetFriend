"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function SitterRegisterPage() {
  const router = useRouter();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          firstName,
          lastName,
          email,
          password,
          phoneNumber: phoneNumber || null,
          address: address || null,
          role: "PET_SITTER",
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Registration failed");
      }

      const data = await res.json();
      localStorage.setItem("token", data.token);
      router.push("/dashboard");
    } catch (err) {
      setError(err.message || "Registration failed");
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
    badge: {
      display: "inline-block",
      padding: "4px 12px",
      borderRadius: "12px",
      backgroundColor: "#FFB6C1",
      color: "#333333",
      fontSize: "12px",
      fontWeight: 600,
      marginTop: "8px",
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
      backgroundColor: "#FFB6C1",
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
          <h1 style={styles.title}>Create Pet Sitter Account</h1>
          <p style={styles.subtitle}>Join as a pet sitter</p>
          <span style={styles.badge}>PET SITTER</span>
        </div>

        {error && (
          <div style={styles.errorBox}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
            <div>
              <label style={styles.label}>First Name</label>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                style={styles.input}
                onFocus={(e) => e.target.style.borderColor = "#FFB6C1"}
                onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
                placeholder="John"
                required
              />
            </div>
            <div>
              <label style={styles.label}>Last Name</label>
              <input
                type="text"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                style={styles.input}
                onFocus={(e) => e.target.style.borderColor = "#FFB6C1"}
                onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
                placeholder="Doe"
                required
              />
            </div>
          </div>

          <div>
            <label style={styles.label}>Email Address</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#FFB6C1"}
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
              onFocus={(e) => e.target.style.borderColor = "#FFB6C1"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="At least 6 characters"
              minLength={6}
              required
            />
          </div>

          <div>
            <label style={{ ...styles.label, color: "#D3D3D3" }}>Phone Number (Optional)</label>
            <input
              type="tel"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#FFB6C1"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="+1 234 567 8900"
            />
          </div>

          <div>
            <label style={{ ...styles.label, color: "#D3D3D3" }}>Address (Optional)</label>
            <input
              type="text"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#FFB6C1"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="123 Main Street"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            style={loading ? styles.buttonDisabled : styles.button}
            onMouseEnter={(e) => !loading && (e.currentTarget.style.opacity = "0.85")}
            onMouseLeave={(e) => !loading && (e.currentTarget.style.opacity = "1")}
          >
            {loading ? "Creating Account..." : "Create Account"}
          </button>
        </form>

        <div style={styles.footer}>
          Already have an account?{" "}
          <button style={styles.link} onClick={() => router.push("/login")}>
            Sign in
          </button>
        </div>
      </div>
    </div>
  );
}
