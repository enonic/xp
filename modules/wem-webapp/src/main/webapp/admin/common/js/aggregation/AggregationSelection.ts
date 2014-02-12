module api.aggregation {

    export class AggregationSelection {

        name: string;
        selectedBuckets: api.aggregation.Bucket[];

        constructor(name: string) {
            this.name = name;
        }

        public setValues(selectedBuckets: api.aggregation.Bucket[]) {
            this.selectedBuckets = selectedBuckets;
        }

        public getName(): string {
            return this.name;
        }

        public getSelectedBuckets(): api.aggregation.Bucket[] {
            return this.selectedBuckets;
        }

    }


}