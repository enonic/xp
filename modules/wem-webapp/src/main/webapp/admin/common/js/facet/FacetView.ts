module api_facet {

    export class FacetView extends api_dom.DivEl {

        private parentGroupView:FacetGroupView;

        private facet:Facet;

        private facetEntrySelectionChangedListeners:Function[] = [];

        constructor(facet:Facet, parentGroupView:FacetGroupView) {
            super('FacetView', 'facet-view');
            this.facet = facet;
            this.parentGroupView = parentGroupView;
        }

        getParentGroupView() {
            return this.parentGroupView;
        }

        getName():string {
            return this.facet.getName();
        }

        deselectFacet(supressEvent?:boolean) {
            throw new Error("Must be implemented by inheritor");
        }

        hasSelectedEntry():boolean {
            throw new Error("Must be implemented by inheritor");
        }

        getSelectedValues():string[] {
            throw new Error("Must be implemented by inheritor");
        }

        update(facet:Facet) {
            throw new Error("Must be implemented by inheritor");
        }

        addFacetEntrySelectionChangeListener(listener:(event:FacetEntryViewSelectionChangedEvent) => void) {
            this.facetEntrySelectionChangedListeners.push(listener);
        }

        removeFacetEntrySelectionChangedListener(listener:(event:FacetEntryViewSelectionChangedEvent) => void) {
            this.facetEntrySelectionChangedListeners = this.facetEntrySelectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyFacetEntrySelectionChanged(event:FacetEntryViewSelectionChangedEvent) {

            this.facetEntrySelectionChangedListeners.forEach((listener:(event:FacetEntryViewSelectionChangedEvent) => void) => {
                listener(event);
            });
        }

        static createFacetView(facet:Facet, parentGroupView:FacetGroupView):FacetView {
            if (facet instanceof TermsFacet) {
                return new TermsFacetView(<TermsFacet>facet, parentGroupView);
            }
            else if (facet instanceof QueryFacet) {
                return new QueryFacetView(<QueryFacet>facet, parentGroupView);
            }
            else {
                throw Error("Creating FacetView of this type of Facet is not supported: " + facet);
            }
        }
    }

}