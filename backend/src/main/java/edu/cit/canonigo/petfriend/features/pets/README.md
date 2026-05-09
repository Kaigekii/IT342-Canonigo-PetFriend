# Pets Feature Slice

## Overview
Handles CRUD operations for pets belonging to pet owners. Owners can create, read, update, and delete their pets.

## Structure
```
features/pets/
├── PetController.java  - REST endpoints
├── PetService.java     - Business logic
├── PetDtos.java        - Request/Response DTOs
└── README.md           - This file
```

## API Endpoints

### GET /api/pets
List all pets for authenticated owner (ordered by most recent first).

**Response:**
```json
[
  {
    "petId": "uuid",
    "ownerId": "uuid",
    "ownerName": "string",
    "name": "string",
    "breed": "string",
    "age": 3,
    "weight": 15.5,
    "species": "DOG|CAT|BIRD|RABBIT|OTHER",
    "specialNeeds": "string",
    "vaccinationStatus": "UP_TO_DATE|OVERDUE|NOT_VACCINATED",
    "photoUrl": "string (optional)"
  }
]
```

### POST /api/pets
Create a new pet for authenticated owner.

**Request:**
```json
{
  "name": "string (required)",
  "breed": "string (optional)",
  "age": 3,
  "weight": 15.5,
  "species": "DOG|CAT|BIRD|RABBIT|OTHER (required)",
  "specialNeeds": "string (optional)",
  "vaccinationStatus": "UP_TO_DATE|OVERDUE|NOT_VACCINATED (required)",
  "photoUrl": "string (optional)"
}
```

**Response:**
```json
{
  "petId": "uuid",
  "ownerId": "uuid",
  "ownerName": "string",
  "name": "string",
  "breed": "string",
  "age": 3,
  "weight": 15.5,
  "species": "DOG|CAT|BIRD|RABBIT|OTHER",
  "specialNeeds": "string",
  "vaccinationStatus": "UP_TO_DATE|OVERDUE|NOT_VACCINATED",
  "photoUrl": "string"
}
```

### PUT /api/pets/{petId}
Update an existing pet.

**Request:**
Same as POST /api/pets

### DELETE /api/pets/{petId}
Delete a pet (owner can only delete their own pets).

**Response:**
```json
{
  "message": "Pet deleted successfully"
}
```

## Validation Rules
- Pet name is required and must not be blank
- Species is required (must be one of: DOG, CAT, BIRD, RABBIT, OTHER)
- Vaccination status is required
- Owner can only create/update/delete pets that belong to them
- Only PET_OWNER role can perform pet operations
- Age and weight are optional

## Pet Species Enum
- `DOG`
- `CAT`
- `BIRD`
- `RABBIT`
- `OTHER`

## Vaccination Status Enum
- `UP_TO_DATE` - Vaccinations are current
- `OVERDUE` - Vaccinations need renewal
- `NOT_VACCINATED` - No vaccinations recorded

## Business Logic
- Pets are ordered by creation date (newest first)
- Pet photo URL is optional
- Special needs can include dietary restrictions, behavioral notes, medical conditions, etc.
- Weight is stored as Double (supports decimal values like 15.5 kg)
- Age is stored as Integer (years)

## Dependencies
- `PetRepository`: Database access
- `UserRepository`: User data access
- `PetService`: Business logic layer

## Error Handling
Custom `PetException` handles:
- Owner not found
- Pet not found
- Authorization violations (pet doesn't belong to owner)
- Validation failures

## Notes
- All pets returned to owner are their own only
- Photo URL validation is minimal (format check only)
- Deletion is permanent (no soft delete)
