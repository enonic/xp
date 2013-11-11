module api_schema {

    export interface SchemaJson extends api_item.ItemJson {

        key:string;

        displayName:string;

        name: string;

        iconUrl: string;

        schemaKind:string;

    }

}