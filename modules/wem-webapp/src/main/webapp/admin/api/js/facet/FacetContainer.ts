module api_facet {

    export class FacetContainer extends api_dom.DivEl {

        private facetGroupViews:FacetGroupView[] = [];

        private firstSelectedGroupView:FacetGroupView;

        constructor() {
            super("FacetContainer");
        }

        addFacetGroupView(facetGroupView:FacetGroupView) {
            this.appendChild(facetGroupView);
            facetGroupView.addFacetEntrySelectionChangeListener((event:FacetEntryViewSelectionChangedEvent) => {

                if (event.getNewValue() && this.firstSelectedGroupView == null ) {
                    this.firstSelectedGroupView = event.getFacetEntryView().getParentFacetView().getParentGroupView();
                }
            });
            this.facetGroupViews.push(facetGroupView);
        }


        deselectAll(supressEvent?:boolean) {
            this.facetGroupViews.forEach((facetView:FacetGroupView) => {
                facetView.deselectGroup(supressEvent);
            });
            this.firstSelectedGroupView = null;
        }

        hasSelectedFacetEntries():boolean {
            var hasSelected:boolean = false;
            this.facetGroupViews.forEach((facetGroupView:FacetGroupView) => {
                if (facetGroupView.hasSelections()) {
                    hasSelected = true;
                }
            });
            return hasSelected;
        }

        updateFacets(facets:Facet[]) {

            this.facetGroupViews.forEach((facetGroupView:FacetGroupView) => {

                var matchingFacets:Facet[] = facets.filter((current:Facet) => {
                    return facetGroupView.handlesFacet(current);
                });

                if (this.isGroupUpdatable(facetGroupView)) {
                    facetGroupView.update(matchingFacets);
                }
            });
        }

        private isGroupUpdatable(facetGroupView:FacetGroupView) {

            return facetGroupView != this.firstSelectedGroupView;
        }

        getSelectedValuesByFacetName():{[s:string] : string[]; } {

            var allValues:{[s:string] : string[]; } = {};

            this.facetGroupViews.forEach((facetGroupView:FacetGroupView) => {
                var currValues:{[s:string] : string[]; } = facetGroupView.getSelectedValuesByFacetName();
                for (var facetName in currValues) {
                    var selectedFacetValues:string[] = currValues[facetName];
                    allValues[facetName] = selectedFacetValues;
                }

            });
            return allValues;
        }
    }
}