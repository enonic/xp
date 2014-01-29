module api.aggregation {

    export class TermsAggregation extends Aggregation {

        private buckets: api.aggregation.Bucket[] = [];

        constructor(name: string) {
            super(name);
        }

        public getBuckets(): api.aggregation.Bucket[] {
            return this.buckets;
        }

        public addBucket(bucket: api.aggregation.Bucket) {
            this.buckets.push(bucket);
        }

        public static fromJson(json: api.aggregation.TermsAggregationJson): TermsAggregation {

            var termsAggregation: TermsAggregation = new TermsAggregation(json.name);

            json.buckets.forEach((bucket: api.aggregation.BucketJson) => {
                termsAggregation.addBucket(api.aggregation.Bucket.fromJson(bucket));
            })

            return termsAggregation;
        }
    }
}
