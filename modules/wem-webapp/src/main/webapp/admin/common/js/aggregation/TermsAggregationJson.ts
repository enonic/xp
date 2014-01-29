module api.aggregation {

    export interface TermsAggregationJson {

        name:string;
        buckets:api.aggregation.BucketJson[];

    }


}