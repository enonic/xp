module app.launcher.json {
    export interface AdminApplicationJson {

        key:AdminApplicationKeyJson;

        name:string;

        shortName:string;

        iconUrl:string;

        allowedPrincipals: string[];
    }
}