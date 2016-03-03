module api.security {

    export interface UserStoreJson {

        displayName: string;
        key: string;
        authConfig?: AuthConfigJson;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}