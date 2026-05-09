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
    background: "none",
    border: "none",
    padding: 0,
    fontSize: 13,
    fontWeight: 600,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
  },
  navItemActive: {
    color: "#333333",
    opacity: 1,
    borderBottom: "2px solid #FFD8B9",
    paddingBottom: 8,
    cursor: "pointer",
    background: "none",
    fontSize: 13,
    fontWeight: 700,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
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
    maxWidth: 1200,
    margin: "0 auto",
    padding: "28px 24px 36px",
  },
  title: {
    fontSize: 38,
    lineHeight: 1.1,
    fontWeight: 800,
    marginBottom: 6,
  },
  subtitle: {
    fontSize: 15,
    color: "#7A7A7A",
    fontWeight: 500,
    marginBottom: 20,
  },
  metricGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(5, minmax(0, 1fr))",
    gap: 14,
    marginBottom: 20,
  },
  metricCard: {
    backgroundColor: "#FFF8F0",
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    minHeight: 92,
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
    padding: "10px 8px",
  },
  metricValue: {
    fontSize: 36,
    lineHeight: 1,
    fontWeight: 800,
    marginBottom: 8,
  },
  metricLabel: {
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    color: "#777777",
  },
  panelGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: 14,
  },
  panelCard: {
    backgroundColor: "#FFF8F0",
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 14,
    minHeight: 300,
  },
  panelTitle: {
    fontSize: 18,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.04em",
    marginBottom: 12,
  },
  activityList: {
    display: "grid",
    gap: 10,
  },
  activityRow: {
    display: "grid",
    gridTemplateColumns: "1fr auto",
    gap: 12,
    paddingBottom: 10,
    borderBottom: "1px solid #D3D3D3",
  },
  activityText: {
    fontSize: 14,
    fontWeight: 600,
    color: "#333333",
    lineHeight: 1.4,
  },
  activityAgo: {
    fontSize: 13,
    fontWeight: 600,
    color: "#888888",
    whiteSpace: "nowrap",
  },
  chartPlaceholder: {
    border: "1px dashed #D3D3D3",
    borderRadius: 8,
    minHeight: 170,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    textAlign: "center",
    color: "#8A8A8A",
    fontWeight: 600,
    fontSize: 14,
    marginBottom: 14,
    backgroundColor: "#FFF8F0",
  },
  revenueRow: {
    display: "grid",
    gridTemplateColumns: "repeat(3, minmax(0, 1fr))",
    gap: 10,
  },
  revenueMetric: {
    textAlign: "center",
    padding: "6px 8px",
  },
  revenueValue: {
    fontSize: 28,
    fontWeight: 800,
    lineHeight: 1,
    marginBottom: 6,
  },
  revenueLabel: {
    fontSize: 12,
    fontWeight: 700,
    color: "#777777",
    letterSpacing: "0.08em",
    textTransform: "uppercase",
  },
  inlineInfo: {
    marginBottom: 12,
    padding: "10px 12px",
    borderRadius: 10,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF9C4",
    color: "#333333",
    fontSize: 13,
    fontWeight: 600,
  },
  errorBox: {
    marginBottom: 12,
    padding: "12px",
    borderRadius: "10px",
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: "14px",
  },
  emptyBox: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    padding: "12px",
    fontSize: "14px",
    color: "#666666",
    fontWeight: 600,
  },
};

const navItems = [
  { key: "dashboard", label: "Dashboard", active: true },
  { key: "pending", label: "Pending Sitters" },
  { key: "users", label: "Users" },
  { key: "bookings", label: "All Bookings" },
];

function formatMoney(amount) {
  const value = Number(amount ?? 0);
  if (!Number.isFinite(value)) {
    return "P0.00";
  }
  return `P${value.toLocaleString("en-PH", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;
}

export default function AdminDashboardPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [isMobile, setIsMobile] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [info, setInfo] = useState("");

  const [dashboard, setDashboard] = useState({
    totalUsers: 0,
    petOwners: 0,
    petSitters: 0,
    pendingApprovals: 0,
    totalBookings: 0,
    monthRevenue: 0,
    platformFees: 0,
    sitterPayouts: 0,
    recentActivity: [],
  });

  const loadData = useCallback(async (authToken) => {
    setLoading(true);
    setError("");
    setInfo("");

    try {
      const meRes = await fetch(`${API_BASE}/api/user/me`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (meRes.status === 401) {
        localStorage.removeItem("token");
        router.replace("/login");
        return;
      }

      if (!meRes.ok) {
        throw new Error("Failed to load profile");
      }

      const meData = await meRes.json();
      if (meData?.role === "PET_OWNER") {
        router.replace("/petowner/dashboard");
        return;
      }
      if (meData?.role === "PET_SITTER") {
        router.replace("/petsitter/dashboard");
        return;
      }
      if (meData?.role !== "ADMIN") {
        throw new Error("Admin access required");
      }

      const adminRes = await fetch(`${API_BASE}/api/admin/dashboard`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (adminRes.status === 403) {
        throw new Error("You are not allowed to access admin dashboard.");
      }

      if (!adminRes.ok) {
        throw new Error("Failed to load admin dashboard data");
      }

      const adminData = await adminRes.json();
      setDashboard({
        totalUsers: adminData?.totalUsers ?? 0,
        petOwners: adminData?.petOwners ?? 0,
        petSitters: adminData?.petSitters ?? 0,
        pendingApprovals: adminData?.pendingApprovals ?? 0,
        totalBookings: adminData?.totalBookings ?? 0,
        monthRevenue: adminData?.monthRevenue ?? 0,
        platformFees: adminData?.platformFees ?? 0,
        sitterPayouts: adminData?.sitterPayouts ?? 0,
        recentActivity: Array.isArray(adminData?.recentActivity) ? adminData.recentActivity : [],
      });
    } catch (err) {
      setError(err?.message || "Unable to load admin dashboard.");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }
    loadData(token);
  }, [loadData, router, token]);

  useEffect(() => {
    const applyScreenMode = () => {
      setIsMobile(window.innerWidth < 960);
    };

    applyScreenMode();
    window.addEventListener("resize", applyScreenMode);
    return () => window.removeEventListener("resize", applyScreenMode);
  }, []);

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

  const handleNavClick = (key) => {
    if (key === "dashboard") {
      return;
    }
    if (key === "pending") {
      router.push("/admin/pending-sitters");
      return;
    }
    if (key === "users") {
      router.push("/admin/users");
      return;
    }
    if (key === "bookings") {
      router.push("/admin/bookings");
      return;
    }
    setInfo("This section is planned next. Dashboard data is already live and connected to backend/database.");
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>

        {!isMobile && (
          <nav style={styles.nav}>
            {navItems.map((item) => (
              <button
                key={item.key}
                type="button"
                style={item.active ? styles.navItemActive : styles.navItem}
                onClick={() => handleNavClick(item.key)}
              >
                {item.label}
              </button>
            ))}
          </nav>
        )}

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Admin</span>
          <button
            type="button"
            onClick={() => setShowProfileMenu((prev) => !prev)}
            style={styles.avatarButton}
            aria-label="Open admin menu"
          />

          {showProfileMenu && (
            <div style={styles.profileMenu}>
              <button type="button" style={styles.menuItem} onClick={() => setInfo("Profile settings will be added soon.")}>
                Profile
              </button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>
                Logout
              </button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>Admin Dashboard</h1>
        <p style={styles.subtitle}>Platform overview and management</p>

        {error ? <div style={styles.errorBox}>{error}</div> : null}
        {info ? <div style={styles.inlineInfo}>{info}</div> : null}

        <section
          style={{
            ...styles.metricGrid,
            gridTemplateColumns: isMobile ? "repeat(2, minmax(0, 1fr))" : styles.metricGrid.gridTemplateColumns,
          }}
        >
          <article style={styles.metricCard}>
            <div style={styles.metricValue}>{loading ? "..." : dashboard.totalUsers}</div>
            <div style={styles.metricLabel}>Total Users</div>
          </article>
          <article style={styles.metricCard}>
            <div style={styles.metricValue}>{loading ? "..." : dashboard.petOwners}</div>
            <div style={styles.metricLabel}>Pet Owners</div>
          </article>
          <article style={styles.metricCard}>
            <div style={styles.metricValue}>{loading ? "..." : dashboard.petSitters}</div>
            <div style={styles.metricLabel}>Pet Sitters</div>
          </article>
          <article style={styles.metricCard}>
            <div style={styles.metricValue}>{loading ? "..." : dashboard.pendingApprovals}</div>
            <div style={styles.metricLabel}>Pending Approvals</div>
          </article>
          <article style={styles.metricCard}>
            <div style={styles.metricValue}>{loading ? "..." : dashboard.totalBookings}</div>
            <div style={styles.metricLabel}>Total Bookings</div>
          </article>
        </section>

        <section
          style={{
            ...styles.panelGrid,
            gridTemplateColumns: isMobile ? "1fr" : styles.panelGrid.gridTemplateColumns,
          }}
        >
          <article style={styles.panelCard}>
            <h2 style={styles.panelTitle}>Recent Activity</h2>

            {dashboard.recentActivity.length === 0 ? (
              <div style={styles.emptyBox}>No recent activity yet.</div>
            ) : (
              <div style={styles.activityList}>
                {dashboard.recentActivity.map((item, index) => (
                  <div
                    key={`${item.type}-${index}`}
                    style={{
                      ...styles.activityRow,
                      borderBottom:
                        index === dashboard.recentActivity.length - 1 ? "none" : styles.activityRow.borderBottom,
                      paddingBottom: index === dashboard.recentActivity.length - 1 ? 0 : styles.activityRow.paddingBottom,
                    }}
                  >
                    <span style={styles.activityText}>{item.message}</span>
                    <span style={styles.activityAgo}>{item.ago}</span>
                  </div>
                ))}
              </div>
            )}
          </article>

          <article style={styles.panelCard}>
            <h2 style={styles.panelTitle}>Revenue (Sandbox)</h2>
            <div style={styles.chartPlaceholder}>[Bar Chart Placeholder]<br />Monthly booking revenue</div>

            <div
              style={{
                ...styles.revenueRow,
                gridTemplateColumns: isMobile ? "1fr" : styles.revenueRow.gridTemplateColumns,
              }}
            >
              <div style={styles.revenueMetric}>
                <div style={styles.revenueValue}>{loading ? "..." : formatMoney(dashboard.monthRevenue)}</div>
                <div style={styles.revenueLabel}>This Month</div>
              </div>
              <div style={styles.revenueMetric}>
                <div style={styles.revenueValue}>{loading ? "..." : formatMoney(dashboard.platformFees)}</div>
                <div style={styles.revenueLabel}>Platform Fees</div>
              </div>
              <div style={styles.revenueMetric}>
                <div style={styles.revenueValue}>{loading ? "..." : formatMoney(dashboard.sitterPayouts)}</div>
                <div style={styles.revenueLabel}>Sitter Payouts</div>
              </div>
            </div>
          </article>
        </section>
      </main>
    </div>
  );
}