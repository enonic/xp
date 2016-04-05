module api.security {

    export interface PrincipalJson {

        key: string;

        displayName: string;

        modifiedTime?: string;

        description?: string;
    }
}