module api.aggregation {

    export class BucketFactory {

        public static createFromJson(json: api.aggregation.BucketWrapperJson): api.aggregation.Bucket {

            if (json.DateRangeBucket) {
                return DateRangeBucket.fromDateRangeJson(<api.aggregation.DateRangeBucketJson>json.DateRangeBucket);
            } else if (json.BucketJson) {
                return Bucket.fromJson(<api.aggregation.BucketJson>json.BucketJson);
            } else {
                throw new Error("Bucket-type not recognized: " + json);
            }
        }
    }

}