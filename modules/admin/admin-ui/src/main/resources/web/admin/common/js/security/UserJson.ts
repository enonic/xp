module api.security {

    export interface UserJson extends PrincipalJson {

        email: string;

        login: string;

        loginDisabled: boolean;

        memberships?: PrincipalJson[];

    }
}