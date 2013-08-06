module api_app_browse_filter {

    export class BrowseFilterPanel extends api_ui.Panel {

        filterSearchAction = new FilterSearchAction();

        filterResetAction = new FilterResetAction();

        facetContainer:FacetContainer;

        searchField:api_dom.InputEl;

        clearFilter:api_dom.AEl;

        lastFacetGroup:FacetGroup;

        searchFilterTypingTimer:number;

        constructor(facetData?:FacetGroupData[]) {
            super('BrowseFilterPanel');

            this.getEl().addClass('filter-panel');
            this.searchField = this.createSearchFieldEl();
            this.facetContainer = this.createFacetContainer(facetData);

            this.clearFilter = this.createClearFilterEl();
            api_event.FilterSearchEvent.on((event:api_event.FilterSearchEvent) => {
                if (this.isDirty()) {
                    this.clearFilter.show();
                } else {
                    this.clearFilter.hide();
                }
                if (event.getTarget()) {
                    this.lastFacetGroup = (<Facet>event.getTarget()).getFacetGroup();
                } else {
                    this.lastFacetGroup = undefined;
                }
                this.search();
            });

        }

        private createFacetContainer(facetData):FacetContainer {
            return new FacetContainer(facetData);
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

        private getFacetGroup(name:string) {
            var facetGroups:FacetGroup[] = this.facetContainer.getFacetGroups();
            for (var i in facetGroups) {
                var facetGroup:FacetGroup = facetGroups[i];
                if (facetGroup.getName() == name) {
                    return facetGroup;
                }
            }
            return null;
        }

        private createClearFilterEl():api_dom.AEl {
            var clearFilter:api_dom.AEl = new api_dom.AEl('ClearFilter', 'reset-link');
            clearFilter.getEl().setInnerHtml('Clear filter');
            clearFilter.getHTMLElement().setAttribute('href', 'javascript:;');
            clearFilter.hide();

            clearFilter.getEl().addEventListener('click', () => {
                this.filterResetAction.execute();
                this.reset();
            });
            return clearFilter;
        }

        private reset() {
            this.searchField.getHTMLElement()['value'] = '';
            window.clearTimeout(this.searchFilterTypingTimer);
            this.facetContainer.reset();
            this.clearFilter.hide();
        }

        private search() {
            this.filterSearchAction.setFilterValues(this.getValues());
            this.filterSearchAction.execute();
        }

        updateFacets(facetsData:FacetGroupData[]) {
            for (var i = 0; i < facetsData.length; i++) {
                var facetGroupData = facetsData[i];
                var facetGroup:FacetGroup = this.getFacetGroup(facetGroupData.name);
                if (facetGroup != this.lastFacetGroup && facetGroup != null) {
                    facetGroup.updateFacets(facetGroupData);
                } else if (facetGroup == null) {
                    var newFacetGroup:FacetGroup = new FacetGroup(facetGroupData);
                    this.facetContainer.addFacetGroup(newFacetGroup);
                }
            }
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
    }

}