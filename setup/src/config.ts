import 'dotenv/config';
import { Ed25519Keypair } from '@mysten/sui/keypairs/ed25519';
import { Secp256r1Keypair } from '@mysten/sui/keypairs/secp256r1';
import { Secp256k1Keypair } from '@mysten/sui/keypairs/secp256k1';
import { Keypair } from '@mysten/sui/cryptography';
import { fromBase64 } from '@mysten/bcs';

// From .env
export const SUI_NETWORK = process.env.SUI_NETWORK!;

// Parse admin secret key and create keypairs
function toKeypair(secretKey: Uint8Array): Keypair {
    let signer: Keypair;
    if (secretKey[0] === 0) {
        signer = Ed25519Keypair.fromSecretKey(
            secretKey
                .slice(1)
        );
    } else if (secretKey[0] === 1) {
        signer = Secp256k1Keypair.fromSecretKey(
            secretKey
                .slice(1)
        );
    } else if (secretKey[0] === 2) {
        signer = Secp256r1Keypair.fromSecretKey(
            secretKey
                .slice(1)
        );
    } else {
        throw new Error(`Unknown key scheme: ${secretKey[0]}`);
    }
    return signer;
}

export const ADMIN_KEYPAIR = toKeypair(fromBase64(process.env.ADMIN_SECRET_KEY!));
export const ADMIN_SECRET_KEY = process.env.ADMIN_SECRET_KEY!;
export const PACKAGE_ID = process.env.PACKAGE_ID!;
export const ADMIN_CAP = process.env.ADMIN_CAP!;
export const COFFEE_MACHINE = process.env.COFFEE_MACHINE!;