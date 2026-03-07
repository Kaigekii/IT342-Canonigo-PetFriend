"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

/* ── Paw SVG icon ─────────────────────────────────────────── */
function PawIcon({ size = 56, color = "#333333" }) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 64 64"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      aria-hidden="true"
    >
      {/* toe beans */}
      <ellipse cx="14" cy="18" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="28" cy="11" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="42" cy="11" rx="6" ry="8" fill={color} opacity="0.85" />
      <ellipse cx="54" cy="18" rx="6" ry="8" fill={color} opacity="0.85" />
      {/* main pad */}
      <ellipse cx="33" cy="40" rx="16" ry="14" fill={color} />
    </svg>
  );
}

/* ── Styles (design-system tokens as JS objects) ──────────── */
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
    zIndex: 9999,
    padding: "16px",
  },
  logoCircle: {
    width: 120,
    height: 120,
    borderRadius: "50%",
    backgroundColor: "#FFD8B9",   /* Soft Peach */
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    boxShadow: "0px 4px 16px rgba(255,180,120,0.35)",
  },
  appName: {
    fontSize: "20px",
    fontWeight: 600,
    color: "#333333",             /* Charcoal Gray */
    letterSpacing: "0.04em",
    lineHeight: 1.5,
    textAlign: "center",
  },
  tagline: {
    fontSize: "12px",
    fontWeight: 400,
    color: "#D3D3D3",             /* Light Gray */
    lineHeight: 1.5,
    marginTop: "-16px",
    textAlign: "center",
  },
  ctaButton: {
    height: "48px",
    width: "280px",
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
  },
  signInRow: {
    fontSize: "13px",
    fontWeight: 400,
    color: "#D3D3D3",             /* Light Gray */
    marginTop: "-8px",
  },
  signInLink: {
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

/* ── Component ────────────────────────────────────────────── */
export default function SplashPage() {
  const router = useRouter();

  /* If user already has a session, skip splash and go to dashboard */
  useEffect(() => {
    const token =
      typeof window !== "undefined" ? localStorage.getItem("token") : null;
    if (token) router.replace("/dashboard");
  }, [router]);

  const handleGetStarted = () => router.push("/role-selection");
  const handleSignIn     = () => router.push("/login");

  return (
    <div style={styles.wrapper} className="splash-fade-in">
      {/* Logo circle */}
      <div style={styles.logoCircle} className="splash-pulse">
        <PawIcon size={56} color="#333333" />
      </div>

      {/* App name */}
      <span style={styles.appName}>PetFriend</span>

      {/* Tagline */}
      <span style={styles.tagline}>Your trusted companion for pet care</span>

      {/* Primary CTA */}
      <button
        style={styles.ctaButton}
        onClick={handleGetStarted}
        onMouseEnter={e => (e.currentTarget.style.opacity = "0.85")}
        onMouseLeave={e => (e.currentTarget.style.opacity = "1")}
      >
        Let&apos;s Get Started
      </button>

      {/* Secondary sign-in prompt */}
      <p style={styles.signInRow}>
        Already have an account?{" "}
        <button style={styles.signInLink} onClick={handleSignIn}>
          Sign in
        </button>
      </p>
    </div>
  );
}

