module api.security {

    export interface UserStoreJson extends api.item.ItemJson {

        displayName: string;
        key: string;
        authConfig?: AuthConfigJson;
        permissions?: api.security.acl.UserStoreAccessControlEntryJson[];
    }
}