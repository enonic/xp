module api_schema {

    export interface SchemaJson extends api_item.ItemJson {

        qualifiedName: string;

        displayName:string;

        name: string;

        iconUrl: string;

    }

}