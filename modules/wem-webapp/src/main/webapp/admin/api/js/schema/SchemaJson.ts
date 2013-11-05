module api_schema {

    export interface SchemaJson extends api_item.ItemJson {

        displayName:string;

        name: string;

        iconUrl: string;

        schemaKind:string;

    }

}