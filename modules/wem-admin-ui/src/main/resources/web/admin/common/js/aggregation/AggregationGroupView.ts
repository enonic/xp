module api.aggregation {

    export class AggregationGroupView extends api.dom.DivEl {

        private name: string;

        private displayName: string;

        private aggregationViews: api.aggregation.AggregationView[] = [];

        private titleEl = new api.dom.H2El();

        private bucketSelectionChangedListeners: Function[] = [];

        constructor(name: string, displayName: string) {
            super("aggregation-group-view");

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

            return aggregation.getName() == this.name;
        }

        getSelectedValuesByAggregationName(): api.aggregation.AggregationSelection[] {

            var aggregationSelections: api.aggregation.AggregationSelection[] = [];

            this.aggregationViews.forEach((bucketAggregationView: api.aggregation.BucketAggregationView) => {

                var selectedBuckets: api.aggregation.Bucket[] = bucketAggregationView.getSelectedValues();

                if (selectedBuckets != null) {
                    var aggregationSelection: api.aggregation.AggregationSelection = new api.aggregation.AggregationSelection(bucketAggregationView.getName());
                    aggregationSelection.setValues(selectedBuckets);

                    aggregationSelections.push(aggregationSelection);
                }
            });

            return aggregationSelections;
        }

        hasSelections(): boolean {
            var hasSelections = false;
            for (var i = 0; i < this.aggregationViews.length; i++) {
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
            this.bucketSelectionChangedListeners = this.bucketSelectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyBucketViewSelectionChanged(event: api.aggregation.BucketViewSelectionChangedEvent) {

            this.bucketSelectionChangedListeners.forEach((listener: (event: BucketViewSelectionChangedEvent) => void) => {
                listener(event);
            });
        }

        update(aggregations: api.aggregation.Aggregation[]) {

            aggregations.forEach((aggregation: api.aggregation.Aggregation) => {

                var existingAggregationView: api.aggregation.AggregationView = this.getAggregationView(aggregation.getName());

                if (existingAggregationView == null) {
                    this.addAggregationView(api.aggregation.AggregationView.createAggregationView(aggregation, this));
                }
                else {
                    if (api.ObjectHelper.iFrameSafeInstanceOf(existingAggregationView, api.aggregation.BucketAggregationView)) {

                        var bucketAggregationView: api.aggregation.BucketAggregationView = <api.aggregation.BucketAggregationView>existingAggregationView;
                        bucketAggregationView.update(aggregation);
                    }
                    // Here be Metric-aggregations
                }
            });
        }

        private getAggregationView(name: string): api.aggregation.AggregationView {

            for (var i = 0; i < this.aggregationViews.length; i++) {
                var aggregationView: api.aggregation.AggregationView = this.aggregationViews[i];
                if (aggregationView.getName() == name) {
                    return aggregationView;
                }
            }
            return null;
        }
    }

}