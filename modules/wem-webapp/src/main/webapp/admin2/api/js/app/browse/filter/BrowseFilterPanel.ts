module api_app_browse_filter {

    export interface BrowseFilterPanelListener extends api_ui.Listener {

        onSearch?(values:any[]);

        onReset?();
    }

    export class BrowseFilterPanel extends api_ui.Panel implements api_ui.Observable {

        private listeners:BrowseFilterPanelListener[] = [];

        private facetContainer:FacetContainer;

        private searchField:api_app_browse_filter.TextSearchField;

        private clearFilter:api_app_browse_filter.ClearFilterButton;

        private searchFilterTypingTimer:number;

        constructor(facetData?:FacetGroupData[]) {
            super('BrowseFilterPanel');
            this.addClass('filter-panel');

            this.searchField = this.createSearchFieldEl();
            this.clearFilter = this.createClearFilterEl();
            this.facetContainer = new FacetContainer(facetData);

            api_app_browse_filter.FilterSearchEvent.on((event:api_app_browse_filter.FilterSearchEvent) => {
                if (this.isDirty()) {
                    this.clearFilter.show();
                } else {
                    this.clearFilter.hide();
                }
                this.search();
            });

        }

        private createSearchFieldEl():api_app_browse_filter.TextSearchField {
            var searchField:api_app_browse_filter.TextSearchField = new api_app_browse_filter.TextSearchField('Search');
            searchField.addSearchListener((event:any) => {
                if (event.which === 97) {
                    new api_event.FilterSearchEvent().fire();
                } else {
                    if (this.searchFilterTypingTimer !== null) {
                        window.clearTimeout(this.searchFilterTypingTimer);
                        this.searchFilterTypingTimer = null;
                    }
                    this.searchFilterTypingTimer = window.setTimeout(() => {
                        new api_event.FilterSearchEvent().fire();
                    }, 500);
                }
            });
            return searchField;
        }

        private createClearFilterEl():api_app_browse_filter.ClearFilterButton {
            var clearFilter:api_app_browse_filter.ClearFilterButton = new api_app_browse_filter.ClearFilterButton();

            clearFilter.hide();

            clearFilter.getEl().addEventListener('click', () => {
                this.reset();
            });
            return clearFilter;
        }

        updateFacets(facetGroupsData:FacetGroupData[]) {
            this.facetContainer.update(facetGroupsData)
        }

        afterRender() {
            this.appendChild(this.searchField);
            this.appendChild(this.clearFilter);
            this.appendChild(this.facetContainer);
        }

        getValues():any[] {
            var values = this.facetContainer.getValues();
            values['query'] = this.searchField.getEl().getValue();
            return values;
        }

        isDirty() {
            return this.facetContainer.isDirty() || this.searchField.getHTMLElement()['value'].trim() != '';
        }

        private reset() {
            this.searchField.getHTMLElement()['value'] = '';
            window.clearTimeout(this.searchFilterTypingTimer);
            this.facetContainer.reset();
            this.clearFilter.hide();
            this.notifyReset();
        }

        private search() {
            this.notifySearch(this.getValues());
        }

        addListener(listener:BrowseFilterPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:BrowseFilterPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySearch(values:any[]) {
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