module api.security.acl {

    export interface AccessControlEntryJson {

        principal: PrincipalJson;

        allow: string[];

        deny: string[];

        // inherited?: boolean;

    }
}