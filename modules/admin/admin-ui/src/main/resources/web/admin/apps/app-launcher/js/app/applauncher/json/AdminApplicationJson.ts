module app.launcher.json {

    export interface AdminApplicationJson extends api.item.ItemJson {

        key:string;

        name:string;

        shortName:string;

        iconUrl:string;

        allowedPrincipals: string[];
    }
}