module api.app.browse.filter {

    export class BrowseFilterPanel extends api.ui.Panel {

        private searchListeners: {(event: SearchEvent):void}[] = [];

        private resetListeners: {():void}[] = [];

        private aggregationContainer: api.aggregation.AggregationContainer;

        private searchField: api.app.browse.filter.TextSearchField;

        private clearFilter: api.app.browse.filter.ClearFilterButton;

        constructor(aggregations?: api.aggregation.Aggregation[], groupViews?: api.aggregation.AggregationGroupView[]) {
            super();
            this.addClass('filter-panel');

            this.searchField = new TextSearchField('Search');
            this.searchField.addValueChangedListener(() => {
                this.search(this.searchField);
            });

            this.clearFilter = new ClearFilterButton();
            this.clearFilter.onClicked(() => {
                this.reset();
            });

            this.aggregationContainer = new api.aggregation.AggregationContainer();
            this.appendChild(this.aggregationContainer);

            if (groupViews != null) {
                groupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {

                        aggregationGroupView.addBucketViewSelectionChangedEventListener((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                            this.search(event.getBucketView());
                        });

                        this.aggregationContainer.addAggregationGroupView(aggregationGroupView);
                    }
                );
            }

            this.onRendered((event) => {
                this.appendChild(this.searchField);
                this.appendChild(this.clearFilter);
                this.appendChild(this.aggregationContainer);
            })
        }

        updateAggregations(aggregations: api.aggregation.Aggregation[], doUpdateAll?: boolean) {
            this.aggregationContainer.updateAggregations(aggregations, doUpdateAll);
        }

        getSearchInputValues(): api.query.SearchInputValues {

            var searchInputValues: api.query.SearchInputValues = new api.query.SearchInputValues();

            searchInputValues.setAggregationSelections(this.aggregationContainer.getSelectedValuesByAggregationName());
            searchInputValues.setTextSearchFieldValue(this.searchField.getEl().getValue());

            return searchInputValues;
        }

        hasFilterSet() {
            return this.aggregationContainer.hasSelectedBuckets() || this.searchField.getHTMLElement()['value'].trim() != '';
        }


        search(elementChanged?: api.dom.Element) {
            if (this.hasFilterSet()) {
                this.clearFilter.show();
            }
            else {
                this.clearFilter.hide();
            }
            var values = this.getSearchInputValues();
            this.notifySearch(values, elementChanged);
        }

        reset() {
            this.searchField.clear(true);
            this.aggregationContainer.deselectAll(true);
            this.clearFilter.hide();
            this.notifyReset();
        }

        onSearch(listener: (event: SearchEvent)=>void) {
            this.searchListeners.push(listener);
        }

        onReset(listener: ()=>void) {
            this.resetListeners.push(listener);
        }

        unSearch(listener: (event: SearchEvent)=>void) {
            this.searchListeners = this.searchListeners.filter((currentListener: (event: SearchEvent)=>void) => {
                return currentListener != listener;
            });
        }

        unReset(listener: ()=>void) {
            this.resetListeners = this.resetListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });

        }

        private notifySearch(searchInputValues: api.query.SearchInputValues, elementChanged?: api.dom.Element) {
            this.searchListeners.forEach((listener: (event: SearchEvent)=>void) => {
                listener.call(this, new SearchEvent(searchInputValues, elementChanged));
            });
        }

        private notifyReset() {
            this.resetListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }
    }

}