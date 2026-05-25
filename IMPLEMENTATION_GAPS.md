# Implementation Gaps (Final Requirements)

This list is based on the current project scan and focuses on required items that appear missing or incomplete. Optional items are excluded.

## Missing or Incomplete Items

1. Email Sending (SMTP)
- Required: send 1 account-related email and 1 system notification email using SMTP.
- Status: no SMTP configuration or mail-sending service found.

2. File Upload
- Required: upload a file (image/pdf), store on server, link to DB record, and allow view/download.
- Status: no upload endpoint or storage handling found.

3. Payment Gateway (Sandbox)
- Required: real payment provider in test mode; record payment results in DB; handle success/failure.
- Status: no payment integration found.

4. External Public API Integration
- Required: consume a real public API used in a meaningful feature and display data.
- Status: only OAuth token verification is present; may not satisfy the public API feature requirement.

5. Web Protected Routes (Consistency)
- Required: protected routes/pages with auth guard.
- Status: auth checks exist in some pages, but no global route guard or middleware found.

## Notes
- Google OAuth login is implemented via Supabase token verification and JWT issuance.
- JWT auth, /me endpoint, password hashing, role checks, and core CRUD modules appear in place.
