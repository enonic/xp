module api.aggregation {

    export interface BucketAggregationJson {

        name:string;
        buckets:api.aggregation.BucketWrapperJson[];

    }


}