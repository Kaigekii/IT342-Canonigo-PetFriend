"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleGoogleSignIn = async () => {
    const { supabase } = await import("@/shared/utils/supabaseClient");
    const { error } = await supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo: `${window.location.origin}/auth/callback`,
      },
    });
    if (error) setError(error.message);
  };

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
      localStorage.setItem("user", JSON.stringify({
        userId: data.userId,
        role: data.role,
        firstName: data.firstName,
        lastName: data.lastName,
        email: data.email,
      }));

      // Redirect based on user role
      const roleRoutes = {
        PET_OWNER: "/petowner/dashboard",
        PET_SITTER: "/petsitter/dashboard",
        ADMIN: "/admin/dashboard",
      };
      const redirectPath = roleRoutes[data.role] || "/dashboard";
      router.push(redirectPath);
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
    googleButton: {
      width: "100%",
      height: "48px",
      backgroundColor: "#ffffff",
      border: "2px solid #D3D3D3",
      borderRadius: "12px",
      fontSize: "14px",
      fontWeight: 600,
      color: "#333333",
      cursor: "pointer",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      gap: "10px",
      transition: "border-color 0.2s ease, box-shadow 0.2s ease",
      boxShadow: "0px 2px 6px rgba(0,0,0,0.06)",
    },
    divider: {
      display: "flex",
      alignItems: "center",
      gap: "12px",
      margin: "20px 0",
    },
    dividerLine: {
      flex: 1,
      height: "1px",
      backgroundColor: "#D3D3D3",
    },
    dividerText: {
      fontSize: "12px",
      color: "#999999",
      fontWeight: 500,
      whiteSpace: "nowrap",
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

        {/* Divider */}
        <div style={styles.divider}>
          <div style={styles.dividerLine} />
          <span style={styles.dividerText}>or continue with</span>
          <div style={styles.dividerLine} />
        </div>

        {/* Google Sign-In Button */}
        <button
          type="button"
          onClick={handleGoogleSignIn}
          style={styles.googleButton}
          onMouseEnter={(e) => {
            e.currentTarget.style.borderColor = "#B6E5D8";
            e.currentTarget.style.boxShadow = "0px 4px 12px rgba(0,0,0,0.12)";
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.borderColor = "#D3D3D3";
            e.currentTarget.style.boxShadow = "0px 2px 6px rgba(0,0,0,0.06)";
          }}
        >
          <svg width="18" height="18" viewBox="0 0 48 48" fill="none">
            <path d="M44.5 20H24v8.5h11.8C34.7 33.9 30.1 37 24 37c-7.2 0-13-5.8-13-13s5.8-13 13-13c3.1 0 5.9 1.1 8.1 2.9l6.4-6.4C34.6 4.1 29.6 2 24 2 11.8 2 2 11.8 2 24s9.8 22 22 22c11 0 21-8 21-22 0-1.3-.2-2.7-.5-4z" fill="#FFC107"/>
            <path d="M6.3 14.7l7 5.1C15.1 16 19.2 13 24 13c3.1 0 5.9 1.1 8.1 2.9l6.4-6.4C34.6 4.1 29.6 2 24 2 16.3 2 9.7 7.4 6.3 14.7z" fill="#FF3D00"/>
            <path d="M24 46c5.5 0 10.5-1.9 14.3-5.1l-6.6-5.6C29.6 37 26.9 38 24 38c-6.1 0-10.7-3.1-11.8-7.5l-7 5.4C8.8 42.3 15.9 46 24 46z" fill="#4CAF50"/>
            <path d="M44.5 20H24v8.5h11.8c-.8 2.4-2.4 4.4-4.5 5.8l6.6 5.6C41.8 36.9 45 31 45 24c0-1.3-.2-2.7-.5-4z" fill="#1976D2"/>
          </svg>
          Sign in with Google
        </button>

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
