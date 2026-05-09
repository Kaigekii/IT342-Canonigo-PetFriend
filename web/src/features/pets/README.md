# Pets Feature - Frontend Documentation

## Overview
The Pets feature handles user pet management, including creating, viewing, updating, and deleting pets. Pet owners manage their pets here.

## Directory Structure
```
features/pets/
├── api.js           # API client functions
├── constants.js     # Pet-specific constants
├── hooks/
│   └── usePets.js   # Pet state management hook
├── components/
│   ├── PetList.js
│   ├── PetCard.js
│   ├── PetForm.js
│   └── README.md
└── README.md
```

## API Client

### Import
```javascript
import { petsApi } from "@/features/pets/api";
```

### Methods

#### `petsApi.listPets()`
Get all pets for current user.
```javascript
const pets = await petsApi.listPets();
// Returns: [{ petId, name, species, breed, age, ... }, ...]
```

#### `petsApi.createPet(petData)`
Create new pet.
```javascript
const newPet = await petsApi.createPet({
  name: "Buddy",
  species: "DOG",
  breed: "Golden Retriever",
  age: 3,
  bio: "Friendly and energetic"
});
```

#### `petsApi.updatePet(petId, petData)`
Update pet information.
```javascript
const updated = await petsApi.updatePet(petId, {
  name: "Buddy Jr",
  bio: "Updated bio"
});
```

#### `petsApi.deletePet(petId)`
Delete a pet.
```javascript
await petsApi.deletePet(petId);
```

## Hooks

### usePets Hook
Manages pet list state and operations.

#### Import
```javascript
import { usePets } from "@/features/pets/hooks/usePets";
```

#### Usage
```javascript
"use client";

import { useEffect } from "react";
import { usePets } from "@/features/pets/hooks/usePets";
import { LoadingSpinner } from "@/shared/components/Banners";

export default function PetsList() {
  const { pets, loading, error, listPets } = usePets();

  useEffect(() => {
    listPets();
  }, []);

  if (loading) return <LoadingSpinner message="Loading pets..." />;
  if (error) return <p>Error: {error}</p>;

  return (
    <div>
      {pets.map(pet => (
        <div key={pet.petId}>
          <h3>{pet.name}</h3>
          <p>{pet.species} - {pet.breed}</p>
        </div>
      ))}
    </div>
  );
}
```

#### Properties
- `pets` - Array of pet objects
- `loading` - Boolean indicating if data is being fetched
- `error` - Error message if any
- `listPets()` - Fetch all pets
- `createPet(petData)` - Create new pet
- `deletePet(petId)` - Delete pet

## Pet Data Structure
```javascript
{
  petId: "uuid",
  ownerId: "uuid",
  name: "Buddy",
  species: "DOG",        // DOG, CAT, RABBIT, HAMSTER, BIRD, OTHER
  breed: "Golden Retriever",
  age: 3,
  bio: "Friendly dog",
  createdAt: "2024-03-15T10:00:00Z",
  updatedAt: "2024-03-15T14:30:00Z"
}
```

## Constants

### Pet Species
```javascript
import { PET_SPECIES, PET_SPECIES_LABELS } from "@/shared/constants/statuses";

console.log(PET_SPECIES.DOG);              // "DOG"
console.log(PET_SPECIES_LABELS.DOG);       // "Dog"
```

## Example Component: PetForm

```javascript
"use client";

import { useState } from "react";
import { usePets } from "@/features/pets/hooks/usePets";
import { PET_SPECIES, PET_SPECIES_LABELS } from "@/shared/constants/statuses";
import { ErrorBanner, SuccessBanner } from "@/shared/components/Banners";

export default function PetForm() {
  const { createPet, loading, error } = usePets();
  const [success, setSuccess] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    species: "",
    breed: "",
    age: "",
    bio: ""
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await createPet(formData);
      setSuccess(true);
      setFormData({ name: "", species: "", breed: "", age: "", bio: "" });
      setTimeout(() => setSuccess(false), 3000);
    } catch (err) {
      console.error("Create pet error:", err);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <ErrorBanner message={error} />}
      {success && <SuccessBanner message="Pet added successfully!" />}
      
      <input
        type="text"
        placeholder="Pet name"
        value={formData.name}
        onChange={(e) => setFormData({...formData, name: e.target.value})}
        required
      />
      
      <select
        value={formData.species}
        onChange={(e) => setFormData({...formData, species: e.target.value})}
        required
      >
        <option value="">Select species</option>
        {Object.entries(PET_SPECIES).map(([key, value]) => (
          <option key={key} value={value}>
            {PET_SPECIES_LABELS[value]}
          </option>
        ))}
      </select>
      
      <input
        type="text"
        placeholder="Breed"
        value={formData.breed}
        onChange={(e) => setFormData({...formData, breed: e.target.value})}
      />
      
      <input
        type="number"
        placeholder="Age"
        min="0"
        max="50"
        value={formData.age}
        onChange={(e) => setFormData({...formData, age: parseInt(e.target.value)})}
      />
      
      <textarea
        placeholder="Pet bio"
        value={formData.bio}
        onChange={(e) => setFormData({...formData, bio: e.target.value})}
      />
      
      <button type="submit" disabled={loading}>
        {loading ? "Adding pet..." : "Add Pet"}
      </button>
    </form>
  );
}
```

## Related Features
- [Auth Feature](../auth/README.md)
- [Bookings Feature](../booking/README.md)

---

**Created**: May 9, 2026  
**Version**: 1.0  
**Status**: Complete
