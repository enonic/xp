module api.query.aggregation {

    export class DateRange extends Range {

        private to: string;
        private from: string;

        constructor(from: string, to: string, key?: string) {
            super(key);
            this.from = from;
            this.to = to;
        }

        public setTo(to: string) {
            this.to = to;
        }

        public setFrom(from: string) {
            this.from = from;
        }

        public setToDate(to: Date) {
            this.to = to.toISOString();
        }

        public setFromDate(from: Date) {
            this.from = from.toISOString();
        }

        public toJson(): api.query.aggregation.DateRangeJson {

            var json: api.query.aggregation.DateRangeJson = <api.query.aggregation.DateRangeJson>super.toRangeJson();

            json.from = this.from;
            json.to = this.to;

            return json;
        }
    }

}

