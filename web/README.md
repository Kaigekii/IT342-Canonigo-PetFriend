This is a [Next.js](https://nextjs.org) project bootstrapped with [`create-next-app`](https://github.com/vercel/next.js/tree/canary/packages/create-next-app).

## Getting Started

First, run the development server:

```bash
npm run dev
# or
yarn dev
# or
pnpm dev
# or
bun dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

You can start editing the page by modifying `app/page.js`. The page auto-updates as you edit the file.

This project uses [`next/font`](https://nextjs.org/docs/app/building-your-application/optimizing/fonts) to automatically optimize and load [Geist](https://vercel.com/font), a new font family for Vercel.

## Learn More

To learn more about Next.js, take a look at the following resources:


## Vercel Deployment

This app is ready for Vercel as a standard Next.js deployment.

### Required environment variables

Set these in Vercel Project Settings > Environment Variables:

- `NEXT_PUBLIC_API_BASE_URL` - public URL of the deployed backend API
- `NEXT_PUBLIC_SUPABASE_URL` - your Supabase project URL
- `NEXT_PUBLIC_SUPABASE_ANON_KEY` - your Supabase anon/public key

Use the same values in a local `.env.local` file when running locally.

### Supabase OAuth redirect URIs

Add both of these to Supabase Auth settings:

- `http://localhost:3000/auth/callback`
- `https://<your-vercel-domain>/auth/callback`

### Backend notes

The frontend calls the backend directly, so the backend must be publicly reachable from Vercel. If the backend is still local, the deployed site will not be able to log in or create bookings.
You can check out [the Next.js GitHub repository](https://github.com/vercel/next.js) - your feedback and contributions are welcome!

## Deploy on Vercel

The easiest way to deploy your Next.js app is to use the [Vercel Platform](https://vercel.com/new?utm_medium=default-template&filter=next.js&utm_source=create-next-app&utm_campaign=create-next-app-readme) from the creators of Next.js.

Check out our [Next.js deployment documentation](https://nextjs.org/docs/app/building-your-application/deploying) for more details.
