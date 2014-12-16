module api.security.acl {

    export interface UserStoreAccessControlEntryJson {

        access: string;

        principal: PrincipalJson;

    }
}