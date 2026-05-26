"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useRouter } from "next/navigation";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

function buildImageUrl(url) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return `${API_BASE}${url}`;
  return url;
}

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
  },
  navItemActive: {
    color: "#333333",
    opacity: 1,
    borderBottom: "2px solid #FFD8B9",
    paddingBottom: 8,
    cursor: "pointer",
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
    maxWidth: 1100,
    margin: "0 auto",
    padding: "24px",
  },
  title: {
    fontSize: 24,
    fontWeight: 800,
    marginBottom: 16,
  },
  shell: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    display: "grid",
    gridTemplateColumns: "320px 1fr",
    minHeight: 520,
    overflow: "hidden",
  },
  listPane: {
    borderRight: "1px solid #D3D3D3",
    backgroundColor: "#FFFFFF",
  },
  listItem: {
    display: "grid",
    gridTemplateColumns: "44px 1fr auto",
    gap: 10,
    alignItems: "center",
    padding: "14px 16px",
    borderBottom: "1px solid #E6E6E6",
    cursor: "pointer",
  },
  listItemActive: {
    backgroundColor: "#F2F2F2",
  },
  avatar: {
    width: 36,
    height: 36,
    borderRadius: "50%",
    backgroundColor: "#D3D3D3",
  },
  nameText: {
    fontSize: 14,
    fontWeight: 700,
    marginBottom: 4,
  },
  previewText: {
    fontSize: 12,
    color: "#777777",
  },
  timeText: {
    fontSize: 11,
    color: "#777777",
  },
  chatPane: {
    display: "grid",
    gridTemplateRows: "70px 1fr 70px",
    backgroundColor: "#FFFFFF",
  },
  chatHeader: {
    borderBottom: "1px solid #D3D3D3",
    padding: "12px 16px",
    display: "flex",
    alignItems: "center",
    gap: 12,
  },
  chatName: {
    fontSize: 16,
    fontWeight: 800,
  },
  chatMeta: {
    fontSize: 12,
    color: "#777777",
    marginTop: 4,
  },
  messageArea: {
    padding: "16px",
    display: "flex",
    flexDirection: "column",
    gap: 12,
    overflowY: "auto",
  },
  messageBubble: {
    maxWidth: "70%",
    padding: "10px 12px",
    borderRadius: 10,
    fontSize: 13,
    lineHeight: 1.4,
  },
  messageMine: {
    alignSelf: "flex-end",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
  },
  messageTheirs: {
    alignSelf: "flex-start",
    backgroundColor: "#F2F2F2",
    color: "#333333",
  },
  messageTime: {
    fontSize: 11,
    color: "#777777",
    marginTop: 6,
  },
  inputRow: {
    borderTop: "1px solid #D3D3D3",
    padding: "12px 16px",
    display: "grid",
    gridTemplateColumns: "1fr auto",
    gap: 12,
    alignItems: "center",
  },
  input: {
    height: 40,
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    padding: "0 12px",
    fontSize: 13,
    outline: "none",
  },
  sendButton: {
    height: 40,
    borderRadius: 6,
    border: "none",
    backgroundColor: "#1E1E1E",
    color: "#FFFFFF",
    fontSize: 12,
    fontWeight: 800,
    letterSpacing: "0.08em",
    textTransform: "uppercase",
    padding: "0 16px",
    cursor: "pointer",
  },
  emptyBox: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 12,
    fontSize: 14,
    color: "#666666",
    fontWeight: 600,
  },
  helperText: {
    fontSize: 12,
    color: "#777777",
    marginBottom: 10,
  },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: 14,
    marginBottom: 12,
  },
};

function formatTimeLabel(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  const now = new Date();
  const isToday = date.toDateString() === now.toDateString();
  if (isToday) {
    return date.toLocaleTimeString("en-US", { hour: "numeric", minute: "2-digit" });
  }
  return date.toLocaleDateString("en-US", { month: "short", day: "numeric" });
}

export default function PetSitterMessagesPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [userId, setUserId] = useState(null);
  const [user, setUser] = useState(null);
  const [conversations, setConversations] = useState([]);
  const [activeId, setActiveId] = useState(null);
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState("");
  const [loading, setLoading] = useState(true);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState("");
  const [showProfileMenu, setShowProfileMenu] = useState(false);

  const loadData = useCallback(async (authToken) => {
    const headers = { Authorization: `Bearer ${authToken}` };

    const meRes = await fetch(`${API_BASE}/api/user/me`, { headers });
    if (meRes.status === 401) {
      localStorage.removeItem("token");
      router.replace("/login");
      return;
    }
    if (!meRes.ok) throw new Error("Failed to load profile");

    const me = await meRes.json();
    if (me.role !== "PET_SITTER") {
      router.replace("/dashboard");
      return;
    }

    setUser(me);
    setUserId(me.userId);

    const [bookingsRes, threadsRes] = await Promise.all([
      fetch(`${API_BASE}/api/bookings/sitter`, { headers }),
      fetch(`${API_BASE}/api/messages/threads`, { headers }),
    ]);

    if (!bookingsRes.ok) throw new Error("Failed to load bookings");
    if (!threadsRes.ok) throw new Error("Failed to load messages");

    const [bookingsData, threadsData] = await Promise.all([
      bookingsRes.json(),
      threadsRes.json(),
    ]);

    const threadMap = new Map();
    (Array.isArray(threadsData) ? threadsData : []).forEach((thread) => {
      threadMap.set(thread.otherUserId, thread);
    });

    const contactMap = new Map();
    (Array.isArray(bookingsData) ? bookingsData : []).forEach((booking) => {
      if (!booking.ownerId) return;
      if (!contactMap.has(booking.ownerId)) {
        const thread = threadMap.get(booking.ownerId);
        contactMap.set(booking.ownerId, {
          id: booking.ownerId,
          name: booking.ownerName || "Pet Owner",
          roleLabel: "Pet Owner",
          threadId: thread?.threadId || null,
          lastMessage: thread?.lastMessage || "Start a conversation",
          lastMessageAt: thread?.lastMessageAt || "",
        });
      }
    });

    const contactList = Array.from(contactMap.values());
    setConversations(contactList);
    if (contactList.length > 0 && !activeId) {
      setActiveId(contactList[0].id);
    }
  }, [router, activeId]);

  const ensureThread = useCallback(async (authToken, contact) => {
    if (contact.threadId) return contact.threadId;

    const res = await fetch(`${API_BASE}/api/messages/threads`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${authToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ otherUserId: contact.id }),
    });

    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || "Failed to create conversation");
    }

    const thread = await res.json();
    setConversations((prev) =>
      prev.map((item) =>
        item.id === contact.id
          ? {
              ...item,
              threadId: thread.threadId,
              lastMessage: thread.lastMessage || item.lastMessage,
              lastMessageAt: thread.lastMessageAt || item.lastMessageAt,
            }
          : item
      )
    );

    return thread.threadId;
  }, []);

  const loadMessages = useCallback(async (authToken, threadId) => {
    const res = await fetch(`${API_BASE}/api/messages/threads/${threadId}/messages`, {
      headers: { Authorization: `Bearer ${authToken}` },
    });
    if (!res.ok) {
      const msg = await res.text();
      throw new Error(msg || "Failed to load messages");
    }
    const data = await res.json();
    return Array.isArray(data) ? data : [];
  }, []);

  useEffect(() => {
    if (!token) {
      router.replace("/login");
      return;
    }

    const run = async () => {
      setError("");
      setLoading(true);
      try {
        await loadData(token);
      } catch (e) {
        setError(e?.message || "Something went wrong");
      } finally {
        setLoading(false);
      }
    };

    run();
  }, [router, token, loadData]);

  useEffect(() => {
    if (!token || !activeId) return;
    const contact = conversations.find((conv) => conv.id === activeId);
    if (!contact) return;

    let isActive = true;
    const run = async () => {
      setError("");
      setLoadingMessages(true);
      try {
        const threadId = await ensureThread(token, contact);
        const data = await loadMessages(token, threadId);
        if (isActive) {
          setMessages(data);
        }
      } catch (e) {
        if (isActive) {
          setError(e?.message || "Failed to load messages");
        }
      } finally {
        if (isActive) {
          setLoadingMessages(false);
        }
      }
    };

    run();
    return () => {
      isActive = false;
    };
  }, [activeId, conversations, token, ensureThread, loadMessages]);

  useEffect(() => {
    const onClick = (event) => {
      if (!menuRef.current) return;
      if (!menuRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
    };

    document.addEventListener("mousedown", onClick);
    return () => document.removeEventListener("mousedown", onClick);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("token");
    router.replace("/login");
  };

  const activeConversation = conversations.find((c) => c.id === activeId) || null;

  const sendMessage = async () => {
    if (!token || !activeConversation || !draft.trim()) return;
    setSending(true);
    setError("");

    try {
      const threadId = await ensureThread(token, activeConversation);
      const res = await fetch(`${API_BASE}/api/messages/threads/${threadId}/messages`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ content: draft.trim() }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to send message");
      }

      const saved = await res.json();
      setMessages((prev) => [...prev, saved]);
      setConversations((prev) =>
        prev.map((item) =>
          item.id === activeConversation.id
            ? {
                ...item,
                lastMessage: saved.content,
                lastMessageAt: saved.createdAt,
              }
            : item
        )
      );
      setDraft("");
    } catch (e) {
      setError(e?.message || "Failed to send message");
    } finally {
      setSending(false);
    }
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter") {
      event.preventDefault();
      sendMessage();
    }
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petsitter/dashboard")}>Dashboard</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/profile")}>My Profile</span>
          <span style={styles.navItem} onClick={() => router.push("/petsitter/requests")}>Requests</span>
          <span style={styles.navItemActive}>Messages</span>
        </nav>

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Pet Sitter</span>
          <button
            type="button"
            style={{
              ...styles.avatarButton,
              backgroundImage: user?.profilePhotoUrl ? `url(${buildImageUrl(user.profilePhotoUrl)})` : undefined,
              backgroundSize: "cover",
              backgroundPosition: "center",
              backgroundRepeat: "no-repeat",
              backgroundColor: user?.profilePhotoUrl ? "transparent" : styles.avatarButton.backgroundColor,
              border: user?.profilePhotoUrl ? "none" : styles.avatarButton.border,
            }}
            aria-label="Open profile menu"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          />

          {showProfileMenu && (
            <div style={styles.profileMenu}>
              <button type="button" style={styles.menuItem} onClick={() => router.push("/petsitter/profile")}>Profile</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <h1 style={styles.title}>Messages</h1>
        <div style={styles.helperText}>You can only message owners you have booked with.</div>
        {error && <div style={styles.errorBox}>{error}</div>}

        {loading ? (
          <div style={styles.emptyBox}>Loading conversations...</div>
        ) : conversations.length === 0 ? (
          <div style={styles.emptyBox}>No bookings yet. Accept a request to start messaging.</div>
        ) : (
          <div style={styles.shell}>
            <aside style={styles.listPane}>
              {conversations.map((conv) => (
                <div
                  key={conv.id}
                  style={{
                    ...styles.listItem,
                    ...(activeId === conv.id ? styles.listItemActive : null),
                  }}
                  onClick={() => setActiveId(conv.id)}
                  role="button"
                  tabIndex={0}
                  onKeyDown={(event) => {
                    if (event.key === "Enter") setActiveId(conv.id);
                  }}
                >
                  <div style={styles.avatar} />
                  <div>
                    <div style={styles.nameText}>{conv.name}</div>
                    <div style={styles.previewText}>{conv.lastMessage || "Start a conversation"}</div>
                  </div>
                  <div style={styles.timeText}>{formatTimeLabel(conv.lastMessageAt)}</div>
                </div>
              ))}
            </aside>

            <section style={styles.chatPane}>
              <div style={styles.chatHeader}>
                <div style={styles.avatar} />
                <div>
                  <div style={styles.chatName}>{activeConversation?.name || "Select a conversation"}</div>
                  <div style={styles.chatMeta}>{activeConversation?.roleLabel || ""}</div>
                </div>
              </div>

              <div style={styles.messageArea}>
                {!activeConversation ? (
                  <div style={styles.emptyBox}>Select a conversation to start chatting.</div>
                ) : loadingMessages ? (
                  <div style={styles.emptyBox}>Loading messages...</div>
                ) : messages.length === 0 ? (
                  <div style={styles.emptyBox}>Start your conversation with this owner.</div>
                ) : (
                  messages.map((msg) => (
                    <div
                      key={msg.messageId}
                      style={{
                        ...styles.messageBubble,
                        ...(msg.senderId === userId ? styles.messageMine : styles.messageTheirs),
                      }}
                    >
                      <div>{msg.content}</div>
                      <div style={styles.messageTime}>{formatTimeLabel(msg.createdAt)}</div>
                    </div>
                  ))
                )}
              </div>

              <div style={styles.inputRow}>
                <input
                  type="text"
                  placeholder="Type a message..."
                  style={styles.input}
                  value={draft}
                  onChange={(event) => setDraft(event.target.value)}
                  onKeyDown={handleKeyDown}
                  disabled={!activeConversation || sending}
                />
                <button type="button" style={styles.sendButton} onClick={sendMessage} disabled={!activeConversation || sending}>
                  {sending ? "Sending" : "Send"}
                </button>
              </div>
            </section>
          </div>
        )}
      </main>
    </div>
  );
}
