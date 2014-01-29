module api.aggregation {

    export class AggregationEntryViewSelectionChangedEvent {

        private oldValue: boolean;

        private newValue: boolean;

        private bucketView: BucketView;

        constructor(oldValue: boolean, newValue: boolean, bucketView: BucketView) {
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.bucketView = bucketView;
        }

        getOldValue(): boolean {
            return this.oldValue;
        }

        getNewValue(): boolean {
            return this.newValue;
        }

        getBucketView(): BucketView {
            return this.bucketView;
        }
    }
}