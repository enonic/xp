module api.security {

    export interface UserJson extends PrincipalJson {

        email: string;

        login: string;

        disabled: boolean;

    }
}