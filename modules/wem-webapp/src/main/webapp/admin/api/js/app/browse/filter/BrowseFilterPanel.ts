module api_app_browse_filter {

    export class BrowseFilterPanel extends api_ui.Panel implements api_event.Observable {

        private listeners:BrowseFilterPanelListener[] = [];

        private facetContainer:api_facet.FacetContainer;

        private searchField:api_app_browse_filter.TextSearchField;

        private clearFilter:api_app_browse_filter.ClearFilterButton;

        constructor(facets?:api_facet.Facet[], groupViews?:api_facet.FacetGroupView[]) {
            super('BrowseFilterPanel');
            this.addClass('filter-panel');

            this.searchField = new TextSearchField('Search');
            this.searchField.addValueChangedListener(() => {
                this.search();
            });

            this.clearFilter = new ClearFilterButton();
            this.clearFilter.getEl().addEventListener('click', () => {
                this.reset();
            });

            this.facetContainer = new api_facet.FacetContainer();
            this.appendChild(this.facetContainer);

            if (groupViews != null) {
                groupViews.forEach((facetGroupView:api_facet.FacetGroupView) => {

                        facetGroupView.addFacetEntrySelectionChangeListener((event:api_facet.FacetEntryViewSelectionChangedEvent) => {

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

        updateFacets(facets:api_facet.Facet[]) {
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