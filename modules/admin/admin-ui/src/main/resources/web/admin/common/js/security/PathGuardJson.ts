module api.security {

    export interface PathGuardJson {

        key: string;
        displayName: string;
        authConfig?: AuthConfigJson;
        paths: string[];
    }
}