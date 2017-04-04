module api.security {

    export interface UserStoreJson extends UserItemJson {

        authConfig?: AuthConfigJson;
        idProviderMode: string;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}
