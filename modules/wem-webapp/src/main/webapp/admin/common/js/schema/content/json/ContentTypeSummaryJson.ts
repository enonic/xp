module api_schema_content_json{

    export interface ContentTypeSummaryJson extends api_schema.SchemaJson {

        abstract:boolean;

        allowChildContent:boolean;

        contentDisplayNameScript: string;

        final: boolean;

        superType:string;

        owner:string;

        modifier:string;
    }
}