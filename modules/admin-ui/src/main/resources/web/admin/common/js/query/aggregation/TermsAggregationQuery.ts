module api.query.aggregation {

    export class TermsAggregationQuery extends AggregationQuery {

        public static TERM_DEFAULT_SIZE: number = 10;

        private fieldName: string;

        private size: number = TermsAggregationQuery.TERM_DEFAULT_SIZE;

        private orderByDirection: string = TermsAggregationOrderDirection.ASC;

        private orderByType: string = TermsAggregationOrderType.TERM;

        toJson(): api.query.aggregation.AggregationQueryTypeWrapperJson {

            var json: api.query.aggregation.TermsAggregationQueryJson = <api.query.aggregation.TermsAggregationQueryJson>super.toAggregationQueryJson();
            json.fieldName = this.getFieldName();
            json.size = this.getSize();
            json.orderByDirection = this.orderByDirection;
            json.orderByType = this.orderByType;

            return <api.query.aggregation.AggregationQueryTypeWrapperJson> {
                TermsAggregationQuery: json
            };
        }

        constructor(name: string) {
            super(name);
        }

        public setFieldName(fieldName: string) {
            this.fieldName = fieldName;
        }

        public getFieldName(): string {
            return this.fieldName;
        }

        public setSize(size: number) {
            this.size = size;
        }

        public getSize(): number {
            return this.size;
        }
    }

    export class TermsAggregationOrderDirection {
        public static ASC: string = "ASC";
        public static DESC: string = "DESC";
    }

    export class TermsAggregationOrderType {
        public static DOC_COUNT: string = "DOC_COUNT";
        public static TERM: string = "TERM";
    }

}
