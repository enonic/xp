module api.security.auth {

    export interface LoginResultJson {

        authenticated: boolean;

        user: api.security.UserJson;

        applications: AdminApplicationJson[];

        principals: string[];

        message?: string;
    }
}