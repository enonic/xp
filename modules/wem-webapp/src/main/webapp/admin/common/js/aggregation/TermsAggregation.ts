module api.aggregation {

    export class TermsAggregation extends Aggregation {

        private buckets: api.aggregation.Bucket[] = [];

        constructor(name: string) {
            super(name);
        }

        public getBucketByName(name: string): api.aggregation.Bucket {
            for (var i = 0; i < this.buckets.length; i++) {
                var bucket: api.aggregation.Bucket = this.buckets[i];
                if (bucket.getName() == name) {
                    return bucket;
                }
            }
            return null;
        }

        public
            getBuckets(): api.aggregation.Bucket[] {
            return this.buckets;
        }

        public
            addBucket(bucket: api.aggregation.Bucket) {
            this.buckets.push(bucket);
        }

        public static
            fromJson(json: api.aggregation.TermsAggregationJson): TermsAggregation {

            var termsAggregation: TermsAggregation = new TermsAggregation(json.name);

            json.buckets.forEach((bucket: api.aggregation.BucketJson) => {
                termsAggregation.addBucket(api.aggregation.Bucket.fromJson(bucket));
            })

            return termsAggregation;
        }
    }
}
