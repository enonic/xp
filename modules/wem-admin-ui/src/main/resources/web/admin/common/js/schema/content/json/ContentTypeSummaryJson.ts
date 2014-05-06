module api.schema.content.json{

    export interface ContentTypeSummaryJson extends api.schema.SchemaJson {

        abstract:boolean;

        allowChildContent:boolean;

        contentDisplayNameScript: string;

        final: boolean;

        superType:string;

        owner:string;

        modifier:string;
    }
}