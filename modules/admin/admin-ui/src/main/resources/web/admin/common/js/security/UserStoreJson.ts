module api.security {

    export interface UserStoreJson {

        displayName: string;
        key: string;
        authConfig?: UserStoreAuthConfigJson;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}