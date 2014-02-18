module api.aggregation {

    export class AggregationGroupView extends api.dom.DivEl {

        private name: string;

        private displayName: string;

        private aggregationViews: api.aggregation.AggregationView[] = [];

        private titleEl = new api.dom.H2El();

        private bucketSelectionChangedListeners: Function[] = [];

        private handleAggregationFilter: (aggregation: api.aggregation.Aggregation) => boolean;

        constructor(name: string, displayName: string, aggregations?: api.aggregation.Aggregation[],
                    handleAggregationFilter?: (aggregation: api.aggregation.Aggregation) => boolean) {
            super("aggregation-group-view");

            this.name = name;
            this.displayName = displayName;
            this.handleAggregationFilter = handleAggregationFilter;

            this.titleEl.getEl().setInnerHtml(this.displayName);
            this.appendChild(this.titleEl);

            if (aggregations) {
                aggregations.forEach((aggregation: api.aggregation.Aggregation) => {
                    this.addAggregationView(api.aggregation.AggregationView.createAggregationView(aggregation, this));
                });
            }
        }

        private addAggregationView(aggregationView: api.aggregation.AggregationView) {
            this.appendChild(aggregationView);

            aggregationView.addBucketViewSelectionChangedEventListener((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                    this.notifyBucketViewSelectionChangedEventChanged(event);
                }
            );

            this.aggregationViews.push(aggregationView);
        }

        /*
         * Override this method to give other criteria for this group to display given facet.
         */
        handlesAggregation(aggregation: api.aggregation.Aggregation) {

            if (this.handleAggregationFilter) {
                return this.handleAggregationFilter(aggregation);
            }
            else {
                return aggregation.getName() == this.name;
            }
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

            console.log("reset group", this.name, this.aggregationViews);

            this.aggregationViews.forEach((aggregationView: api.aggregation.AggregationView) => {
                aggregationView.deselectFacet(supressEvent);
            });
        }

        addBucketViewSelectionChangedEventListener(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.bucketSelectionChangedListeners.push(listener);
        }

        removeBucketViewSelectionChangedEventListener(listener: (event: api.aggregation.BucketViewSelectionChangedEvent) => void) {
            this.bucketSelectionChangedListeners = this.bucketSelectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyBucketViewSelectionChangedEventChanged(event: api.aggregation.BucketViewSelectionChangedEvent) {

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
                    if (existingAggregationView instanceof api.aggregation.BucketAggregationView) {

                        var bucketAggregationView: api.aggregation.BucketAggregationView = <api.aggregation.BucketAggregationView>existingAggregationView;
                        bucketAggregationView.update(aggregation);

                    }
                    // else if (existingFacetView instanceof QueryFacetView) {
                    //     var queryFacetView: QueryFacetView = <QueryFacetView>existingFacetView;
                    //     queryFacetView.update(facet);
                    // }
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