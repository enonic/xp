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

                this.addBucket(api.aggregation.BucketViewFactory.createBucketView(bucket, this));

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

        getSelectedValues(): string[] {
            var selectedValues: string[] = [];
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                if (bucketView.isSelected()) {
                    selectedValues.push(bucketView.getSelectedValue());
                }
            });
            return selectedValues;
        }

        deselectFacet(supressEvent?: boolean) {
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                bucketView.deselect(supressEvent);
            });
        }

        update(aggregation: api.aggregation.Aggregation) {

            this.bucketAggregation = <api.aggregation.BucketAggregation> aggregation;

            this.bucketViews = [];
            this.removeChildren();

            var anyCountLargerThanZero = false;

            this.bucketAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {

                this.addBucket(api.aggregation.BucketViewFactory.createBucketView(bucket, this));

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

    }

}