import {z} from "zod";
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
    TESTING_MODE: z.boolean().default(false),
    CRON_SECRET: z.string(),
    WAL_AIRDROP_PACKAGE: z.string(),
    ADMIN_SECRET_KEY: z.string(),
});

const serverConfig = serverConfigSchema.parse({
    WAL_AIRDROP_PACKAGE: process.env.WAL_AIRDROP_PACKAGE,
    CRON_SECRET: process.env.CRON_SECRET,
    ADMIN_SECRET_KEY: process.env.ADMIN_SECRET,
    TESTING_MODE: process.env.VERCEL_ENV && process.env.VERCEL_ENV !== "production"
});

export default serverConfig;
