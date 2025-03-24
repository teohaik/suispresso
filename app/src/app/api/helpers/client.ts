import clientConfig from "@/config/clientConfig";
import { SuiClient } from "@mysten/sui/client";

export const suiClient = new SuiClient({
  url: clientConfig.NEXT_PUBLIC_SUI_NETWORK,
});


suiClient.queryTransactionBlocks()