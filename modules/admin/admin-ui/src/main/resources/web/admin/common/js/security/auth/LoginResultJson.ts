module api.security.auth {

    export interface LoginResultJson {

        authenticated: boolean;

        user: api.security.UserJson;

        principals: string[];

        message?: string;
    }
}