module api.aggregation {

    export class DateRangeBucketView extends api.aggregation.BucketView {

        // TODO: Refactor this

        private dateRangeBucket: api.aggregation.DateRangeBucket;

        constructor(bucket: api.aggregation.DateRangeBucket, parentAggregationView: api.aggregation.AggregationView) {
            super(bucket, parentAggregationView);
            this.dateRangeBucket = bucket;
        }

        public getSelectedValue(): string {
            return "from=" + this.dateRangeBucket.getFrom() + ";" + "to=" + this.dateRangeBucket.getTo();
            ;
        }
    }
}