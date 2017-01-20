module api.aggregation {

    import BucketAggregationView = api.aggregation.BucketAggregationView;
    import AggregationSelection = api.aggregation.AggregationSelection;

    export class AggregationGroupView extends api.dom.DivEl {

        private name: string;

        private displayName: string;

        private aggregationViews: api.aggregation.AggregationView[] = [];

        private titleEl: api.dom.H2El = new api.dom.H2El();

        private bucketSelectionChangedListeners: Function[] = [];

        constructor(name: string, displayName: string) {
            super('aggregation-group-view');

            this.name = name;
            this.displayName = displayName;

            this.titleEl.getEl().setInnerHtml(this.displayName);
            this.appendChild(this.titleEl);
        }

        private addAggregationView(aggregationView: api.aggregation.AggregationView) {
            this.appendChild(aggregationView);

            aggregationView.onBucketViewSelectionChanged((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                    this.notifyBucketViewSelectionChanged(event);
                }
            );

            this.aggregationViews.push(aggregationView);
        }

        initialize(): void {
            // must be implemented by children
        }

        getAggregationViews(): api.aggregation.AggregationView[] {
            return this.aggregationViews;
        }

        getName(): string {
            return this.name;
        }

        /*
         * Override this method to give other criteria for this group to display given facet.
         */
        handlesAggregation(aggregation: api.aggregation.Aggregation) {

            return aggregation.getName() === this.name;
        }

        getSelectedValuesByAggregationName(): AggregationSelection[] {

            let aggregationSelections: AggregationSelection[] = [];

            this.aggregationViews.forEach((bucketAggregationView: BucketAggregationView) => {

                let selectedBuckets: api.aggregation.Bucket[] = bucketAggregationView.getSelectedValues();

                if (selectedBuckets != null) {
                    let aggregationSelection: AggregationSelection = new AggregationSelection(bucketAggregationView.getName());
                    aggregationSelection.setValues(selectedBuckets);

                    aggregationSelections.push(aggregationSelection);
                }
            });

            return aggregationSelections;
        }

        hasSelections(): boolean {
            let hasSelections = false;
            for (let i = 0; i < this.aggregationViews.length; i++) {
                if (this.aggregationViews[i].hasSelectedEntry()) {
                    hasSelections = true;
                    break;
                }
            }
            return hasSelections;
        }

        deselectGroup(supressEvent?: boolean) {

            this.aggregationViews.forEach((aggregationView: api.aggregation.AggregationView) => {
                aggregationView.deselectFacet(supressEvent);
            });
        }

        onBucketViewSelectionChanged(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.bucketSelectionChangedListeners.push(listener);
        }

        unBucketViewSelectionChanged(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.bucketSelectionChangedListeners = this.bucketSelectionChangedListeners
                .filter(function (curr: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
                    return curr !== listener;
                });
        }

        notifyBucketViewSelectionChanged(event: api.aggregation.BucketViewSelectionChangedEvent) {

            this.bucketSelectionChangedListeners.forEach((listener: (event: BucketViewSelectionChangedEvent) => void) => {
                listener(event);
            });
        }

        update(aggregations: api.aggregation.Aggregation[]) {

            aggregations.forEach((aggregation: api.aggregation.Aggregation) => {

                let existingAggregationView: api.aggregation.AggregationView = this.getAggregationView(aggregation.getName());

                if (existingAggregationView == null) {
                    this.addAggregationView(api.aggregation.AggregationView.createAggregationView(aggregation, this));
                } else {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(existingAggregationView, BucketAggregationView)) {

                        let bucketAggregationView: BucketAggregationView = <BucketAggregationView>existingAggregationView;
                        bucketAggregationView.update(aggregation);
                    }
                    // Here be Metric-aggregations
                }
            });
        }

        private getAggregationView(name: string): api.aggregation.AggregationView {

            for (let i = 0; i < this.aggregationViews.length; i++) {
                let aggregationView: api.aggregation.AggregationView = this.aggregationViews[i];
                if (aggregationView.getName() === name) {
                    return aggregationView;
                }
            }
            return null;
        }
    }

}
