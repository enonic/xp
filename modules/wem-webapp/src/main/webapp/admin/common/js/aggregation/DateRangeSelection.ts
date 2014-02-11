module api.aggregation {

    export class DateRangeSelection {

        private from: Date;
        private to: Date;

        constructor(from: Date, to: Date) {
            this.from = from;
            this.to = to;
        }
    }
}