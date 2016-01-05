module api.security {

    export interface UserStoreJson {

        displayName: string;
        key: string;
        authServiceKey: string;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}