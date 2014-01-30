module api.app.browse.filter {

    export class BrowseFilterPanel extends api.ui.Panel implements api.event.Observable {

        private listeners: BrowseFilterPanelListener[] = [];

        private aggregationContainer: api.aggregation.AggregationContainer;

        private searchField: api.app.browse.filter.TextSearchField;

        private clearFilter: api.app.browse.filter.ClearFilterButton;

        constructor(aggregations?: api.aggregation.Aggregation[], groupViews?: api.aggregation.AggregationGroupView[]) {
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

            this.aggregationContainer = new api.aggregation.AggregationContainer();
            this.appendChild(this.aggregationContainer);

            if (groupViews != null) {
                groupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {

                        aggregationGroupView.addBucketViewSelectionChangedEventListener((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                            this.search();
                        });

                        this.aggregationContainer.addAggregationGroupView(aggregationGroupView);
                    }
                );
            }
        }

        afterRender() {
            this.appendChild(this.searchField);
            this.appendChild(this.clearFilter);
            this.appendChild(this.aggregationContainer);
        }

        updateAggregations(aggregations: api.aggregation.Aggregation[]) {
            this.aggregationContainer.updateAggregations(aggregations);
        }

        getValues(): { [s : string ] : string[];
        } {
            var values: { [s:string] : string[];
            } = this.aggregationContainer.getSelectedValuesByAggregationName();
            values['query'] = [this.searchField.getEl().getValue()];
            return values;
        }

        hasFilterSet() {
            return this.aggregationContainer.hasSelectedBuckets() || this.searchField.getHTMLElement()['value'].trim() != '';
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
            this.aggregationContainer.deselectAll(true);
            this.clearFilter.hide();
            this.notifyReset();
        }

        addListener(listener: BrowseFilterPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener: BrowseFilterPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySearch(values: { [s : string ] : string[];
        }) {
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