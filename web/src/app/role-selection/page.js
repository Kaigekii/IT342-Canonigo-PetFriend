"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

/* ── Paw SVG icon ───────────────────────────────────────────────── */
function PawIcon({ size = 80, color = "#333333" }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 64 64"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      <ellipse cx="14" cy="18" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="28" cy="11" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="42" cy="11" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="54" cy="18" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="33" cy="40" rx="16" ry="14" fill={color} />
    </svg>
  );
}

/* ── Styles (design-system tokens) ─────────────────────────────── */
const styles = {
  wrapper: {
    position: "fixed",
    inset: 0,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    gap: "24px",
    backgroundColor: "#FFF8F0",   /* Cream White */
    padding: "16px",
  },
  logoCircle: {
    width: 100,
    height: 100,
    borderRadius: "50%",
    backgroundColor: "#FFD8B9",   /* Soft Peach */
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    boxShadow: "0px 4px 16px rgba(255,180,120,0.35)",
    marginBottom: "8px",
  },
  heading: {
    fontSize: "20px",
    fontWeight: 600,
    color: "#333333",             /* Charcoal Gray */
    letterSpacing: "0.04em",
    lineHeight: 1.5,
    textAlign: "center",
    marginBottom: "8px",
  },
  subheading: {
    fontSize: "12px",
    fontWeight: 400,
    color: "#D3D3D3",             /* Light Gray */
    lineHeight: 1.5,
    textAlign: "center",
    marginBottom: "16px",
  },
  rolesCard: {
    backgroundColor: "#D3D3D3",   /* Light Gray background */
    borderRadius: "16px",
    padding: "16px",
    width: "100%",
    maxWidth: "420px",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
  },
  roleOption: {
    backgroundColor: "#FFF8F0",   /* Cream White */
    borderRadius: "16px",
    padding: "16px 20px",
    marginBottom: "12px",
    display: "flex",
    alignItems: "center",
    gap: "16px",
    cursor: "pointer",
    border: "2px solid transparent",
    transition: "all 0.2s ease",
  },
  roleOptionSelected: {
    backgroundColor: "#FFF8F0",
    borderRadius: "16px",
    padding: "16px 20px",
    marginBottom: "12px",
    display: "flex",
    alignItems: "center",
    gap: "16px",
    cursor: "pointer",
    border: "2px solid #FFD8B9",  /* Soft Peach border when selected */
    transition: "all 0.2s ease",
    boxShadow: "0px 2px 8px rgba(255,216,185,0.4)",
  },
  radioCircle: {
    width: "20px",
    height: "20px",
    borderRadius: "50%",
    border: "2px solid #D3D3D3",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  radioCircleSelected: {
    width: "20px",
    height: "20px",
    borderRadius: "50%",
    border: "2px solid #FFD8B9",
    backgroundColor: "#FFD8B9",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    flexShrink: 0,
  },
  radioInner: {
    width: "10px",
    height: "10px",
    borderRadius: "50%",
    backgroundColor: "#333333",
  },
  roleLabel: {
    fontSize: "14px",
    fontWeight: 600,
    color: "#333333",
    flex: 1,
  },
  continueButton: {
    height: "48px",
    width: "100%",
    maxWidth: "420px",
    borderRadius: "12px",
    backgroundColor: "#FFD8B9",   /* Soft Peach */
    border: "none",
    cursor: "pointer",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.08)",
    fontSize: "14px",
    fontWeight: 600,
    color: "#333333",
    letterSpacing: "0.03em",
    transition: "opacity 0.15s ease",
    marginTop: "8px",
  },
  continueButtonDisabled: {
    height: "48px",
    width: "100%",
    maxWidth: "420px",
    borderRadius: "12px",
    backgroundColor: "#D3D3D3",
    border: "none",
    cursor: "not-allowed",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    fontSize: "14px",
    fontWeight: 600,
    color: "#999999",
    letterSpacing: "0.03em",
    marginTop: "8px",
  },
};

/* ── Component ──────────────────────────────────────────────────── */
export default function RoleSelectionPage() {
  const router = useRouter();
  const [selectedRole, setSelectedRole] = useState(null);

  const handleContinue = () => {
    if (!selectedRole) return;
    
    if (selectedRole === "owner") {
      router.push("/register/owner");
    } else {
      router.push("/register/sitter");
    }
  };

  return (
    <div style={styles.wrapper} className="splash-fade-in">
      {/* Logo */}
      <div style={styles.logoCircle}>
        <img src="/petfriend-logo-icon.svg" alt="PetFriend Logo" style={{ width: 48, height: 48 }} />
      </div>

      {/* Heading */}
      <h1 style={styles.heading}>CREATE ACCOUNT</h1>

      {/* Subheading */}
      <p style={styles.subheading}>Choose your role to get started</p>

      {/* Roles card */}
      <div style={styles.rolesCard}>
        {/* Pet Owner option */}
        <div
          style={selectedRole === "owner" ? styles.roleOptionSelected : styles.roleOption}
          onClick={() => setSelectedRole("owner")}
        >
          <div style={selectedRole === "owner" ? styles.radioCircleSelected : styles.radioCircle}>
            {selectedRole === "owner" && <div style={styles.radioInner} />}
          </div>
          <span style={styles.roleLabel}>Pet Owner</span>
        </div>

        {/* Pet Sitter option */}
        <div
          style={selectedRole === "sitter" ? styles.roleOptionSelected : styles.roleOption}
          onClick={() => setSelectedRole("sitter")}
        >
          <div style={selectedRole === "sitter" ? styles.radioCircleSelected : styles.radioCircle}>
            {selectedRole === "sitter" && <div style={styles.radioInner} />}
          </div>
          <span style={styles.roleLabel}>Pet Sitter</span>
        </div>
      </div>

      {/* Continue button */}
      <button
        style={selectedRole ? styles.continueButton : styles.continueButtonDisabled}
        onClick={handleContinue}
        disabled={!selectedRole}
        onMouseEnter={(e) => selectedRole && (e.currentTarget.style.opacity = "0.85")}
        onMouseLeave={(e) => selectedRole && (e.currentTarget.style.opacity = "1")}
      >
        Continue
      </button>
    </div>
  );
}
