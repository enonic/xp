module api.security.acl {

    export interface AccessControlEntryJson {

        principal: { displayName: string; key: string; };

        allow: string[];

        deny: string[];

    }
}