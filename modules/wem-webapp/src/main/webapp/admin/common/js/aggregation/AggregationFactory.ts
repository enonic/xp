module api.aggregation {

    export class AggregationFactory {

        public static createFromJson(json: api.aggregation.AggregationTypeWrapperJson): api.aggregation.Aggregation {

            if (json.terms) {
                return api.aggregation.TermsAggregation.fromJson(<api.aggregation.TermsAggregationJson>json.terms);
            } else {
                throw new Error("Aggregation type not recognized: " + json);
            }
        }
    }
}