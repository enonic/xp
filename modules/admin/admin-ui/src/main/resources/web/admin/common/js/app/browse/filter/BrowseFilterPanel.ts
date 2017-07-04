module api.app.browse.filter {

    export class BrowseFilterPanel<T> extends api.ui.panel.Panel {

        private searchStartedListeners: {():void}[] = [];

        private hideFilterPanelButtonClickedListeners: {():void}[] = [];

        private showResultsButtonClickedListeners: {():void}[] = [];

        private aggregationContainer: api.aggregation.AggregationContainer;

        private searchField: api.app.browse.filter.TextSearchField;

        private clearFilter: api.app.browse.filter.ClearFilterButton;

        private hitsCounterEl: api.dom.SpanEl;

        private hideFilterPanelButton: api.dom.SpanEl;

        private showResultsButton: api.dom.SpanEl;

        protected filterPanelRefreshNeeded: boolean = false;

        private refreshStartedListeners: {():void}[] = [];

        protected selectionSection: ConstraintSection<T>;

        constructor() {
            super();
            this.addClass('filter-panel');

            this.hideFilterPanelButton = new api.dom.SpanEl('hide-filter-panel-button icon-search');
            this.hideFilterPanelButton.onClicked(() => this.notifyHidePanelButtonPressed());

            let showResultsButtonWrapper = new api.dom.DivEl('show-filter-results');
            this.showResultsButton = new api.dom.SpanEl('show-filter-results-button');
            this.updateResultsTitle(true);
            this.showResultsButton.onClicked(() => this.notifyShowResultsButtonPressed());
            showResultsButtonWrapper.appendChild(this.showResultsButton);

            this.searchField = new TextSearchField('Search');
            this.searchField.onValueChanged(() => {
                this.search(this.searchField);
            });

            this.clearFilter = new ClearFilterButton();
            this.clearFilter.onClicked(() => this.reset());

            this.hitsCounterEl = new api.dom.SpanEl('hits-counter');

            let hitsCounterAndClearButtonWrapper = new api.dom.DivEl('hits-and-clear');
            hitsCounterAndClearButtonWrapper.appendChildren(this.clearFilter, this.hitsCounterEl);

            this.aggregationContainer = new api.aggregation.AggregationContainer();
            this.aggregationContainer.hide();
            this.appendChild(this.aggregationContainer);

            let groupViews = this.getGroupViews();
            if (groupViews != null) {
                groupViews.forEach((aggregationGroupView: api.aggregation.AggregationGroupView) => {

                        aggregationGroupView.onBucketViewSelectionChanged((event: api.aggregation.BucketViewSelectionChangedEvent) => {
                            this.search(event.getBucketView());
                        });

                        this.aggregationContainer.addAggregationGroupView(aggregationGroupView);
                    }
                );
            }

            this.onRendered(() => {
                this.appendChild(this.hideFilterPanelButton);
                this.appendExtraSections();
                this.appendChild(this.searchField);
                this.appendChild(hitsCounterAndClearButtonWrapper);
                this.appendChild(this.aggregationContainer);
                this.appendChild(showResultsButtonWrapper);

                this.showResultsButton.hide();

                api.ui.KeyBindings.get().bindKey(new api.ui.KeyBinding('/', () => {
                    setTimeout(this.giveFocusToSearch.bind(this), 100);
                }).setGlobal(true));
            });

            this.onHidden(() => {
                this.aggregationContainer.hide();
            });

            this.onShown(() => {
                setTimeout(this.aggregationContainer.show.bind(this.aggregationContainer), 100);
                this.refresh();
            });
        }

        protected getGroupViews(): api.aggregation.AggregationGroupView[] {
            return [];
        }

        protected appendExtraSections() {
            this.appendSelectedItemsSection();
        }

        protected appendSelectedItemsSection() {
            this.selectionSection = this.createConstraintSection();
            this.appendChild(this.selectionSection);
        }

        protected getSelectionItems(): T[] {
            return this.selectionSection.getItems();
        }

        setConstraintItems(constraintSection: ConstraintSection<T>, items: T[]) {
            if (api.ObjectHelper.anyArrayEquals(items, constraintSection.getItems())) {
                return;
            }
            constraintSection.setItems(items);
            if (constraintSection.isActive()) {
                this.resetControls();
                this.search();
                this.addClass('show-constraint');
                setTimeout(this.giveFocusToSearch.bind(this), 100);
            }
        }

        setSelectedItems(items: T[]) {
            this.setConstraintItems(this.selectionSection, items);
        }

        hasConstraint() {
            return !!this.selectionSection && this.selectionSection.isActive();
        }

        protected createConstraintSection(): api.app.browse.filter.ConstraintSection<T> {
            return new api.app.browse.filter.ConstraintSection<T>('Selected Items', () => this.onCloseFilterInConstrainedMode());
        }

        protected onCloseFilterInConstrainedMode() {
            this.notifyHidePanelButtonPressed();
        }

        setRefreshOfFilterRequired() {
            this.filterPanelRefreshNeeded = true;
        }

        giveFocusToSearch() {
            this.searchField.giveFocus();
        }

        updateAggregations(aggregations: api.aggregation.Aggregation[], doUpdateAll?: boolean) {
            this.aggregationContainer.updateAggregations(aggregations, doUpdateAll);
        }

        getSearchInputValues(): api.query.SearchInputValues {

            let searchInputValues: api.query.SearchInputValues = new api.query.SearchInputValues();

            searchInputValues.setAggregationSelections(this.aggregationContainer.getSelectedValuesByAggregationName());
            searchInputValues.setTextSearchFieldValue(this.searchField.getEl().getValue());

            return searchInputValues;
        }

        hasFilterSet(): boolean {
            return this.aggregationContainer.hasSelectedBuckets() || this.hasSearchStringSet();
        }

        protected isFilteredOrConstrained(): boolean {
            return this.hasFilterSet() || this.selectionSection.isActive();
        }

        hasSearchStringSet(): boolean {
            return this.searchField.getHTMLElement()['value'].trim() !== '';
        }

        search(elementChanged?: api.dom.Element) {
            const hasFilterSet = this.hasFilterSet();

            this.clearFilter.setVisible(hasFilterSet);
            this.updateResultsTitle(!hasFilterSet);

            this.notifySearchStarted();
            this.doSearch(elementChanged);
        }

        doSearch(elementChanged?: api.dom.Element) {
            return;
        }

        refresh() {
            if (this.filterPanelRefreshNeeded) {
                this.notifyRefreshStarted();
                this.doRefresh();
                this.filterPanelRefreshNeeded = false;
            }
        }

        doRefresh() {
            return;
        }

        resetConstraints() {
            this.removeClass('show-constraint');
            this.selectionSection.reset();
            this.reset(true);
        }

        reset(suppressEvent?: boolean) {
            this.resetControls();
            this.resetFacets(suppressEvent);
        }

        resetControls() {
            this.searchField.clear(true);
            this.aggregationContainer.deselectAll(true);
            this.clearFilter.hide();
            this.updateResultsTitle(true);
        }

        protected resetFacets(suppressEvent?: boolean, doResetAll?: boolean) {
            throw new Error('To be implemented by inheritors');
        }

        deselectAll() {
            this.aggregationContainer.deselectAll(true);
        }

        onSearchStarted(listener: ()=> void) {
            this.searchStartedListeners.push(listener);
        }

        onRefreshStarted(listener: ()=> void) {
            this.refreshStartedListeners.push(listener);
        }

        unRefreshStarted(listener: ()=>void) {
            this.refreshStartedListeners = this.refreshStartedListeners.filter((currentListener: ()=> void) => {
                return currentListener !== listener;
            });
        }

        unSearchStarted(listener: ()=> void) {
            this.searchStartedListeners = this.searchStartedListeners.filter((currentListener: ()=> void) => {
                return currentListener !== listener;
            });
        }

        onHideFilterPanelButtonClicked(listener: ()=> void) {
            this.hideFilterPanelButtonClickedListeners.push(listener);
        }

        onShowResultsButtonClicked(listener: ()=> void) {
            this.showResultsButtonClickedListeners.push(listener);
        }

        private notifySearchStarted() {
            this.searchStartedListeners.forEach((listener: ()=> void) => {
                listener.call(this);
            });
        }

        protected notifyRefreshStarted() {
            this.refreshStartedListeners.forEach((listener: ()=> void) => {
                listener.call(this);
            });
        }

        private notifyHidePanelButtonPressed() {
            this.hideFilterPanelButtonClickedListeners.forEach((listener: ()=> void) => {
                listener.call(this);
            });
        }

        private notifyShowResultsButtonPressed() {
            this.showResultsButtonClickedListeners.forEach((listener: ()=> void) => {
                listener.call(this);
            });
        }

        updateHitsCounter(hits: number, emptyFilterValue: boolean = false) {
            let unfilteredSelection = (this.hasConstraint() && hits === this.getSelectionItems().length);
            if (emptyFilterValue || unfilteredSelection) {
                this.hitsCounterEl.setHtml(hits + ' total');
            } else {
                if (hits !== 1) {
                    this.hitsCounterEl.setHtml(hits + ' hits');
                } else {
                    this.hitsCounterEl.setHtml(hits + ' hit');
                }
            }

            if (hits !== 0) {
                this.showResultsButton.show();
            } else {
                this.showResultsButton.hide();
            }
        }

        updateResultsTitle(allShown: boolean) {
            const title = allShown ? 'Show all' : 'Show results';
            this.showResultsButton.setHtml(title);
        }
    }

    export class ConstraintSection<T> extends api.dom.DivEl {

        private label: api.dom.LabelEl;
        protected items: T[];

        constructor(label: string, closeCallback?: () => void) {
            super('constraint-section');

            this.checkVisibilityState();

            this.label = new api.dom.LabelEl(label);
            this.appendChildren(this.label);

            if (!!closeCallback) {
                this.appendCloseButton(closeCallback);
            }
        }

        private appendCloseButton(closeCallback: () => void):  api.ui.button.ActionButton {
            let action = new api.ui.Action('').onExecuted(() => closeCallback());
            let button = new  api.ui.button.ActionButton(action);

            button.addClass('btn-close');
            this.appendChild(button);

            return button;
        }

        public reset() {
            this.items = null;
            this.checkVisibilityState();
        }

        public getItems(): T[] {
            return this.items;
        }

        private checkVisibilityState() {
            this.setVisible(this.isActive());
        }

        public isActive(): boolean {
            return !!this.items;
        }

        public setItems(items: T[]) {

            this.items = items;
            this.checkVisibilityState();
        }

        protected setLabel(text: string) {
            this.label.setValue(text);
        }

    }
}
