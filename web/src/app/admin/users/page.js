"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const PAGE_SIZE = 4;

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
    fontSize: 32,
    lineHeight: 1.1,
    fontWeight: 800,
    marginBottom: 6,
  },
  subtitle: {
    fontSize: 15,
    color: "#7A7A7A",
    fontWeight: 600,
    marginBottom: 18,
  },
  filtersRow: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 16,
    marginBottom: 16,
    flexWrap: "wrap",
  },
  tabs: {
    display: "flex",
    alignItems: "center",
    gap: 10,
    flexWrap: "wrap",
  },
  tabButton: {
    height: 34,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    letterSpacing: "0.06em",
    textTransform: "uppercase",
    padding: "0 14px",
    cursor: "pointer",
  },
  tabButtonActive: {
    height: 34,
    borderRadius: 8,
    border: "1px solid #1E1E1E",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    letterSpacing: "0.06em",
    textTransform: "uppercase",
    padding: "0 14px",
    cursor: "pointer",
  },
  searchInput: {
    height: 36,
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    padding: "0 12px",
    minWidth: 220,
    fontSize: 13,
    outline: "none",
  },
  table: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    overflow: "hidden",
  },
  tableHeader: {
    display: "grid",
    gridTemplateColumns: "130px 200px 240px 140px 140px 140px 80px",
    padding: "12px 16px",
    backgroundColor: "#F2F2F2",
    borderBottom: "1px solid #D3D3D3",
    fontSize: 12,
    fontWeight: 800,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    color: "#777777",
  },
  tableRow: {
    display: "grid",
    gridTemplateColumns: "130px 200px 240px 140px 140px 140px 80px",
    padding: "14px 16px",
    alignItems: "center",
    borderBottom: "1px solid #D3D3D3",
    fontSize: 13,
  },
  tableRowLast: {
    borderBottom: "none",
  },
  cellStrong: {
    fontWeight: 700,
  },
  statusBadge: {
    fontSize: 11,
    fontWeight: 800,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    padding: "6px 10px",
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    textAlign: "center",
    width: "fit-content",
  },
  statusActive: {
    backgroundColor: "#FFF8F0",
  },
  statusVerified: {
    backgroundColor: "#B6E5D8",
    borderColor: "#B6E5D8",
  },
  statusPending: {
    backgroundColor: "#FFF9C4",
    borderColor: "#FFF9C4",
  },
  linkButton: {
    border: "none",
    backgroundColor: "transparent",
    color: "#333333",
    textDecoration: "underline",
    fontSize: 13,
    fontWeight: 700,
    cursor: "pointer",
    padding: 0,
  },
  pagination: {
    marginTop: 14,
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: 16,
    flexWrap: "wrap",
  },
  pager: {
    display: "flex",
    alignItems: "center",
    gap: 8,
  },
  pagerButton: {
    width: 30,
    height: 30,
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    fontSize: 12,
    fontWeight: 700,
    cursor: "pointer",
  },
  pagerButtonActive: {
    width: 30,
    height: 30,
    borderRadius: 6,
    border: "1px solid #1E1E1E",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    cursor: "pointer",
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
  { key: "dashboard", label: "Dashboard" },
  { key: "pending", label: "Pending Sitters" },
  { key: "users", label: "Users", active: true },
  { key: "bookings", label: "All Bookings" },
];

const filterTabs = [
  { key: "all", label: "All" },
  { key: "owners", label: "Pet Owners" },
  { key: "sitters", label: "Pet Sitters" },
  { key: "pending", label: "Pending" },
];

function formatDate(isoText) {
  if (!isoText) return "-";
  const dt = new Date(isoText);
  if (Number.isNaN(dt.getTime())) return "-";
  return dt.toLocaleDateString("en-US", { month: "short", day: "2-digit", year: "numeric" });
}

function shortUserId(id) {
  if (!id) return "usr_----";
  const cleaned = String(id).replace(/-/g, "");
  return `usr_${cleaned.slice(0, 5)}`;
}

function formatRole(role) {
  if (!role) return "-";
  return role.replace("_", " ");
}

function getStatusLabel(user) {
  if (user.role === "PET_SITTER") {
    return user.verified ? "Verified" : "Pending";
  }
  return "Active";
}

function getStatusStyle(user) {
  if (user.role === "PET_SITTER") {
    return user.verified ? { ...styles.statusBadge, ...styles.statusVerified } : { ...styles.statusBadge, ...styles.statusPending };
  }
  return { ...styles.statusBadge, ...styles.statusActive };
}

export default function AdminUsersPage() {
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
  const [users, setUsers] = useState([]);
  const [activeTab, setActiveTab] = useState("all");
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);

  const loadUsers = useCallback(async (authToken) => {
    setLoading(true);
    setError("");

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

      const res = await fetch(`${API_BASE}/api/admin/users`, {
        headers: { Authorization: `Bearer ${authToken}` },
      });

      if (res.status === 403) {
        throw new Error("You are not allowed to access users.");
      }

      if (!res.ok) {
        throw new Error("Failed to load users");
      }

      const data = await res.json();
      setUsers(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err?.message || "Unable to load users.");
    } finally {
      setLoading(false);
    }
  }, [router]);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }
    loadUsers(token);
  }, [loadUsers, router, token]);

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

  useEffect(() => {
    setPage(1);
  }, [activeTab, search]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.replace("/login");
  };

  const handleNavClick = (key) => {
    if (key === "dashboard") {
      router.push("/admin/dashboard");
      return;
    }
    if (key === "pending") {
      router.push("/admin/pending-sitters");
      return;
    }
    if (key === "users") {
      return;
    }
    if (key === "bookings") {
      router.push("/admin/bookings");
    }
  };

  const filteredUsers = useMemo(() => {
    const query = search.trim().toLowerCase();
    return users.filter((user) => {
      const matchesSearch = !query
        || user.fullName?.toLowerCase().includes(query)
        || user.email?.toLowerCase().includes(query);

      if (!matchesSearch) return false;

      if (activeTab === "owners") return user.role === "PET_OWNER";
      if (activeTab === "sitters") return user.role === "PET_SITTER";
      if (activeTab === "pending") return user.role === "PET_SITTER" && !user.verified;
      return true;
    });
  }, [users, activeTab, search]);

  const totalPages = Math.max(1, Math.ceil(filteredUsers.length / PAGE_SIZE));
  const safePage = Math.min(page, totalPages);
  const startIndex = (safePage - 1) * PAGE_SIZE;
  const pageItems = filteredUsers.slice(startIndex, startIndex + PAGE_SIZE);

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
              <button type="button" style={styles.menuItem} onClick={() => router.push("/admin/dashboard")}>Dashboard</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>User Management</h1>
        <div style={styles.subtitle}>Manage and review platform users.</div>

        {error && <div style={styles.errorBox}>{error}</div>}

        <div style={styles.filtersRow}>
          <div style={styles.tabs}>
            {filterTabs.map((tab) => (
              <button
                key={tab.key}
                type="button"
                style={activeTab === tab.key ? styles.tabButtonActive : styles.tabButton}
                onClick={() => setActiveTab(tab.key)}
              >
                {tab.label}
              </button>
            ))}
          </div>
          <input
            type="text"
            placeholder="Search users..."
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            style={styles.searchInput}
          />
        </div>

        {loading ? (
          <div style={styles.emptyBox}>Loading users...</div>
        ) : pageItems.length === 0 ? (
          <div style={styles.emptyBox}>No users found.</div>
        ) : (
          <div style={styles.table}>
            <div style={styles.tableHeader}>
              <div>ID</div>
              <div>Name</div>
              <div>Email</div>
              <div>Role</div>
              <div>Status</div>
              <div>Joined</div>
              <div />
            </div>

            {pageItems.map((user, index) => (
              <div
                key={user.userId}
                style={{
                  ...styles.tableRow,
                  ...(index === pageItems.length - 1 ? styles.tableRowLast : null),
                }}
              >
                <div>{shortUserId(user.userId)}</div>
                <div style={styles.cellStrong}>{user.fullName}</div>
                <div>{user.email}</div>
                <div>{formatRole(user.role)}</div>
                <div>
                  <span style={getStatusStyle(user)}>{getStatusLabel(user)}</span>
                </div>
                <div>{formatDate(user.createdAt)}</div>
                <div>
                  <button type="button" style={styles.linkButton} onClick={() => alert("User details view coming soon.")}>View</button>
                </div>
              </div>
            ))}
          </div>
        )}

        <div style={styles.pagination}>
          <div>
            Showing {filteredUsers.length === 0 ? 0 : startIndex + 1}-{Math.min(startIndex + PAGE_SIZE, filteredUsers.length)} of {filteredUsers.length} users
          </div>
          <div style={styles.pager}>
            <button
              type="button"
              style={styles.pagerButton}
              onClick={() => setPage((prev) => Math.max(1, prev - 1))}
              disabled={safePage <= 1}
            >
              &lt;
            </button>
            {Array.from({ length: totalPages }, (_, idx) => idx + 1).map((num) => (
              <button
                key={num}
                type="button"
                style={safePage === num ? styles.pagerButtonActive : styles.pagerButton}
                onClick={() => setPage(num)}
              >
                {num}
              </button>
            ))}
            <button
              type="button"
              style={styles.pagerButton}
              onClick={() => setPage((prev) => Math.min(totalPages, prev + 1))}
              disabled={safePage >= totalPages}
            >
              &gt;
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}
