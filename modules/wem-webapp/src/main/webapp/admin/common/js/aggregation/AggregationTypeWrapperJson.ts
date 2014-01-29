module api.aggregation {

    export interface AggregationTypeWrapperJson {
        terms?:api.aggregation.TermsAggregationJson;
    }
}