module api.aggregation {

    export class AggregationView extends api.dom.DivEl {

        private parentGroupView: api.aggregation.AggregationGroupView;

        private aggregation: api.aggregation.Aggregation;

        private facetEntrySelectionChangedListeners: Function[] = [];

        constructor(aggregation: api.aggregation.Aggregation, parentGroupView: api.aggregation.AggregationGroupView) {
            super('facet-view');
            this.aggregation = aggregation;
            this.parentGroupView = parentGroupView;
        }

        getParentGroupView() {
            return this.parentGroupView;
        }

        getName(): string {
            return this.aggregation.getName();
        }

        deselectFacet(supressEvent?: boolean) {
            throw new Error("Must be implemented by inheritor");
        }

        hasSelectedEntry(): boolean {
            throw new Error("Must be implemented by inheritor");
        }

        getSelectedValues(): string[] {
            throw new Error("Must be implemented by inheritor");
        }

        update(aggregation: api.aggregation.Aggregation) {
            throw new Error("Must be implemented by inheritor");
        }

        /*
         addFacetEntrySelectionChangeListener(listener: (event: FacetEntryViewSelectionChangedEvent) => void) {
         this.facetEntrySelectionChangedListeners.push(listener);
         }

         removeFacetEntrySelectionChangedListener(listener: (event: FacetEntryViewSelectionChangedEvent) => void) {
         this.facetEntrySelectionChangedListeners = this.facetEntrySelectionChangedListeners.filter(function (curr) {
         return curr != listener;
         });
         }

         notifyFacetEntrySelectionChanged(event: FacetEntryViewSelectionChangedEvent) {

         this.facetEntrySelectionChangedListeners.forEach((listener: (event: FacetEntryViewSelectionChangedEvent) => void) => {
         listener(event);
         });
         }
         */

        static createAggregationView(aggregation: api.aggregation.Aggregation,
                                     parentGroupView: api.aggregation.AggregationGroupView): api.aggregation.AggregationView {
            if (aggregation instanceof api.aggregation.TermsAggregation) {
                return new api.aggregation.TermsAggregationView(<api.aggregation.TermsAggregation>aggregation, parentGroupView);
            }
            else {
                throw Error("Creating AggregationView of this type of Aggregation is not supported: " + aggregation);
            }
        }
    }


}