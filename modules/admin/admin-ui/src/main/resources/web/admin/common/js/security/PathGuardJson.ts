module api.security {

    export interface PathGuardJson {

        key: string;
        displayName: string;
        description: string;
        authConfig?: AuthConfigJson;
        paths: string[];
    }
}