module api.aggregation {

    export class BucketAggregationView extends api.aggregation.AggregationView {

        private bucketAggregation: api.aggregation.BucketAggregation;

        private bucketViews: api.aggregation.BucketView[] = [];

        private hasAnyCountLargerThanZero: boolean;

        constructor(bucketAggregation: api.aggregation.BucketAggregation, parentGroupView: api.aggregation.AggregationGroupView) {
            super(bucketAggregation, parentGroupView);

            this.bucketAggregation = bucketAggregation;

            this.hasAnyCountLargerThanZero = false;
            this.bucketAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {

                this.addBucket(new api.aggregation.BucketView(bucket, this));

                if (bucket.getDocCount() > 0) {
                    this.hasAnyCountLargerThanZero = true;
                }

            });

            if (!this.hasAnyCountLargerThanZero) {
                this.hide();
            }
        }

        hasSelectedEntry(): boolean {
            var isSelected: boolean = false;
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                if (bucketView.isSelected()) {
                    isSelected = true;
                }
            });
            return isSelected;
        }

        private addBucket(bucketView: api.aggregation.BucketView) {
            this.appendChild(bucketView);
            bucketView.addSelectionChangeListener((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                    this.notifyBucketViewSelectionChangedEvent(event);
                }
            );
            this.bucketViews.push(bucketView);
        }

        getSelectedValues(): api.aggregation.Bucket[] {

            var selectedBuckets: api.aggregation.Bucket[] = [];

            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                if (bucketView.isSelected()) {
                    selectedBuckets.push(bucketView.getBucket());
                }
            });

            return selectedBuckets;
        }

        deselectFacet(supressEvent?: boolean) {
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                bucketView.deselect(supressEvent);
            });
        }

        update(aggregation: api.aggregation.Aggregation) {

            var selectedBucketNames: string[] = this.getSelectedBucketNames();

            this.bucketAggregation = <api.aggregation.BucketAggregation> aggregation;
            this.bucketViews = [];
            this.removeChildren();

            var anyCountLargerThanZero = false;

            this.bucketAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {

                var wasSelected: boolean = (jQuery.inArray(bucket.getKey(), selectedBucketNames)) > -1;

                var bucketView: api.aggregation.BucketView = new api.aggregation.BucketView(bucket, this, wasSelected);
                this.addBucket(bucketView);

                if (bucket.getDocCount() > 0) {
                    anyCountLargerThanZero = true;
                }

            });

            this.hasAnyCountLargerThanZero = anyCountLargerThanZero;

            if (!this.hasAnyCountLargerThanZero) {
                this.hide();
            }
            else if (!this.isVisible()) {
                this.show();
            }
        }

        private getSelectedBucketNames(): string[] {

            var selectedBucketNames: string[] = [];

            var selectedBuckets: api.aggregation.Bucket[] = this.getSelectedValues();

            selectedBuckets.forEach((bucket: api.aggregation.Bucket) => {
                selectedBucketNames.push(bucket.getKey());
            });

            return selectedBucketNames;
        }
    }
}