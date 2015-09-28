module api.query.aggregation {

    export interface AggregationQueryTypeWrapperJson {

        TermsAggregationQuery?:api.query.aggregation.TermsAggregationQueryJson;
        DateRangeAggregationQuery?:api.query.aggregation.DateRangeAggregationQueryJson;

    }

}
