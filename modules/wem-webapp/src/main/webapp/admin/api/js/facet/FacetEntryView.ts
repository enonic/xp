module api_facet {

    export class FacetEntryView extends api_dom.DivEl {

        private parentFacetView:FacetView;

        private selectionChangedListeners:Function[] = [];

        constructor(parentFacetView:FacetView) {
            super('FacetEntryView', 'facet-entry-view');
            this.parentFacetView = parentFacetView;
        }

        getParentFacetView() {
            return this.parentFacetView;
        }


        addSelectionChangeListener(listener:(event:FacetEntryViewSelectionChangedEvent) => void) {
            this.selectionChangedListeners.push(listener);
        }

        removeSelectionChangedListener(listener:(event:FacetEntryViewSelectionChangedEvent) => void) {
            this.selectionChangedListeners = this.selectionChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifySelectionChanged(oldValue:boolean, newValue:boolean) {

            this.selectionChangedListeners.forEach((listener:(event:FacetEntryViewSelectionChangedEvent) => void) => {
                listener(new FacetEntryViewSelectionChangedEvent(oldValue, newValue, this));
            });
        }

    }
}