module api.content {

    export interface ContentQueryResult<T> {

        aggregations:api.aggregation.AggregationTypeWrapperJson[];
        contents:T[];
    }
}