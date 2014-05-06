module api.query.aggregation {

    export interface DateRangeAggregationQueryJson {

        name: string;
        fieldName: string;
        ranges:api.query.aggregation.DateRangeJson[];

    }
}