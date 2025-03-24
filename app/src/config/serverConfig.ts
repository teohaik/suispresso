import { z } from "zod";
import 'dotenv/config';

/*
 * The schema for the server-side environment variables
 * These variables should be defined in:
 * * the app/.env.development.local file for the local environment
 * * the Vercel's UI for the deployed environment
 * They must not be tracked by Git
 * They are SECRET, and not exposed to the client side
 */

const serverConfigSchema = z.object({
  TESTING_MODE: z.boolean().default(false)
});

const serverConfig = serverConfigSchema.parse({
  TESTING_MODE:                 process.env.VERCEL_ENV && process.env.VERCEL_ENV !== "production"
});

export default serverConfig;
