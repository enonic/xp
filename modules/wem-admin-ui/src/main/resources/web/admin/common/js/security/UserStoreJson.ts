module api.security.json {

    export interface UserStoreJson extends api.item.ItemJson {
        name:string;
        key:UserStoreKey;

    }
}