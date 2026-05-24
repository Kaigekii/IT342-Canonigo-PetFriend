"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { supabase } from "@/shared/utils/supabaseClient";

export default function AuthCallbackPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [status, setStatus] = useState("Processing your Google sign-in...");
  const [error, setError] = useState("");

  useEffect(() => {
    const handleCallback = async () => {
      try {
        // Get the session Supabase set after the OAuth redirect
        const { data: sessionData, error: sessionError } =
          await supabase.auth.getSession();

        if (sessionError || !sessionData?.session) {
          throw new Error("Could not retrieve Google session. Please try again.");
        }

        const accessToken = sessionData.session.access_token;
        // role is only present when coming from a register page
        const role = searchParams.get("role") || null;

        setStatus(role ? `Setting up your ${role.replace("_", " ")} account...` : "Signing you in...");

        // Exchange the Supabase token for a PetFriend JWT
        const res = await fetch("http://localhost:8080/api/auth/google", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ token: accessToken, role }),
        });

        if (!res.ok) {
          const msg = await res.text();
          throw new Error(msg || "Authentication failed");
        }

        const data = await res.json();

        // Store our own JWT + user info
        localStorage.setItem("token", data.token);
        localStorage.setItem(
          "user",
          JSON.stringify({
            userId: data.userId,
            role: data.role,
            firstName: data.firstName,
            lastName: data.lastName,
            email: data.email,
          })
        );

        // Redirect to the correct dashboard
        const roleRoutes = {
          PET_OWNER: "/petowner/dashboard",
          PET_SITTER: "/petsitter/dashboard",
          ADMIN: "/admin/dashboard",
        };
        const redirectPath = roleRoutes[data.role] || "/dashboard";
        router.push(redirectPath);
      } catch (err) {
        setError(err.message || "Something went wrong. Please try again.");
      }
    };

    handleCallback();
  }, [router, searchParams]);

  const styles = {
    container: {
      minHeight: "100vh",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "#FFF8F0",
      flexDirection: "column",
      gap: "16px",
      padding: "32px 16px",
    },
    card: {
      width: "100%",
      maxWidth: "420px",
      backgroundColor: "#fff",
      borderRadius: "16px",
      padding: "40px 32px",
      boxShadow: "0px 4px 24px rgba(0,0,0,0.08)",
      border: "1px solid #E8E8E8",
      textAlign: "center",
    },
    spinner: {
      width: "48px",
      height: "48px",
      border: "4px solid #E8E8E8",
      borderTop: "4px solid #B6E5D8",
      borderRadius: "50%",
      animation: "spin 0.8s linear infinite",
      margin: "0 auto 24px",
    },
    title: {
      fontSize: "18px",
      fontWeight: 600,
      color: "#333333",
      marginBottom: "8px",
    },
    subtitle: {
      fontSize: "14px",
      color: "#999999",
    },
    errorBox: {
      padding: "14px 16px",
      borderRadius: "10px",
      backgroundColor: "#FFEEEE",
      border: "1px solid #FFCCCC",
      color: "#CC0000",
      fontSize: "14px",
      marginTop: "16px",
    },
    backBtn: {
      marginTop: "20px",
      padding: "10px 24px",
      backgroundColor: "#B6E5D8",
      border: "none",
      borderRadius: "10px",
      fontSize: "14px",
      fontWeight: 600,
      color: "#333333",
      cursor: "pointer",
    },
  };

  return (
    <>
      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
      <div style={styles.container}>
        <div style={styles.card}>
          <img
            src="/petfriend-logo-icon.svg"
            alt="PetFriend"
            style={{ width: 56, height: 56, margin: "0 auto 20px" }}
          />

          {!error ? (
            <>
              <div style={styles.spinner} />
              <p style={styles.title}>Almost there!</p>
              <p style={styles.subtitle}>{status}</p>
            </>
          ) : (
            <>
              <p style={styles.title}>Sign-in failed</p>
              <div style={styles.errorBox}>{error}</div>
              <button style={styles.backBtn} onClick={() => router.push("/login")}>
                Back to Login
              </button>
            </>
          )}
        </div>
      </div>
    </>
  );
}
