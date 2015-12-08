module api.security.auth {

    export interface AdminApplicationJson extends api.item.ItemJson {

        key:string;

        name:string;

        shortName:string;

        icon:string;

        iconImage:AdminApplicationIconJson;
    }

    export interface AdminApplicationIconJson {

        application:string;

        path:string;
    }
}