module api.security.auth {

    export interface AdminApplicationJson extends api.item.ItemJson {

        key:string;

        name:string;

        shortName:string;

        iconUrl:string;
    }
}