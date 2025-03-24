
// src/index.ts - renamed from setup.ts

import { ADMIN_KEYPAIR, SUI_NETWORK } from './config';

// Add your setup code here
console.log("Hello World");
console.log("Admin address:", ADMIN_KEYPAIR.getPublicKey().toSuiAddress());
console.log("Network:", SUI_NETWORK);