module api.aggregation {

    export class BucketAggregation extends Aggregation {

        private buckets: api.aggregation.Bucket[] = [];

        constructor(name: string) {
            super(name);
        }

        public getBucketByName(name: string): api.aggregation.Bucket {
            for (var i = 0; i < this.buckets.length; i++) {
                var bucket: api.aggregation.Bucket = this.buckets[i];
                if (bucket.getKey() == name) {
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
            fromJson(json: api.aggregation.BucketAggregationJson): BucketAggregation {

            var bucketAggregation: BucketAggregation = new BucketAggregation(json.name);

            json.buckets.forEach((bucketWrapper: api.aggregation.BucketWrapperJson) => {
                bucketAggregation.addBucket(api.aggregation.BucketFactory.createFromJson(bucketWrapper));
            })

            return bucketAggregation;
        }
    }
}
