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

        getSelectedValues(): string[] {
            var terms: string[] = [];
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                if (bucketView.isSelected()) {
                    terms.push(bucketView.getName());
                }
            });
            return terms;
        }

        deselectFacet(supressEvent?: boolean) {
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                bucketView.deselect(supressEvent);
            });
        }

        update(aggregation: api.aggregation.Aggregation) {

            this.bucketAggregation = <api.aggregation.BucketAggregation> aggregation;

            var anyCountLargerThanZero = false;

            // TODO: This should possible be done differently. The point is to remove
            // buckets from view when they are no longer in the aggregation-result from the query.
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                var existingBucket: api.aggregation.Bucket = this.bucketAggregation.getBucketByName(bucketView.getName());
                if (existingBucket == null) {
                    bucketView.hide();
                }
            });

            this.bucketAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {
                var existingEntry: api.aggregation.BucketView = this.bucketView(bucket.getKey());

                if (existingEntry != null) {
                    existingEntry.update(bucket);
                }

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

        private bucketView(name: string): api.aggregation.BucketView {
            for (var i = 0; i < this.bucketViews.length; i++) {
                var bucketView: api.aggregation.BucketView = this.bucketViews[i];
                if (bucketView.getName() == name) {
                    return bucketView;
                }
            }
            return null;
        }

    }

}