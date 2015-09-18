module api.aggregation {

    export class AggregationContainer extends api.dom.DivEl {

        aggregationGroupViews: api.aggregation.AggregationGroupView[] = [];

        private lastSelectedGroupView: api.aggregation.AggregationGroupView;

        constructor() {
            super();
        }

        addAggregationGroupView(aggregationGroupView: api.aggregation.AggregationGroupView) {
            this.appendChild(aggregationGroupView);

            aggregationGroupView.onBucketViewSelectionChanged((event: api.aggregation.BucketViewSelectionChangedEvent) => {

                if (event.getNewValue()) {
                    this.lastSelectedGroupView = event.getBucketView().getParentAggregationView().getParentGroupView();
                }
            });

            this.aggregationGroupViews.push(aggregationGroupView);
        }

        deselectAll(supressEvent?: boolean) {
            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {
                aggregationGroupView.deselectGroup(supressEvent);
            });

            this.lastSelectedGroupView = null;
        }

        hasSelectedBuckets(): boolean {
            var hasSelected: boolean = false;
            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {
                if (aggregationGroupView.hasSelections()) {
                    hasSelected = true;
                }
            });
            return hasSelected;
        }

        updateAggregations(aggregations: api.aggregation.Aggregation[], doUpdateAll?: boolean) {

            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {

                var matchingAggregations: api.aggregation.Aggregation[] = aggregations.filter((current: api.aggregation.Aggregation) => {
                    return aggregationGroupView.handlesAggregation(current);
                });

                if (doUpdateAll || this.isGroupUpdatable(aggregationGroupView)) {
                    aggregationGroupView.update(matchingAggregations);
                }
            });
        }

        private isGroupUpdatable(aggregationGroupView: api.aggregation.AggregationGroupView) {
            return aggregationGroupView != this.lastSelectedGroupView;
        }

        getSelectedValuesByAggregationName(): api.aggregation.AggregationSelection[] {
            var aggregationSelections: api.aggregation.AggregationSelection[] = [];

            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {
                var selectedValuesByAggregationName = aggregationGroupView.getSelectedValuesByAggregationName();
                aggregationSelections = aggregationSelections.concat(selectedValuesByAggregationName);

            });

            return aggregationSelections;
        }
    }
}