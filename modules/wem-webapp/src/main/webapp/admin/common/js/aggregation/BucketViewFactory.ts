module api.aggregation {

    export class BucketViewFactory {

        public static createBucketView(bucket: api.aggregation.Bucket,
                                       parentView: api.aggregation.AggregationView): api.aggregation.BucketView {

            if (bucket instanceof api.aggregation.DateRangeBucket) {
                return new api.aggregation.DateRangeBucketView(<DateRangeBucket>bucket, parentView);
            } else {
                return new api.aggregation.BucketView(bucket, parentView);
            }
        }
    }
}