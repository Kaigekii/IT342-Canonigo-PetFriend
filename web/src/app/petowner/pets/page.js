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
    maxWidth: 980,
    margin: "0 auto",
    padding: "28px 24px 36px",
  },
  pageHeader: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
  },
  pageTitle: {
    fontSize: 28,
    fontWeight: 800,
    marginBottom: 10,
  },
  addPetButton: {
    height: 40,
    padding: "0 16px",
    borderRadius: 8,
    border: "2px solid #FFD8B9",
    backgroundColor: "#FFD8B9",
    color: "#333333",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    cursor: "pointer",
  },
  petList: {
    display: "grid",
    gap: 12,
    marginTop: 14,
  },
  petCard: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 12,
    display: "grid",
    gridTemplateColumns: "78px 1fr auto",
    gap: 12,
    alignItems: "start",
  },
  photoBox: {
    width: 66,
    height: 66,
    borderRadius: 8,
    backgroundColor: "#FFB6C1",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 11,
    fontWeight: 700,
  },
  petName: {
    fontSize: 20,
    fontWeight: 700,
    marginBottom: 6,
  },
  petInfoGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
    gap: 2,
    fontSize: 13,
    color: "#6F6F6F",
    fontWeight: 600,
  },
  cardActions: {
    display: "flex",
    gap: 6,
  },
  tinyButton: {
    height: 28,
    borderRadius: 6,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    padding: "0 10px",
    cursor: "pointer",
  },
  tinyDanger: {
    height: 28,
    borderRadius: 6,
    border: "1px solid #FFCCBC",
    backgroundColor: "#FFCCBC",
    color: "#333333",
    fontSize: 12,
    fontWeight: 700,
    padding: "0 10px",
    cursor: "pointer",
  },
  formWrap: {
    marginTop: 28,
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 14,
  },
  formTitle: {
    fontSize: 13,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.08em",
    marginBottom: 12,
  },
  formGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(2, minmax(0, 1fr))",
    gap: 10,
  },
  fieldLabel: {
    fontSize: 11,
    color: "#7A7A7A",
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    fontWeight: 700,
    marginBottom: 4,
  },
  input: {
    width: "100%",
    height: 40,
    padding: "0 10px",
    fontSize: 13,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    outline: "none",
  },
  textarea: {
    width: "100%",
    minHeight: 70,
    resize: "vertical",
    padding: 10,
    fontSize: 13,
    color: "#333333",
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 6,
    outline: "none",
  },
  uploadBox: {
    width: "100%",
    minHeight: 56,
    border: "1px dashed #D3D3D3",
    borderRadius: 6,
    backgroundColor: "#FFF8F0",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: 12,
    color: "#7A7A7A",
    fontWeight: 600,
    textAlign: "center",
    padding: 8,
  },
  formActions: {
    marginTop: 12,
  },
  button: {
    height: 40,
    padding: "0 14px",
    borderRadius: 8,
    border: "2px solid #FFD8B9",
    backgroundColor: "#FFD8B9",
    color: "#333333",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    cursor: "pointer",
  },
  buttonSecondary: {
    height: 40,
    padding: "0 14px",
    borderRadius: 8,
    border: "1px solid #D3D3D3",
    backgroundColor: "#FFF8F0",
    color: "#333333",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    cursor: "pointer",
  },
  errorBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#FFCCBC",
    border: "2px solid #FFCCBC",
    color: "#333333",
    fontSize: 14,
    marginTop: 12,
  },
  successBox: {
    padding: 12,
    borderRadius: 10,
    backgroundColor: "#B6E5D8",
    border: "2px solid #B6E5D8",
    color: "#333333",
    fontSize: 14,
    marginTop: 12,
  },
  empty: {
    border: "1px solid #D3D3D3",
    borderRadius: 8,
    backgroundColor: "#FFF8F0",
    boxShadow: "0px 2px 6px rgba(0,0,0,0.05)",
    padding: 12,
    fontSize: 14,
    color: "#666666",
    fontWeight: 600,
  },
  modalOverlay: {
    position: "fixed",
    inset: 0,
    backgroundColor: "rgba(51, 51, 51, 0.35)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: 16,
    zIndex: 60,
  },
  modalCard: {
    width: "100%",
    maxWidth: 420,
    backgroundColor: "#FFF8F0",
    border: "1px solid #D3D3D3",
    borderRadius: 12,
    boxShadow: "0px 8px 18px rgba(0,0,0,0.12)",
    padding: 18,
  },
  modalTitle: {
    fontSize: 18,
    fontWeight: 800,
    color: "#333333",
    marginBottom: 8,
  },
  modalText: {
    fontSize: 14,
    color: "#666666",
    lineHeight: 1.5,
    marginBottom: 14,
  },
  modalActions: {
    display: "flex",
    justifyContent: "flex-end",
    gap: 8,
  },
  modalDangerButton: {
    height: 40,
    padding: "0 14px",
    borderRadius: 8,
    border: "2px solid #FFCCBC",
    backgroundColor: "#FFCCBC",
    color: "#333333",
    fontSize: 12,
    fontWeight: 800,
    textTransform: "uppercase",
    letterSpacing: "0.07em",
    cursor: "pointer",
  },
};

function asWeight(value) {
  if (value === null || value === undefined || value === "") return null;
  const num = Number(value);
  return Number.isFinite(num) ? num : null;
}

export default function PetOwnerPetsPage() {
  const router = useRouter();
  const menuRef = useRef(null);

  const token = useMemo(() => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem("token");
  }, []);

  const [user, setUser] = useState(null);
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [editingPetId, setEditingPetId] = useState(null);
  const [petToDelete, setPetToDelete] = useState(null);

  const [petName, setPetName] = useState("");
  const [breed, setBreed] = useState("");
  const [age, setAge] = useState("");
  const [weight, setWeight] = useState("");
  const [species, setSpecies] = useState("DOG");
  const [vaccinationStatus, setVaccinationStatus] = useState("UP_TO_DATE");
  const [specialNeeds, setSpecialNeeds] = useState("");
  const [photoUrl, setPhotoUrl] = useState("");

  const resetForm = () => {
    setPetName("");
    setBreed("");
    setAge("");
    setWeight("");
    setSpecies("DOG");
    setVaccinationStatus("UP_TO_DATE");
    setSpecialNeeds("");
    setPhotoUrl("");
    setEditingPetId(null);
  };

  const loadData = useCallback(async (authToken) => {
    const authHeaders = { Authorization: `Bearer ${authToken}` };

    const meRes = await fetch(`${API_BASE}/api/user/me`, { headers: authHeaders });
    if (meRes.status === 401) {
      localStorage.removeItem("token");
      router.replace("/login");
      return;
    }
    if (!meRes.ok) throw new Error("Failed to load profile");

    const me = await meRes.json();
    if (me.role !== "PET_OWNER") {
      router.replace("/dashboard");
      return;
    }

    setUser(me);

    const petsRes = await fetch(`${API_BASE}/api/pets`, { headers: authHeaders });
    if (!petsRes.ok) throw new Error("Failed to load pets");

    const petsData = await petsRes.json();
    setPets(Array.isArray(petsData) ? petsData : []);
  }, [router]);

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

  const handleSavePet = async (e) => {
    e.preventDefault();
    setError("");
    setSuccess("");

    const name = petName.trim();
    if (!name) {
      setError("Pet name is required");
      return;
    }

    if (!token) {
      router.replace("/login");
      return;
    }

    setSaving(true);
    try {
      const isEditing = Boolean(editingPetId);
      const res = await fetch(
        isEditing ? `${API_BASE}/api/pets/${editingPetId}` : `${API_BASE}/api/pets`,
        {
        method: isEditing ? "PUT" : "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name,
          breed: breed.trim() || null,
          age: age ? Number(age) : null,
          weight: asWeight(weight),
          species,
          specialNeeds: specialNeeds.trim() || null,
          vaccinationStatus,
          photoUrl: photoUrl.trim() || null,
        }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to save pet");
      }

      resetForm();

      await loadData(token);
      setSuccess(isEditing ? "Pet updated successfully" : "Pet saved successfully");
    } catch (e2) {
      setError(e2?.message || "Failed to save pet");
    } finally {
      setSaving(false);
    }
  };

  const handleEditPet = (pet) => {
    setEditingPetId(pet.petId);
    setPetName(pet.name || "");
    setBreed(pet.breed || "");
    setAge(typeof pet.age === "number" ? String(pet.age) : "");
    setWeight(typeof pet.weight === "number" ? String(pet.weight) : "");
    setSpecies(pet.species || "DOG");
    setVaccinationStatus(pet.vaccinationStatus || "UP_TO_DATE");
    setSpecialNeeds(pet.specialNeeds || "");
    setPhotoUrl(pet.photoUrl || "");
    setError("");
    setSuccess("");
    document.getElementById("add-pet-form")?.scrollIntoView({ behavior: "smooth", block: "start" });
  };

  const handleDeletePet = async () => {
    if (!token) {
      router.replace("/login");
      return;
    }

    if (!petToDelete) {
      return;
    }

    setError("");
    setSuccess("");
    try {
      const res = await fetch(`${API_BASE}/api/pets/${petToDelete.petId}`, {
        method: "DELETE",
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || "Failed to delete pet");
      }

      setPets((prev) => prev.filter((p) => p.petId !== petToDelete.petId));
      setSuccess("Pet deleted successfully");
      setPetToDelete(null);
    } catch (e3) {
      setError(e3?.message || "Failed to delete pet");
    }
  };

  return (
    <div style={styles.page}>
      <header style={styles.topBar}>
        <div style={styles.brand}>PetFriend</div>
        <nav style={styles.nav} aria-label="Primary">
          <span style={styles.navItem} onClick={() => router.push("/petowner/dashboard")}>Dashboard</span>
          <span style={styles.navItemActive}>My Pets</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/find-sitter")}>Find Sitter</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/bookings")}>Bookings</span>
          <span style={styles.navItem} onClick={() => router.push("/petowner/messages")}>Messages</span>
        </nav>

        <div style={styles.topRightWrap} ref={menuRef}>
          <span style={styles.topRightRole}>Pet Owner</span>
          <button
            type="button"
            style={styles.avatarButton}
            aria-label="Open profile menu"
            onClick={() => setShowProfileMenu((prev) => !prev)}
          />

          {showProfileMenu && (
            <div style={styles.profileMenu}>
              <button type="button" style={styles.menuItem} onClick={() => router.push("/petowner/profile")}>Profile</button>
              <button type="button" style={styles.menuItemDanger} onClick={handleLogout}>Logout</button>
            </div>
          )}
        </div>
      </header>

      <main style={styles.content}>
        <div style={styles.pageHeader}>
          <h1 style={styles.pageTitle}>My Pets</h1>
          <button
            type="button"
            style={styles.addPetButton}
            onClick={() => {
              resetForm();
              document.getElementById("add-pet-form")?.scrollIntoView({ behavior: "smooth", block: "start" });
            }}
          >
            + Add Pet
          </button>
        </div>

        {error && <div style={styles.errorBox}>{error}</div>}
        {success && <div style={styles.successBox}>{success}</div>}

        <section style={styles.petList} aria-label="Pet list">
          {loading ? (
            <div style={styles.empty}>Loading pets...</div>
          ) : pets.length === 0 ? (
            <div style={styles.empty}>No pets yet.</div>
          ) : (
            pets.map((pet) => (
              <div key={pet.petId} style={styles.petCard}>
                <div style={styles.photoBox}>Photo</div>

                <div>
                  <div style={styles.petName}>{pet.name}</div>
                  <div style={styles.petInfoGrid}>
                    <div>Breed: {pet.breed || "-"}</div>
                    <div>Age: {typeof pet.age === "number" ? `${pet.age} yrs` : "-"}</div>
                    <div>Species: {pet.species || "-"}</div>
                    <div>Weight: {typeof pet.weight === "number" ? `${pet.weight} kg` : "-"}</div>
                    <div>Vaccination: {pet.vaccinationStatus ? pet.vaccinationStatus.replaceAll("_", " ") : "-"}</div>
                    <div>Special Needs: {pet.specialNeeds || "None"}</div>
                  </div>
                </div>

                <div style={styles.cardActions}>
                  <button type="button" style={styles.tinyButton} onClick={() => handleEditPet(pet)} disabled={saving}>Edit</button>
                  <button type="button" style={styles.tinyDanger} onClick={() => setPetToDelete(pet)} disabled={saving}>Delete</button>
                </div>
              </div>
            ))
          )}
        </section>

        <section id="add-pet-form" style={styles.formWrap} aria-label="Add pet form">
          <div style={styles.formTitle}>{editingPetId ? "Edit Pet Form" : "Add New Pet Form"}</div>
          <form onSubmit={handleSavePet}>
            <div style={styles.formGrid}>
              <div>
                <div style={styles.fieldLabel}>Pet Name</div>
                <input style={styles.input} placeholder="e.g. Bantay" value={petName} onChange={(e) => setPetName(e.target.value)} disabled={saving} />
              </div>
              <div>
                <div style={styles.fieldLabel}>Breed</div>
                <input style={styles.input} placeholder="e.g. Aspin" value={breed} onChange={(e) => setBreed(e.target.value)} disabled={saving} />
              </div>
              <div>
                <div style={styles.fieldLabel}>Age</div>
                <input style={styles.input} type="number" min="0" placeholder="e.g. 3" value={age} onChange={(e) => setAge(e.target.value)} disabled={saving} />
              </div>
              <div>
                <div style={styles.fieldLabel}>Weight (kg)</div>
                <input style={styles.input} type="number" min="0" step="0.1" placeholder="e.g. 12.5" value={weight} onChange={(e) => setWeight(e.target.value)} disabled={saving} />
              </div>
              <div>
                <div style={styles.fieldLabel}>Species</div>
                <select style={styles.input} value={species} onChange={(e) => setSpecies(e.target.value)} disabled={saving}>
                  <option value="DOG">DOG</option>
                  <option value="CAT">CAT</option>
                  <option value="OTHER">OTHER</option>
                </select>
              </div>
              <div>
                <div style={styles.fieldLabel}>Vaccination Status</div>
                <select style={styles.input} value={vaccinationStatus} onChange={(e) => setVaccinationStatus(e.target.value)} disabled={saving}>
                  <option value="UP_TO_DATE">UP TO DATE</option>
                  <option value="NOT_UP_TO_DATE">NOT UP TO DATE</option>
                  <option value="UNKNOWN">UNKNOWN</option>
                </select>
              </div>
              <div style={{ gridColumn: "1 / -1" }}>
                <div style={styles.fieldLabel}>Special Needs</div>
                <textarea style={styles.textarea} placeholder="Describe any special needs..." value={specialNeeds} onChange={(e) => setSpecialNeeds(e.target.value)} disabled={saving} />
              </div>
              <div style={{ gridColumn: "1 / -1" }}>
                <div style={styles.fieldLabel}>Photo Upload</div>
                <div style={styles.uploadBox}>Drop image here or paste image URL below</div>
              </div>
              <div style={{ gridColumn: "1 / -1" }}>
                <div style={styles.fieldLabel}>Photo URL (optional)</div>
                <input style={styles.input} placeholder="https://..." value={photoUrl} onChange={(e) => setPhotoUrl(e.target.value)} disabled={saving} />
              </div>
            </div>

            <div style={styles.formActions}>
              <div style={{ display: "flex", gap: 8 }}>
                <button type="submit" style={styles.button} disabled={saving}>
                  {saving ? "Saving..." : editingPetId ? "Update Pet" : "Save Pet"}
                </button>
                {editingPetId && (
                  <button type="button" style={styles.buttonSecondary} onClick={resetForm} disabled={saving}>
                    Cancel Edit
                  </button>
                )}
              </div>
            </div>
          </form>
        </section>
      </main>

      {petToDelete && (
        <div style={styles.modalOverlay} role="dialog" aria-modal="true" aria-label="Delete confirmation">
          <div style={styles.modalCard}>
            <div style={styles.modalTitle}>Delete Pet?</div>
            <div style={styles.modalText}>
              You are about to delete <strong>{petToDelete.name}</strong>. This action cannot be undone.
            </div>
            <div style={styles.modalActions}>
              <button type="button" style={styles.buttonSecondary} onClick={() => setPetToDelete(null)}>
                Cancel
              </button>
              <button type="button" style={styles.modalDangerButton} onClick={handleDeletePet}>
                Yes, Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
