module api.ui.selector.combobox {

    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import Viewer = api.ui.Viewer;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;
    import PostLoader = api.util.loader.PostLoader;
    import LoaderErrorEvent = api.util.loader.event.LoaderErrorEvent;

    export class RichComboBox<OPTION_DISPLAY_VALUE> extends api.dom.CompositeFormInputEl {

        protected loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        private selectedOptionsView: SelectedOptionsView<OPTION_DISPLAY_VALUE>;

        private comboBox: LoaderComboBox<OPTION_DISPLAY_VALUE>;

        private errorContainer: api.dom.DivEl;

        private identifierMethod: string;

        private loadingListeners: {(): void;}[];

        private loadedListeners: {(items: OPTION_DISPLAY_VALUE[], postLoaded?: boolean): void;}[];

        private interval: number;

        private treegridDropdownEnabled: boolean;

        public static debug: boolean = false;

        constructor(builder: RichComboBoxBuilder<OPTION_DISPLAY_VALUE>) {
            super();

            this.comboBox = this.createCombobox(builder);
            this.loader = builder.loader;
            this.loadedListeners = [];
            this.loadingListeners = [];
            this.identifierMethod = builder.identifierMethod;
            this.selectedOptionsView = builder.selectedOptionsView;
            this.treegridDropdownEnabled = builder.treegridDropdownEnabled;
            this.errorContainer = new api.dom.DivEl('error-container');

            this.setupLoader();
            this.setWrappedInput(this.comboBox);
            this.setAdditionalElements(this.errorContainer, this.selectedOptionsView);

            if (!api.util.StringHelper.isBlank(builder.comboBoxName)) {
                this.setName(builder.comboBoxName);
            }
            if (!api.util.StringHelper.isBlank(builder.value)) {
                this.setIgnoreNextFocus(true); // do not move focus when setting original value
            }

            this.addClass('rich-combobox');
        }

        private createCombobox(builder: RichComboBoxBuilder<OPTION_DISPLAY_VALUE>): LoaderComboBox<OPTION_DISPLAY_VALUE> {
            let comboBox = new LoaderComboBox<OPTION_DISPLAY_VALUE>(builder.comboBoxName, this.createComboboxConfig(
                builder), builder.loader);

            comboBox.onClicked((event: MouseEvent) => {
                comboBox.giveFocus();
            });

            return comboBox;
        }

        private createComboboxConfig(builder: RichComboBoxBuilder<OPTION_DISPLAY_VALUE>): ComboBoxConfig<OPTION_DISPLAY_VALUE> {
            return {
                maximumOccurrences: builder.maximumOccurrences,
                selectedOptionsView: builder.selectedOptionsView,
                optionDisplayValueViewer: builder.optionDisplayValueViewer,
                hideComboBoxWhenMaxReached: builder.hideComboBoxWhenMaxReached,
                setNextInputFocusWhenMaxReached: builder.nextInputFocusWhenMaxReached,
                delayedInputValueChangedHandling: builder.delayedInputValueChangedHandling,
                minWidth: builder.minWidth,
                value: builder.value,
                noOptionsText: builder.noOptionsText,
                maxHeight: builder.maxHeight,
                displayMissingSelectedOptions: builder.displayMissingSelectedOptions,
                removeMissingSelectedOptions: builder.removeMissingSelectedOptions,
                skipAutoDropShowOnValueChange: true,
                treegridDropdownEnabled: builder.treegridDropdownEnabled,
                optionDataHelper: builder.optionDataHelper,
                optionDataLoader: builder.optionDataLoader,
                onDropdownShownCallback: this.loadOptionsAfterShowDropdown.bind(this)
            };
        }

        setReadOnly(readOnly: boolean) {
            super.setReadOnly(readOnly);

            this.comboBox.setReadOnly(readOnly);

            this.toggleClass('readonly', readOnly);
        }

        private handleLastRange(handler: () => void) {
            let grid = this.getComboBox().getComboBoxDropdownGrid().getGrid();

            grid.onShown(() => {
                if (this.interval) {
                    clearInterval(this.interval);
                }
                this.interval = setInterval(() => {
                    if (!this.isDataGridSelfLoading()) {
                        grid = this.getComboBox().getComboBoxDropdownGrid().getGrid();
                        let canvas = grid.getCanvasNode();
                        let canvasEl = new api.dom.ElementHelper(canvas);
                        let viewportEl = new api.dom.ElementHelper(canvas.parentElement);

                        let isLastRange = viewportEl.getScrollTop() >= canvasEl.getHeight() - 3 * viewportEl.getHeight();

                        if (isLastRange) {
                            handler();
                        }
                    }
                }, 200);
            });

            grid.onHidden(() => {
                if (this.interval) {
                    clearInterval(this.interval);
                }
            });
        }

        setIgnoreNextFocus(value: boolean = true): RichComboBox<OPTION_DISPLAY_VALUE> {
            this.comboBox.setIgnoreNextFocus(value);
            return this;
        }

        isIgnoreNextFocus(): boolean {
            return this.comboBox.isIgnoreNextFocus();
        }

        getSelectedDisplayValues(): OPTION_DISPLAY_VALUE[] {
            return this.comboBox.getSelectedOptions().map((option: Option<OPTION_DISPLAY_VALUE>) => {
                return option.displayValue;
            });
        }

        getSelectedValues(): string[] {
            return this.comboBox.getSelectedOptions().map((option: Option<OPTION_DISPLAY_VALUE>) => {
                return option.value;
            });
        }

        getDisplayValues(): OPTION_DISPLAY_VALUE[] {
            return this.comboBox.getOptions().map((option: Option<OPTION_DISPLAY_VALUE>) => {
                return option.displayValue;
            });
        }

        getSelectedOptions(): SelectedOption<OPTION_DISPLAY_VALUE>[] {
            return this.selectedOptionsView.getSelectedOptions();
        }

        getSelectedOption(option: Option<OPTION_DISPLAY_VALUE>): SelectedOption<OPTION_DISPLAY_VALUE> {
            return this.selectedOptionsView.getByOption(option);
        }

        getSelectedOptionView(): SelectedOptionsView<OPTION_DISPLAY_VALUE> {
            return this.selectedOptionsView;
        }

        isOptionSelected(option: Option<OPTION_DISPLAY_VALUE>): boolean {
            return this.comboBox.isOptionSelected(option);
        }

        maximumOccurrencesReached(): boolean {
            return this.comboBox.maximumOccurrencesReached();
        }

        getComboBox(): ComboBox<OPTION_DISPLAY_VALUE> {
            return this.comboBox;
        }

        addOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.comboBox.addOption(option);
        }

        selectOption(option: Option<OPTION_DISPLAY_VALUE>, silent: boolean = false) {
            this.comboBox.selectOption(option, silent);
        }

        hasOptions(): boolean {
            return this.comboBox.hasOptions();
        }

        getOptionCount(): number {
            return this.comboBox.getOptionCount();
        }

        getOptions(): Option<OPTION_DISPLAY_VALUE>[] {
            return this.comboBox.getOptions();
        }

        getOptionByValue(value: string): Option<OPTION_DISPLAY_VALUE> {
            return this.comboBox.getOptionByValue(value);
        }

        getOptionByRow(rowIndex: number): Option<OPTION_DISPLAY_VALUE> {
            return this.comboBox.getOptionByRow(rowIndex);
        }

        countSelected(): number {
            return this.comboBox.countSelectedOptions();
        }

        select(value: OPTION_DISPLAY_VALUE, readOnly?: boolean) {
            this.comboBox.selectOption(this.createOption(value, readOnly));
        }

        deselect(value: OPTION_DISPLAY_VALUE) {
            this.comboBox.deselectOption(this.createOption(value));
        }

        clearCombobox() {
            this.clearSelection(true);
            this.comboBox.getInput().getEl().setValue('');
        }

        clearSelection(forceClear: boolean = false) {
            this.comboBox.clearSelection(false, true, forceClear);
        }

        isSelected(value: OPTION_DISPLAY_VALUE): boolean {
            let selectedValues = this.getSelectedValues();
            let valueToFind = this.getDisplayValueId(value);
            for (let i = 0; i < selectedValues.length; i++) {
                if (selectedValues[i] === valueToFind) {
                    return true;
                }
            }
            return false;
        }

        protected getDisplayValueId(value: Object): string {
            let val = value[this.identifierMethod]();
            return typeof val === 'object' && val['toString'] ? val.toString() : val;
        }

        protected createOption(value: Object, readOnly?: boolean): Option<OPTION_DISPLAY_VALUE> {
            return {
                value: this.getDisplayValueId(value),
                displayValue: <OPTION_DISPLAY_VALUE>value,
                readOnly: readOnly
            };
        }

        private isDataGridSelfLoading(): boolean { // if its a tree grid and there is no filter - it will load data itself
            return this.treegridDropdownEnabled && this.comboBox.isInputEmpty();
        }

        private loadOptionsAfterShowDropdown(): wemQ.Promise<void> {
            let deferred = wemQ.defer<void>();
            if (this.isDataGridSelfLoading()) {
                this.comboBox.getComboBoxDropdownGrid().reload().then(() => {
                    this.comboBox.showDropdown();
                    deferred.resolve(null);
                });
            } else {
                this.loader.load().then(() => {
                    deferred.resolve(null);
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
            }
            return deferred.promise;
        }

        private setupLoader() {

            this.comboBox.onOptionFilterInputValueChanged((event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>) => {
                if (this.isDataGridSelfLoading()) {
                    this.comboBox.getComboBoxDropdownGrid().reload().then(() => {
                        this.comboBox.showDropdown();
                    });
                } else {
                    this.loader.search(event.getNewValue()).then((result: OPTION_DISPLAY_VALUE[]) => {
                        return result;
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
                }
            });

            this.loader.onLoadingData((event: api.util.loader.event.LoadingDataEvent) => {
                if (!event.isPostLoad() && !this.treegridDropdownEnabled) {
                    this.comboBox.setEmptyDropdownText('Searching...');
                }
                this.notifyLoading();
            });

            this.loader.onLoadedData((event: api.util.loader.event.LoadedDataEvent<OPTION_DISPLAY_VALUE>) => {
                this.errorContainer.hide();
                let options = this.createOptions(event.getData());
                // check if postLoad and save selection
                this.comboBox.setOptions(options, event.isPostLoad());
                this.notifyLoaded(event.getData(), event.isPostLoad());
            });

            this.loader.onErrorOccurred((event: LoaderErrorEvent) => {
                this.comboBox.hideDropdown();
                this.errorContainer.setHtml(event.getTextStatus()).show();
            });

            if (api.ObjectHelper.iFrameSafeInstanceOf(this.loader, PostLoader)) {
                this.handleLastRange((<PostLoader<any, OPTION_DISPLAY_VALUE>>this.loader).postLoad.bind(this.loader));
            }
        }

        private createOptions(items: Object[]): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            let options = [];
            items.forEach((itemInst: Object) => {
                options.push(this.createOption(itemInst));
            });
            return options;
        }

        getLoader(): api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE> {
            return this.loader;
        }

        setInputIconUrl(url: string) {
            this.comboBox.setInputIconUrl(url);
        }

        onOptionDeselected(listener: {(option: SelectedOptionEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.onOptionDeselected(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOptionEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.unOptionDeselected(listener);
        }

        onOptionSelected(listener: {(option: SelectedOptionEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.onOptionSelected(listener);
        }

        unOptionSelected(listener: {(option: SelectedOptionEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.unOptionSelected(listener);
        }

        onOptionMoved(listener: {(option: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.onOptionMoved(listener);
        }

        unOptionMoved(listener: {(option: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.unOptionMoved(listener);
        }

        private notifyLoading() {
            this.loadingListeners.forEach((listener) => {
                listener();
            });
        }

        onLoading(listener: {(): void;}) {
            this.loadingListeners.push(listener);
        }

        unLoading(listener: {(): void;}) {
            let index = this.loadedListeners.indexOf(listener);
            this.loadedListeners.splice(index, 1);
        }

        onLoaded(listener: {(items: OPTION_DISPLAY_VALUE[], postLoaded?: boolean): void;}) {
            this.loadedListeners.push(listener);
        }

        unLoaded(listenerToBeRemoved: {(items: OPTION_DISPLAY_VALUE[], postLoaded?: boolean): void;}) {
            let index = this.loadedListeners.indexOf(listenerToBeRemoved);
            this.loadedListeners.splice(index, 1);
        }

        private notifyLoaded(items: OPTION_DISPLAY_VALUE[], postLoaded?: boolean) {
            this.loadedListeners.forEach((listener) => {
                listener(items, postLoaded);
            });
        }

        onValueLoaded(listener: (options: Option<OPTION_DISPLAY_VALUE>[]) => void) {
            this.comboBox.onValueLoaded(listener);
        }

        unValueLoaded(listener: (options: Option<OPTION_DISPLAY_VALUE>[]) => void) {
            this.comboBox.unValueLoaded(listener);
        }

        giveFocus(): boolean {
            return this.comboBox.giveFocus();
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.comboBox.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.comboBox.unBlur(listener);
        }
    }

    export class RichComboBoxBuilder<T> {

        comboBoxName: string;

        loader: api.util.loader.BaseLoader<any, T>;

        selectedOptionsView: SelectedOptionsView<T>;

        identifierMethod: string = 'getId';

        maximumOccurrences: number = 0;

        optionDisplayValueViewer: Viewer<T>;

        delayedInputValueChangedHandling: number;

        nextInputFocusWhenMaxReached: boolean = true;

        hideComboBoxWhenMaxReached: boolean = true;

        minWidth: number;

        maxHeight: number;

        value: string;

        noOptionsText: string;

        displayMissingSelectedOptions: boolean;

        removeMissingSelectedOptions: boolean;

        skipAutoDropShowOnValueChange: boolean;

        treegridDropdownEnabled: boolean;

        optionDataHelper: OptionDataHelper<T>;

        optionDataLoader: OptionDataLoader<T>;

        setComboBoxName(comboBoxName: string): RichComboBoxBuilder<T> {
            this.comboBoxName = comboBoxName;
            return this;
        }

        setIdentifierMethod(identifierMethod: string): RichComboBoxBuilder<T> {
            this.identifierMethod = identifierMethod;
            return this;
        }

        setLoader(loader: api.util.loader.BaseLoader<any, T>): RichComboBoxBuilder<T> {
            this.loader = loader;
            return this;
        }

        setSelectedOptionsView(selectedOptionsView: SelectedOptionsView<T>): RichComboBoxBuilder<T> {
            this.selectedOptionsView = selectedOptionsView;
            return this;
        }

        getSelectedOptionsView(): SelectedOptionsView<T> {
            return this.selectedOptionsView;
        }

        setMaximumOccurrences(maximumOccurrences: number): RichComboBoxBuilder<T> {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setOptionDisplayValueViewer(value: Viewer<T>): RichComboBoxBuilder<T> {
            this.optionDisplayValueViewer = value;
            return this;
        }

        setDelayedInputValueChangedHandling(value: number): RichComboBoxBuilder<T> {
            this.delayedInputValueChangedHandling = value;
            return this;
        }

        setNextInputFocusWhenMaxReached(value: boolean): RichComboBoxBuilder<T> {
            this.nextInputFocusWhenMaxReached = value;
            return this;
        }

        setHideComboBoxWhenMaxReached(value: boolean): RichComboBoxBuilder<T> {
            this.hideComboBoxWhenMaxReached = value;
            return this;
        }

        setMinWidth(value: number): RichComboBoxBuilder<T> {
            this.minWidth = value;
            return this;
        }

        setMaxHeight(value: number): RichComboBoxBuilder<T> {
            this.maxHeight = value;
            return this;
        }

        setValue(value: string): RichComboBoxBuilder<T> {
            this.value = value;
            return this;
        }

        setNoOptionsText(value: string): RichComboBoxBuilder<T> {
            this.noOptionsText = value;
            return this;
        }

        setDisplayMissingSelectedOptions(value: boolean): RichComboBoxBuilder<T> {
            this.displayMissingSelectedOptions = value;
            return this;
        }

        setRemoveMissingSelectedOptions(value: boolean): RichComboBoxBuilder<T> {
            this.removeMissingSelectedOptions = value;
            return this;
        }

        setSkipAutoDropShowOnValueChange(value: boolean): RichComboBoxBuilder<T> {
            this.skipAutoDropShowOnValueChange = value;
            return this;
        }

        setTreegridDropdownEnabled(value: boolean): RichComboBoxBuilder<T> {
            this.treegridDropdownEnabled = value;
            return this;
        }

        setOptionDataHelper(value: OptionDataHelper<T>): RichComboBoxBuilder<T> {
            this.optionDataHelper = value;
            return this;
        }

        setOptionDataLoader(value: OptionDataLoader<T>): RichComboBoxBuilder<T> {
            this.optionDataLoader = value;
            return this;
        }

        build(): RichComboBox<T> {
            return new RichComboBox(this);
        }
    }

}
