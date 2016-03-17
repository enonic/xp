module api.security {

    export interface PathGuardJson {

        key: string;
        displayName: string;
        description: string;
        userStoreKey: string;
        passive: boolean;
        paths: string[];
    }
}