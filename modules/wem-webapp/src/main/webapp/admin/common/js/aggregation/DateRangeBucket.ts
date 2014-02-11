module api.aggregation {

    export class DateRangeBucket extends Bucket {

        private from: Date;
        private to: Date;

        constructor(key: string, docCount: number) {
            super(key, docCount);
        }

        public getFrom(): Date {
            return this.from;
        }

        public getTo(): Date {
            return this.to;
        }

        public static fromDateRangeJson(json: api.aggregation.DateRangeBucketJson): DateRangeBucket {

            var dateRangeBucket: api.aggregation.DateRangeBucket = new api.aggregation.DateRangeBucket(json.key, json.docCount);
            dateRangeBucket.from = json.from;
            dateRangeBucket.to = json.to;

            return dateRangeBucket;
        }

    }

}