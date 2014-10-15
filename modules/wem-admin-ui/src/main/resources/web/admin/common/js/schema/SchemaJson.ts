module api.schema {

    export interface SchemaJson extends api.item.ItemJson {

        displayName:string;

        description:string;

        name: string;

        iconUrl: string;
    }
}