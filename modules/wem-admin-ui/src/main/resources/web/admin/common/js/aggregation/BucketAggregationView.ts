module api.aggregation {

    export class BucketAggregationView extends api.aggregation.AggregationView {

        private bucketAggregation: api.aggregation.BucketAggregation;

        private bucketViews: api.aggregation.BucketView[] = [];

        private showBucketView: boolean;

        constructor(bucketAggregation: api.aggregation.BucketAggregation, parentGroupView: api.aggregation.AggregationGroupView) {
            super(bucketAggregation, parentGroupView);

            this.bucketAggregation = bucketAggregation;

            this.showBucketView = false;
            this.bucketAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {
                this.addBucket(new api.aggregation.BucketView(bucket, this, false, this.getDisplayNameForName(bucket.getKey())));
                if (bucket.getDocCount() > 0) {
                    this.showBucketView = true;
                }

            });

            if (!this.showBucketView) {
                this.hide();
            }
        }

        setDisplayNames(): void {
            this.bucketViews.forEach((bucketView: api.aggregation.BucketView) => {
                bucketView.setDisplayName(this.getDisplayNameForName(bucketView.getName()));
            })
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
            bucketView.onSelectionChanged((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                    this.notifyBucketViewSelectionChanged(event);
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

            var anyBucketVisible = false;

            this.bucketAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {

                var wasSelected: boolean = (wemjq.inArray(bucket.getKey(), selectedBucketNames)) > -1;

                var bucketView: api.aggregation.BucketView = new api.aggregation.BucketView(bucket, this, wasSelected,
                    this.getDisplayNameForName(bucket.getKey()));

                this.addBucket(bucketView);

                if (bucket.getDocCount() > 0 || wasSelected) {
                    anyBucketVisible = true;
                }

            });

            this.showBucketView = anyBucketVisible;

            if (!this.showBucketView) {
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