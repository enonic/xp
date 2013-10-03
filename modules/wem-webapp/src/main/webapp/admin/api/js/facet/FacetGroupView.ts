module api_facet {

    export class FacetGroupView extends api_dom.DivEl {

        private name:string;

        private displayName:string;

        private facetViews:FacetView[] = [];

        private titleEl = new api_dom.H2El();

        private facetEntrySelectionChangedListeners:Function[] = [];

        private handleFacetFilter:(facet:Facet) => boolean;

        constructor(name:string, displayName:string, facets?:Facet[], handleFacetFilter?:(facet:Facet) => boolean) {
            super('FacetGroupView', "facet-group-view");

            this.name = name;
            this.displayName = displayName;
            this.handleFacetFilter = handleFacetFilter;

            this.titleEl.getEl().setInnerHtml(this.displayName);
            this.appendChild(this.titleEl);

            if (facets) {
                facets.forEach((facet:Facet) => {
                    this.addFacetView(FacetView.createFacetView(facet, this));
                });
            }
        }

        getName() {
            return this.name;
        }

        /*
         * Override this method to give other criteria for this group to display given facet.
         */
        handlesFacet(facet:Facet) {
            if (this.handleFacetFilter) {
                return this.handleFacetFilter(facet);
            }
            else {
                return facet.getName() == this.name;
            }
        }

        update(facets:Facet[]) {

            facets.forEach((facet:Facet) => {

                var existingFacetView:FacetView = this.getFacetView(facet.getName());

                if (existingFacetView == null) {
                    this.addFacetView(FacetView.createFacetView(facet, this));
                }
                else {
                    if (existingFacetView instanceof TermsFacetView) {
                        var termsFacetView:TermsFacetView = <TermsFacetView>existingFacetView;
                        termsFacetView.update(facet);
                    }
                    else if (existingFacetView instanceof QueryFacetView) {
                        var queryFacetView:QueryFacetView = <QueryFacetView>existingFacetView;
                        queryFacetView.update(facet);
                    }
                }
            });
        }

        deselectGroup() {
            this.facetViews.forEach((facetView:FacetView) => {
                facetView.deselectFacet();
            });
        }

        hasSelections():boolean {

            var hasSelections = false;
            for (var i = 0; i < this.facetViews.length; i++) {
                if (this.facetViews[i].hasSelectedEntry()) {
                    hasSelections = true;
                    break;
                }
            }
            return hasSelections;
        }

        getSelectedValuesByFacetName():{ [s : string ] : string[]; } {

            var values:{[s:string] : string[]; } = {};

            this.facetViews.forEach((termsFacetView:TermsFacetView) => {
                values[termsFacetView.getName()] = termsFacetView.getSelectedValues();
            });
            return values;
        }

        private addFacetView(facetView:FacetView) {
            this.appendChild(facetView);
            facetView.addFacetEntrySelectionChangeListener((event:FacetEntryViewSelectionChangedEvent) => {
                    this.notifyFacetEntrySelectionChanged(event);
                }
            );
            this.facetViews.push(facetView);
        }

        private getFacetView(name:string):FacetView {

            for (var i = 0; i < this.facetViews.length; i++) {
                var facetView:FacetView = this.facetViews[i];
                if (facetView.getName() == name) {
                    return facetView;
                }
            }
            return null;
        }

        addFacetEntrySelectionChangeListener(listener:(event:FacetEntryViewSelectionChangedEvent) => void) {
            this.facetEntrySelectionChangedListeners.push(listener);
        }

        removeFacetEntrySelectionChangedListener(listener:(event:FacetEntryViewSelectionChangedEvent) => void) {
            this.facetEntrySelectionChangedListeners = this.facetEntrySelectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyFacetEntrySelectionChanged(event:FacetEntryViewSelectionChangedEvent) {

            this.facetEntrySelectionChangedListeners.forEach((listener:(event:FacetEntryViewSelectionChangedEvent) => void) => {
                listener(event);
            });
        }
    }
}