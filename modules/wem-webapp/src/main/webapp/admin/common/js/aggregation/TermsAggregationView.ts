module api.aggregation {

    export class TermsAggregationView extends api.aggregation.AggregationView {

        private termsAggregation: api.aggregation.TermsAggregation;

        private bucketViews: api.aggregation.BucketView[] = [];

        private hasAnyCountLargerThanZero: boolean;

        constructor(termsAggregation: api.aggregation.TermsAggregation, parentGroupView: api.aggregation.AggregationGroupView) {

            super(termsAggregation, parentGroupView);

            this.termsAggregation = termsAggregation;

            this.hasAnyCountLargerThanZero = false;

            this.termsAggregation.getBuckets().forEach((bucket: api.aggregation.Bucket) => {
                this.addBucket(new api.aggregation.BucketView(bucket, this));
                if (bucket.getDocCount() > 0) {
                    this.hasAnyCountLargerThanZero = true;
                }
            });

            if (!this.hasAnyCountLargerThanZero) {
                this.hide();
            }
        }

        private addBucket(bucketView: api.aggregation.BucketView) {

            this.appendChild(bucketView);

            // bucketView.addSelectionChangeListener((event:FacetEntryViewSelectionChangedEvent) => {
            //         this.notifyFacetEntrySelectionChanged(event);
            //     }
            // );

            this.bucketViews.push(bucketView);
        }

    }

}