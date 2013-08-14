module api_app_browse_filter {

    export class BrowseFilterPanel extends api_ui.Panel {

        private filterSearchAction:FilterSearchAction = new FilterSearchAction();

        private filterResetAction:FilterResetAction = new FilterResetAction();

        private facetContainer:FacetContainer;

        private searchField:api_dom.InputEl;

        private clearFilter:api_dom.AEl;

        private searchFilterTypingTimer:number;

        constructor(facetData?:api_app_browse_filter.FacetGroupParams[]) {
            super('BrowseFilterPanel');
            this.addClass('filter-panel');

            this.searchField = this.createSearchFieldEl();
            this.clearFilter = this.createClearFilterEl();
            this.facetContainer = new FacetContainer(facetData);

            api_event.FilterSearchEvent.on((event:api_event.FilterSearchEvent) => {
                if (this.isDirty()) {
                    this.clearFilter.show();
                } else {
                    this.clearFilter.hide();
                }
                this.search();
            });

        }

        private createSearchFieldEl():api_dom.InputEl {
            var searchField = new api_dom.InputEl('SearchField', 'search-field');
            searchField.getEl().addEventListener('keydown', (event:any) => {
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

        private createClearFilterEl():api_dom.AEl {
            var clearFilter:api_dom.AEl = new api_dom.AEl('ClearFilter', 'reset-link');
            clearFilter.getEl().setInnerHtml('Clear filter');
            clearFilter.getHTMLElement().setAttribute('href', 'javascript:;');
            clearFilter.hide();

            clearFilter.getEl().addEventListener('click', () => {
                this.reset();
            });
            return clearFilter;
        }

        updateFacets(facetGroupsData:api_app_browse_filter.FacetGroupParams[]) {
            this.facetContainer.update(facetGroupsData)
        }

        setFilterSearchAction(action:FilterSearchAction) {
            this.filterSearchAction = action;
        }

        setFilterResetAction(action:FilterResetAction) {
            this.filterResetAction = action;
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
            this.filterResetAction.execute();
        }

        private search() {
            this.filterSearchAction.setFilterValues(this.getValues());
            this.filterSearchAction.execute();
        }
    }

}