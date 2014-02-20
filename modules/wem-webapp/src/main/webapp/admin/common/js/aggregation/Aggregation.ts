
module api.aggregation {

    export class Aggregation {

        private name: string;

        constructor(name: string) {
            this.name = name;
        }

        public getName(): string {
            return this.name;
        }

        public static fromJsonArray(aggregationWrapperJsons: AggregationTypeWrapperJson[]) : Aggregation[] {

            var aggregations: Aggregation[] = [];

            aggregationWrapperJsons.forEach((aggregationJson: AggregationTypeWrapperJson) => {
                aggregations.push(AggregationFactory.createFromJson(aggregationJson));
            });

            return aggregations;
        }

    }
}
