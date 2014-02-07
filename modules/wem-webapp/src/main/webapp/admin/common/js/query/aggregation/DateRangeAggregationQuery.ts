module api.query.aggregation {

    export class DateRangeAggregationQuery extends api.query.aggregation.AggregationQuery {

        private fieldName: string;

        private ranges: api.query.aggregation.DateRange[] = [];

        constructor(name: string) {
            super(name);
        }

        public setFieldName(fieldName: string) {
            this.fieldName = fieldName;
        }

        public getFieldName(): string {
            return this.fieldName;
        }

        public addRange(range: api.query.aggregation.DateRange) {

            this.ranges.push(range);
        }

        toJson(): api.query.aggregation.AggregationQueryTypeWrapperJson {

            var json: api.query.aggregation.DateRangeAggregationQueryJson = <api.query.aggregation.DateRangeAggregationQueryJson>super.toAggregationQueryJson();
            json.fieldName = this.getFieldName();
            json.ranges = [];

            this.ranges.forEach((range: api.query.aggregation.DateRange) => {
                json.ranges.push(range.toJson());
            });

            return <api.query.aggregation.AggregationQueryTypeWrapperJson>
            {
                DateRangeAggregationQuery: json
            };
        }


    }


}