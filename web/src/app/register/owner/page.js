"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function OwnerRegisterPage() {
  const router = useRouter();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const validatePassword = (pwd) => {
    const minLength = pwd.length >= 8;
    const hasUpperCase = /[A-Z]/.test(pwd);
    const hasNumber = /[0-9]/.test(pwd);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(pwd);
    
    return { minLength, hasUpperCase, hasNumber, hasSpecialChar, isValid: minLength && hasUpperCase && hasNumber && hasSpecialChar };
  };

  // Real-time validation state
  const passwordValidation = validatePassword(password);
  const passwordsMatch = password && confirmPassword && password === confirmPassword;

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    // Validate password
    const passwordValidation = validatePassword(password);
    if (!passwordValidation.isValid) {
      setError("Password must be at least 8 characters with 1 uppercase letter, 1 number, and 1 special character");
      return;
    }

    // Check if passwords match
    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

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
          role: "PET_OWNER",
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Registration failed");
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
      router.push("/petowner/dashboard");
    } catch (err) {
      setError(err.message || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignIn = async () => {
    const { supabase } = await import("@/shared/utils/supabaseClient");
    const { error } = await supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo: `${window.location.origin}/auth/callback?role=PET_OWNER`,
      },
    });
    if (error) setError(error.message);
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
      backgroundColor: "#FFD8B9",
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
      boxSizing: "border-box",
    },
    button: {
      width: "100%",
      height: "48px",
      backgroundColor: "#FFD8B9",
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
    validationList: {
      fontSize: "12px",
      marginTop: "8px",
      listStyle: "none",
      padding: 0,
    },
    validationItem: {
      display: "flex",
      alignItems: "center",
      gap: "6px",
      marginBottom: "4px",
    },
    validationIcon: {
      fontSize: "14px",
      fontWeight: "bold",
    },
  };

  return (
    <div style={styles.container}>
      <div style={styles.card}>
        <div style={styles.header}>
          <img src="/petfriend-logo-icon.svg" alt="PetFriend Logo" style={{ width: 70, height: 70, margin: "0 auto 16px" }} />
          <h1 style={styles.title}>Create Pet Owner Account</h1>
          <p style={styles.subtitle}>Join as a pet owner</p>
          <span style={styles.badge}>PET OWNER</span>
        </div>

        {error && (
          <div style={styles.errorBox}>
            {error}
          </div>
        )}

        <button
          type="button"
          onClick={handleGoogleSignIn}
          style={styles.googleButton}
          onMouseEnter={(e) => {
            e.currentTarget.style.borderColor = "#FFD8B9";
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
          Continue with Google
        </button>

        <div style={styles.divider}>
          <div style={styles.dividerLine} />
          <span style={styles.dividerText}>or register with email</span>
          <div style={styles.dividerLine} />
        </div>

        <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "16px" }}>
            <div>
              <label style={styles.label}>First Name</label>
              <input
                type="text"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                style={styles.input}
                onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
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
                onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
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
              onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
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
              onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="Create a strong password"
              required
            />
            {password && (
              <ul style={styles.validationList}>
                <li style={styles.validationItem}>
                  <span style={{ ...styles.validationIcon, color: passwordValidation.minLength ? "#B6E5D8" : "#FFCCBC" }}>
                    {passwordValidation.minLength ? "✓" : "✗"}
                  </span>
                  <span style={{ color: passwordValidation.minLength ? "#333333" : "#999999" }}>
                    At least 8 characters
                  </span>
                </li>
                <li style={styles.validationItem}>
                  <span style={{ ...styles.validationIcon, color: passwordValidation.hasUpperCase ? "#B6E5D8" : "#FFCCBC" }}>
                    {passwordValidation.hasUpperCase ? "✓" : "✗"}
                  </span>
                  <span style={{ color: passwordValidation.hasUpperCase ? "#333333" : "#999999" }}>
                    At least 1 uppercase letter
                  </span>
                </li>
                <li style={styles.validationItem}>
                  <span style={{ ...styles.validationIcon, color: passwordValidation.hasNumber ? "#B6E5D8" : "#FFCCBC" }}>
                    {passwordValidation.hasNumber ? "✓" : "✗"}
                  </span>
                  <span style={{ color: passwordValidation.hasNumber ? "#333333" : "#999999" }}>
                    At least 1 number
                  </span>
                </li>
                <li style={styles.validationItem}>
                  <span style={{ ...styles.validationIcon, color: passwordValidation.hasSpecialChar ? "#B6E5D8" : "#FFCCBC" }}>
                    {passwordValidation.hasSpecialChar ? "✓" : "✗"}
                  </span>
                  <span style={{ color: passwordValidation.hasSpecialChar ? "#333333" : "#999999" }}>
                    At least 1 special character
                  </span>
                </li>
              </ul>
            )}
          </div>

          <div>
            <label style={styles.label}>Confirm Password</label>
            <input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
              onBlur={(e) => e.target.style.borderColor = "#D3D3D3"}
              placeholder="Re-enter your password"
              required
            />
            {confirmPassword && (
              <ul style={styles.validationList}>
                <li style={styles.validationItem}>
                  <span style={{ ...styles.validationIcon, color: passwordsMatch ? "#B6E5D8" : "#FFCCBC" }}>
                    {passwordsMatch ? "✓" : "✗"}
                  </span>
                  <span style={{ color: passwordsMatch ? "#333333" : "#999999" }}>
                    Passwords match
                  </span>
                </li>
              </ul>
            )}
          </div>

          <div>
            <label style={{ ...styles.label, color: "#D3D3D3" }}>Phone Number (Optional)</label>
            <input
              type="tel"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              style={styles.input}
              onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
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
              onFocus={(e) => e.target.style.borderColor = "#FFD8B9"}
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
