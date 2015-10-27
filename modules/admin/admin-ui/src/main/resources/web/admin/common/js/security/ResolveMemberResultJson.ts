module api.security {

    export interface ResolveMemberResultJson {

        principalKey: string;

        members: PrincipalsJson;

    }
}