module api.security {

    export interface SynchUserStoreResultJson {

        userStoreKey: string;

        synchronized: boolean;

        reason: string;

    }
}