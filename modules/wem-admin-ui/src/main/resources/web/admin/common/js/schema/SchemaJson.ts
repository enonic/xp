module api.schema {

    export interface SchemaJson extends api.item.ItemJson {

        key:string;

        displayName:string;

        description:string;

        name: string;

        iconUrl: string;

        schemaKind:string;

    }
}