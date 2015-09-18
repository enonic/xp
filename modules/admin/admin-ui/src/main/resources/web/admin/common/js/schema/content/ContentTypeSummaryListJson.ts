module api.schema.content {

    export interface ContentTypeSummaryListJson extends api.schema.SchemaJson {

        total:number;
        totalHits: number;
        hits: number;

        contentTypes:ContentTypeSummaryJson[];
    }
}