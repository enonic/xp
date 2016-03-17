module api.app.browse.filter {

    export class BrowseFilterPanel extends api.ui.panel.Panel {

        private searchListeners: {(event: SearchEvent):void}[] = [];

        private refreshListeners: {(event: RefreshEvent):void}[] = [];

        private resetListeners: {():void}[] = [];

        private hideFilterPanelButtonClickedListeners: {():void}[] = [];

        private showResultsButtonClickedListeners: {():void}[] = [];

        private aggregationContainer: api.aggregation.AggregationContainer;

        private searchField: api.app.browse.filter.TextSearchField;

        private clearFilter: api.app.browse.filter.ClearFilterButton;

        private hitsCounterEl: api.dom.SpanEl;

        private hideFilterPanelButton: api.dom.SpanEl;

        private showResultsButton: api.dom.SpanEl;

        constructor(aggregations?: api.aggregation.Aggregation[], groupViews?: api.aggregation.AggregationGroupView[]) {
            super();
            this.addClass('filter-panel');

            this.hideFilterPanelButton = new api.dom.SpanEl("hide-filter-panel-button icon-search");
            this.hideFilterPanelButton.onClicked(() => this.notifyHidePanelButtonPressed());

            var showResultsButtonWrapper = new api.dom.DivEl("show-filter-results");
            this.showResultsButton = new api.dom.SpanEl("show-filter-results-button");
            this.showResultsButton.setHtml("Show results");
            this.showResultsButton.onClicked(() => this.notifyShowResultsButtonPressed());
            showResultsButtonWrapper.appendChild(this.showResultsButton);

            this.searchField = new TextSearchField('Search');
            this.searchField.onValueChanged(() => {
                this.search(this.searchField);
            });

            this.clearFilter = new ClearFilterButton();
            this.clearFilter.onClicked((event: MouseEvent) => {
                this.reset();
            });

            this.hitsCounterEl = new api.dom.SpanEl("hits-counter");

            var hitsCounterAndClearButtonWrapper = new api.dom.DivEl("hits-and-clear");
            hitsCounterAndClearButtonWrapper.appendChildren(this.clearFilter, this.hitsCounterEl);

            this.aggregationContainer = new api.aggregation.AggregationContainer();
            this.appendChild(this.aggregationContainer);

            if (groupViews != null) {
                groupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {

                        aggregationGroupView.onBucketViewSelectionChanged((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                            this.search(event.getBucketView());
                        });

                        this.aggregationContainer.addAggregationGroupView(aggregationGroupView);
                    }
                );
            }

            this.onRendered((event) => {
                this.appendChild(this.hideFilterPanelButton);
                this.appendChild(this.searchField);
                this.appendChild(hitsCounterAndClearButtonWrapper);
                this.appendChild(this.aggregationContainer);
                this.appendChild(showResultsButtonWrapper);

                this.showResultsButton.hide();

                api.ui.KeyBindings.get().bindKey(new api.ui.KeyBinding("/", (e: ExtendedKeyboardEvent) => {
                    setTimeout(this.giveFocusToSearch.bind(this), 100);
                }).setGlobal(true));
            })
        }

        giveFocusToSearch() {
            this.searchField.giveFocus();
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

        refresh() {
            this.notifyRefresh();
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

        onRefresh(listener: (event: RefreshEvent)=>void) {
            this.refreshListeners.push(listener);
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

        onHideFilterPanelButtonClicked(listener: ()=>void) {
            this.hideFilterPanelButtonClickedListeners.push(listener);
        }

        onShowResultsButtonClicked(listener: ()=>void) {
            this.showResultsButtonClickedListeners.push(listener);
        }

        private notifySearch(searchInputValues: api.query.SearchInputValues, elementChanged?: api.dom.Element) {
            this.searchListeners.forEach((listener: (event: SearchEvent)=>void) => {
                listener.call(this, new SearchEvent(searchInputValues, elementChanged));
            });
        }

        private notifyRefresh() {
            this.refreshListeners.forEach((listener: ()=>void) => {
                listener.call(this, new RefreshEvent(this.getSearchInputValues()));
            });
        }

        private notifyReset() {
            this.resetListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyHidePanelButtonPressed() {
            this.hideFilterPanelButtonClickedListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyShowResultsButtonPressed() {
            this.showResultsButtonClickedListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        updateHitsCounter(hits: number, emptyFilterValue: boolean = false) {
            if (!emptyFilterValue) {
                if (hits != 1) {
                    this.hitsCounterEl.setHtml(hits + " hits");
                }
                else {
                    this.hitsCounterEl.setHtml(hits + " hit");
                }
            } else {
                this.hitsCounterEl.setHtml(hits + " total");
            }

            if(hits != 0) {
                this.showResultsButton.show();
            }
            else {
                this.showResultsButton.hide();
            }
        }
    }

}