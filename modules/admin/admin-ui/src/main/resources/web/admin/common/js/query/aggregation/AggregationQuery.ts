module api.query.aggregation {

    export class AggregationQuery {

        private name: string;

        toJson(): api.query.aggregation.AggregationQueryTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

        toAggregationQueryJson(): api.query.aggregation.AggregationQueryJson {

            return {
                "name": this.getName()
            };
        }

        constructor(name: string) {
            this.name = name;
        }

        public getName(): string {
            return this.name;
        }

    }
}
