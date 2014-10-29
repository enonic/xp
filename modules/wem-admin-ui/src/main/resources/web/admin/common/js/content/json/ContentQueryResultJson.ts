module api.content.json {

    export interface ContentQueryResultJson<T extends ContentIdBaseItemJson> {

        aggregations:api.aggregation.AggregationTypeWrapperJson[];
        contents:T[];
        metadata: api.content.ContentMetadata;
    }
}