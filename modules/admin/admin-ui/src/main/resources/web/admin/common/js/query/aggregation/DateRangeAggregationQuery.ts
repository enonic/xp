module api.query.aggregation {

    export class DateRangeAggregationQuery extends AggregationQuery {

        private fieldName: string;

        private ranges: DateRange[] = [];

        constructor(name: string) {
            super(name);
        }

        public setFieldName(fieldName: string) {
            this.fieldName = fieldName;
        }

        public getFieldName(): string {
            return this.fieldName;
        }

        public addRange(range: DateRange) {

            this.ranges.push(range);
        }

        toJson(): AggregationQueryTypeWrapperJson {

            var json: DateRangeAggregationQueryJson = <DateRangeAggregationQueryJson>super.toAggregationQueryJson();
            json.fieldName = this.getFieldName();
            json.ranges = [];

            this.ranges.forEach((range: DateRange) => {
                json.ranges.push(range.toJson());
            });

            return <AggregationQueryTypeWrapperJson>
            {
                DateRangeAggregationQuery: json
            };
        }


    }


}