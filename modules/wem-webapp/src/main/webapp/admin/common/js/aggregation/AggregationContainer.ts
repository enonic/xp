module api.aggregation {

    export class AggregationContainer extends api.dom.DivEl {

        aggregationGroupViews: api.aggregation.AggregationGroupView[];

        constructor() {
            super();
        }

        addAggregationGroupView(aggregationGroupView: api.aggregation.AggregationGroupView) {

            this.appendChild(aggregationGroupView);

            //    aggregationGroupView.addFacetEntrySelectionChangeListener((event: FacetEntryViewSelectionChangedEvent) => {
            //
            //        if (event.getNewValue() && this.firstSelectedGroupView == null) {
            //            this.firstSelectedGroupView = event.getFacetEntryView().getParentFacetView().getParentGroupView();
            //        }
            //    });

            this.aggregationGroupViews.push(aggregationGroupView);
        }

    }

}