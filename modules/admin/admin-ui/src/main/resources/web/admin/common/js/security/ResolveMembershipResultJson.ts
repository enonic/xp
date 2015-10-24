module api.security {

    export interface ResolveMembershipResultJson {

        principalKey: string;

        members: string[];

    }
}