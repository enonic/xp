module api.aggregation {

    export class AggregationGroupView extends api.dom.DivEl {

        private name: string;

        private aggregationViews: api.aggregation.AggregationView[] = [];

        private titleEl = new api.dom.H2El();

        constructor(name: string, aggregations?: api.aggregation.Aggregation[]) { //, handleFacetFilter?:(facet:Facet) => boolean) {
            super("aggregation-group-view");

            this.name = name;

            this.titleEl.getEl().setInnerHtml(this.name);
            this.appendChild(this.titleEl);

            if (aggregations) {
                aggregations.forEach((aggregation: api.aggregation.Aggregation) => {
                    this.addAggregationView(api.aggregation.AggregationView.createAggregationView(aggregation, this));
                });
            }
        }


        private addAggregationView(aggregationView: api.aggregation.AggregationView) {
            this.appendChild(aggregationView);
            // aggregationView.addFacetEntrySelectionChangeListener((event: FacetEntryViewSelectionChangedEvent) => {
            //         this.notifyFacetEntrySelectionChanged(event);
            //     }
            // );
            this.aggregationViews.push(aggregationView);
        }


    }

}