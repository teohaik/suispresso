import { z } from "zod";
import 'dotenv/config';


/*
 * The schema for the client-side environment variables
 * These variables should be defined in the app/.env file
 * These variables are NOT SECRET, they are exposed to the client side
 * They can and should be tracked by Git
 * All of the env variables must have the NEXT_PUBLIC_ prefix
 */

const clientConfigSchema = z.object({
  NEXT_PUBLIC_SUI_NETWORK: z.string(),
  NEXT_PUBLIC_SUI_NETWORK_NAME: z.enum(["mainnet", "testnet"]),
  NEXT_PUBLIC_PACKAGE_ADDRESS: z.string(),
  USE_TOP_NAVBAR_IN_LARGE_SCREEN: z.boolean(),
  ENOKI_CLIENT_NETWORK: z.enum(["mainnet", "testnet"]),
  NEXT_PUBLIC_GOOGLE_CLIENT_ID: z.string(),
  NEXT_PUBLIC_ENOKI_API_KEY: z.string(),
  ADMIN_ADDRESS: z.string(),
  ADMIN_CAP: z.string(),
});

const clientConfig = clientConfigSchema.parse({
  NEXT_PUBLIC_SUI_NETWORK: process.env.NEXT_PUBLIC_SUI_NETWORK!,
  NEXT_PUBLIC_SUI_NETWORK_NAME: process.env.NEXT_PUBLIC_SUI_NETWORK_NAME as
    | "mainnet"
    | "testnet",
  NEXT_PUBLIC_PACKAGE_ADDRESS: process.env.NEXT_PUBLIC_PACKAGE_ADDRESS!,
  USE_TOP_NAVBAR_IN_LARGE_SCREEN:
    process.env.NEXT_PUBLIC_USE_TOP_NAVBAR_IN_LARGE_SCREEN === "true",
  ENOKI_CLIENT_NETWORK: process.env.NEXT_PUBLIC_ENOKI_CLIENT_NETWORK as
    | "mainnet"
    | "testnet",
  NEXT_PUBLIC_GOOGLE_CLIENT_ID: process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID,
  NEXT_PUBLIC_ENOKI_API_KEY: process.env.NEXT_PUBLIC_ENOKI_API_KEY,
  ADMIN_ADDRESS: process.env.NEXT_PUBLIC_ADMIN_ADDRESS,
  ADMIN_CAP: process.env.ADMIN_CAP,
});

export default clientConfig;
