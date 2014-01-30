module api.aggregation {

    export class AggregationContainer extends api.dom.DivEl {

        aggregationGroupViews: api.aggregation.AggregationGroupView[] = [];

        private firstSelectedGroupView: api.aggregation.AggregationGroupView;

        constructor() {
            super();
        }

        addAggregationGroupView(aggregationGroupView: api.aggregation.AggregationGroupView) {
            this.appendChild(aggregationGroupView);

            aggregationGroupView.addBucketViewSelectionChangedEventListener((event: api.aggregation.BucketViewSelectionChangedEvent) => {

                if (event.getNewValue() && this.firstSelectedGroupView == null) {
                    this.firstSelectedGroupView = event.getBucketView().getParentAggregationView().getParentGroupView();
                }

            });

            this.aggregationGroupViews.push(aggregationGroupView);
        }

        deselectAll(supressEvent?: boolean) {
            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {
                aggregationGroupView.deselectGroup(supressEvent);
            });
            this.firstSelectedGroupView = null;
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

        updateAggregations(aggregations: api.aggregation.Aggregation[]) {

            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {

                var matchingAggregations: api.aggregation.Aggregation[] = aggregations.filter((current: api.aggregation.Aggregation) => {
                    return aggregationGroupView.handlesAggregation(current);
                });

                if (this.isGroupUpdatable(aggregationGroupView)) {
                    aggregationGroupView.update(matchingAggregations);
                }
            });
        }

        private isGroupUpdatable(aggregationGroupView: api.aggregation.AggregationGroupView) {

            return aggregationGroupView != this.firstSelectedGroupView;
        }


        getSelectedValuesByAggregationName(): {[s:string] : string[];
        } {

            var allValues: { [s:string] : string[];
            } = {};

            this.aggregationGroupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {
                var currValues: {[s:string] : string[];
                } = aggregationGroupView.getSelectedValuesByAggregationName();

                for (var aggregationName in currValues) {
                    var selectedAggregationValues: string[] = currValues[aggregationName];
                    allValues[aggregationName] = selectedAggregationValues;
                }

            });
            return allValues;
        }

    }
}