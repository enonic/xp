module api_schema_content_json{

    export interface ContentTypeSummaryListJson extends api_schema.SchemaJson {

        total:number;

        contentTypes:ContentTypeSummaryJson[];
    }
}