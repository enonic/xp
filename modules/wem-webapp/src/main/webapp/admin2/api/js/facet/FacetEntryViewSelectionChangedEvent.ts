module api_facet {

    export class FacetEntryViewSelectionChangedEvent {

        private oldValue:boolean;

        private newValue:boolean;

        private facetEntryView:FacetEntryView;

        constructor(oldValue:boolean, newValue:boolean, facetEntryView:FacetEntryView) {
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.facetEntryView = facetEntryView;
        }

        getOldValue():boolean {
            return this.oldValue;
        }

        getNewValue():boolean {
            return this.newValue;
        }

        getFacetEntryView():FacetEntryView {
            return this.facetEntryView;
        }
    }
}