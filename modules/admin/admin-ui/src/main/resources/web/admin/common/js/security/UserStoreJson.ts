module api.security {

    export interface UserStoreJson {

        displayName: string;
        key: string;
        description?: string;
        authConfig?: AuthConfigJson;
        idProviderMode: string;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}
