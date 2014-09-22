module api.schema.content {

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