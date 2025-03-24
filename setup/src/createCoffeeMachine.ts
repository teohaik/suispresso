import {ADMIN_CAP, ADMIN_KEYPAIR, getAdminKeypair, PACKAGE_ID, SUI_NETWORK} from "./config";

import {SuiClient} from "@mysten/sui/client";
import {Transaction} from "@mysten/sui/transactions";

const suiClient = new SuiClient({
    url: SUI_NETWORK!
});


const tx : Transaction = new Transaction();

tx.moveCall({
    target: `${PACKAGE_ID}::suihub_coffee::create_coffee_machine`,
    arguments: [
        tx.object(ADMIN_CAP),
        tx.pure.string("Athens"),
        tx.pure.u64(10000000),
    ],
});


suiClient
    .signAndExecuteTransaction({
        signer: ADMIN_KEYPAIR,
        transaction: tx,
        options: {
            showObjectChanges: true,
            showEffects: true,
        },
    })
    .then(async (res) => {
        const status = res?.effects?.status.status;
        if (status !== "success") {
            throw new Error("Transaction failed");
        }
        console.log("TX Successful, digest = ", res.digest);
        const machineId = res?.effects?.created?.[0].reference.objectId;
        console.log("Coffee Machine ID: ", machineId);
        return res;
    })
    .catch((err) => {
        console.log({ err });
        throw new Error("Error while signing and executing tx");
    });