module api.security {

    export interface DeletePrincipalResultJson {

        principalKey: string;

        deleted: boolean;

        reason: string;

    }
}