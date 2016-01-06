module api.security {

    export interface UserStoreJson {

        displayName: string;
        key: string;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}