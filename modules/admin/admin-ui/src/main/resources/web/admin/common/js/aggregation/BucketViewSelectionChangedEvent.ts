module api.aggregation {

    export class BucketViewSelectionChangedEvent {

        private oldValue: boolean;

        private newValue: boolean;

        private bucketView: api.aggregation.BucketView;

        constructor(oldValue: boolean, newValue: boolean, bucketView: api.aggregation.BucketView) {
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

        getBucketView(): api.aggregation.BucketView {
            return this.bucketView;
        }
    }
}