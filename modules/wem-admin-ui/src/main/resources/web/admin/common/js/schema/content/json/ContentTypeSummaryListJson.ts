module api.schema.content.json {

    export interface ContentTypeSummaryListJson extends api.schema.SchemaJson {

        total:number;
        totalHits: number;
        hits: number;

        contentTypes:ContentTypeSummaryJson[];
    }
}