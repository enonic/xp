module api.app.browse.filter {

    export class BrowseFilterPanel extends api.ui.Panel implements api.event.Observable {

        private listeners:BrowseFilterPanelListener[] = [];

        private facetContainer:api.facet.FacetContainer;

        private searchField:api.app.browse.filter.TextSearchField;

        private clearFilter:api.app.browse.filter.ClearFilterButton;

        constructor(facets?:api.facet.Facet[], groupViews?:api.facet.FacetGroupView[]) {
            super();
            this.addClass('filter-panel');

            this.searchField = new TextSearchField('Search');
            this.searchField.addValueChangedListener(() => {
                this.search();
            });

            this.clearFilter = new ClearFilterButton();
            this.clearFilter.getEl().addEventListener('click', () => {
                this.reset();
            });

            this.facetContainer = new api.facet.FacetContainer();
            this.appendChild(this.facetContainer);

            if (groupViews != null) {
                groupViews.forEach((facetGroupView:api.facet.FacetGroupView) => {

                        facetGroupView.addFacetEntrySelectionChangeListener((event:api.facet.FacetEntryViewSelectionChangedEvent) => {

                            this.search();
                        });

                        this.facetContainer.addFacetGroupView(facetGroupView);
                    }
                );
            }
        }

        afterRender() {
            this.appendChild(this.searchField);
            this.appendChild(this.clearFilter);
            this.appendChild(this.facetContainer);
        }

        updateFacets(facets:api.facet.Facet[]) {
            this.facetContainer.updateFacets(facets)
        }

        getValues():{ [s : string ] : string[]; } {
            var values:{[s:string] : string[]; } = this.facetContainer.getSelectedValuesByFacetName();
            values['query'] = [this.searchField.getEl().getValue()];
            return values;
        }

        hasFilterSet() {
            return this.facetContainer.hasSelectedFacetEntries() || this.searchField.getHTMLElement()['value'].trim() != '';
        }

        search() {
            if (this.hasFilterSet()) {
                this.clearFilter.show();
            }
            else {
                this.clearFilter.hide();
            }
            var values = this.getValues();
            this.notifySearch(values);
        }

        reset() {
            this.searchField.clear(true);
            this.facetContainer.deselectAll(true);
            this.clearFilter.hide();
            this.notifyReset();
        }

        addListener(listener:BrowseFilterPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:BrowseFilterPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySearch(values:{ [s : string ] : string[]; }) {
            this.listeners.forEach((listener) => {
                if (listener.onSearch) {
                    listener.onSearch(values);
                }
            });
        }

        private notifyReset() {
            this.listeners.forEach((listener) => {
                if (listener.onReset) {
                    listener.onReset();
                }
            });
        }
    }

}