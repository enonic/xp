module api.aggregation {

    export class AggregationFactory {

        public static createFromJson(json: api.aggregation.AggregationTypeWrapperJson): api.aggregation.Aggregation {

            if (json.BucketAggregation) {
                return api.aggregation.BucketAggregation.fromJson(<api.aggregation.BucketAggregationJson>json.BucketAggregation);
            } else {
                throw new Error("Aggregation type not recognized: " + json);
            }
        }
    }
}