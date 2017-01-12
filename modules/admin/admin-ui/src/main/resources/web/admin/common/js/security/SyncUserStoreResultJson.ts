module api.security {

    export interface SyncUserStoreResultJson {

        userStoreKey: string;

        synchronized: boolean;

        reason: string;

    }
}
