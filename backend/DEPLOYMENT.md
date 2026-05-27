# Backend Deployment

## Required environment variables

Set these in your backend host (Render, Railway, Fly.io, etc.):

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `JWT_SECRET`
- `SUPABASE_URL`
- `SUPABASE_SERVICE_ROLE_KEY`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM`
- `PAYMONGO_SECRET_KEY`
- `FRONTEND_BASE_URL`
- `CORS_ALLOWED_ORIGIN_PATTERNS`

## Recommended values

- `FRONTEND_BASE_URL` should be your Vercel domain, for example `https://your-app.vercel.app`
- `CORS_ALLOWED_ORIGIN_PATTERNS` should include at least:
  - `http://localhost:3000`
  - `https://*.vercel.app`
  - your custom Vercel domain if you use one
- `PAYMONGO_SUCCESS_URL` and `PAYMONGO_CANCEL_URL` can be left unset because they default to the frontend base URL plus the payment routes.

## PayMongo

The backend creates PayMongo checkout sessions and sends the user back to the frontend payment success/cancel pages. Keep the PayMongo webhook pointing at:

- `/api/payments/paymongo/webhook`

## Notes

- The backend must be publicly reachable from Vercel.
- Keep Google OAuth, Supabase, and PayMongo settings aligned between the frontend and backend.
