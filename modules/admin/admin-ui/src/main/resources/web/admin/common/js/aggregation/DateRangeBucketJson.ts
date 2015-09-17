module api.aggregation {

    export interface DateRangeBucketJson extends api.aggregation.BucketJson {
        from:Date;
        to:Date;
    }

}