module api.ui.selector.combobox {

    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import Viewer = api.ui.Viewer;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;

    export class RichComboBox<OPTION_DISPLAY_VALUE> extends api.dom.CompositeFormInputEl {

        private loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        private selectedOptionsView: SelectedOptionsView<OPTION_DISPLAY_VALUE>;

        private comboBox: RichComboBoxComboBox<OPTION_DISPLAY_VALUE>;

        private identifierMethod: string;

        private loadingListeners: {():void;}[];

        private loadedListeners: {(items: OPTION_DISPLAY_VALUE[]):void;}[];

        private setNextInputFocusWhenMaxReached: boolean;

        private interval: number;

        public static debug: boolean = false;

        constructor(builder: RichComboBoxBuilder<OPTION_DISPLAY_VALUE>) {

            this.loadedListeners = [];
            this.loadingListeners = [];

            this.identifierMethod = builder.identifierMethod;
            this.selectedOptionsView = builder.selectedOptionsView;

            var comboBoxConfig: ComboBoxConfig<OPTION_DISPLAY_VALUE> = {
                maximumOccurrences: builder.maximumOccurrences,
                selectedOptionsView: this.selectedOptionsView,
                optionDisplayValueViewer: builder.optionDisplayValueViewer,
                hideComboBoxWhenMaxReached: builder.hideComboBoxWhenMaxReached,
                setNextInputFocusWhenMaxReached: builder.nextInputFocusWhenMaxReached,
                delayedInputValueChangedHandling: builder.delayedInputValueChangedHandling,
                minWidth: builder.minWidth,
                value: builder.value
            };

            this.loader = builder.loader;
            this.comboBox = new RichComboBoxComboBox<OPTION_DISPLAY_VALUE>(name, comboBoxConfig, this.loader);
            this.setupLoader();

            this.comboBox.onClicked((event: MouseEvent) => {
                this.comboBox.giveFocus();
            });

            super(this.comboBox, this.selectedOptionsView);

            if (!api.util.StringHelper.isBlank(builder.comboBoxName)) {
                this.setName(builder.comboBoxName);
            }
            if (!api.util.StringHelper.isBlank(builder.value)) {
                // do not move focus when setting original value
                this.setIgnoreNextFocus(true);
            }

            this.addClass('rich-combobox');
        }

        handleLastRange(handler: () => void) {
            let grid = this.getComboBox().getComboBoxDropdownGrid().getElement();

            grid.onShown(() => {
                if (this.interval) {
                    clearInterval(this.interval);
                }
                this.interval = setInterval(() => {
                    grid = this.getComboBox().getComboBoxDropdownGrid().getElement();
                    let canvas = grid.getCanvasNode();
                    let canvasEl = new api.dom.ElementHelper(canvas);
                    let viewportEl = new api.dom.ElementHelper(canvas.parentElement);

                    let isLastRange = viewportEl.getScrollTop() >= canvasEl.getHeight() - 3 * viewportEl.getHeight();

                    if (isLastRange) {
                        handler();
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
            this.comboBox.getInput().getEl().setValue("");
        }

        clearSelection(forceClear: boolean = false) {
            this.comboBox.clearSelection(false, true, forceClear);
        }

        isSelected(value: OPTION_DISPLAY_VALUE): boolean {
            var selectedValues = this.getSelectedValues();
            var valueToFind = this.getDisplayValueId(value);
            for (var i = 0; i < selectedValues.length; i++) {
                if (selectedValues[i] == valueToFind) {
                    return true;
                }
            }
            return false;
        }

        protected getDisplayValueId(value: Object): string {
            var val = value[this.identifierMethod]();
            return typeof val == 'object' && val['toString'] ? val.toString() : val;
        }

        protected createOption(value: Object, readOnly?: boolean): Option<OPTION_DISPLAY_VALUE> {
            return {
                value: this.getDisplayValueId(value),
                displayValue: <OPTION_DISPLAY_VALUE>value,
                readOnly: readOnly
            }
        }

        private setupLoader() {

            this.comboBox.onOptionFilterInputValueChanged((event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>) => {

                this.loader.search(event.getNewValue()).
                    then((result: OPTION_DISPLAY_VALUE[]) => {
                        return result;
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();

            });

            this.loader.onLoadingData((event: api.util.loader.event.LoadingDataEvent) => {
                if (!event.isPostLoad()) {
                    this.comboBox.setEmptyDropdownText("Searching...");
                }
                this.notifyLoading();
            });

            this.loader.onLoadedData((event: api.util.loader.event.LoadedDataEvent<OPTION_DISPLAY_VALUE>) => {
                var options = this.createOptions(event.getData());
                // check if postLoad and save selection
                this.comboBox.setOptions(options, event.isPostLoaded());
                this.notifyLoaded(event.getData());
            });
        }

        private createOptions(items: Object[]): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            var options = [];
            items.forEach((itemInst: Object) => {
                options.push(this.createOption(itemInst));
            });
            return options;
        }

        getLoader(): api.util.loader.BaseLoader<api.item.ItemJson, OPTION_DISPLAY_VALUE> {
            return this.loader;
        }

        setInputIconUrl(url: string) {
            this.comboBox.setInputIconUrl(url);
        }

        onOptionDeselected(listener: {(option: SelectedOption<OPTION_DISPLAY_VALUE>):void;}) {
            this.comboBox.onOptionDeselected(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.unOptionDeselected(listener);
        }

        onOptionSelected(listener: {(option: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.onOptionSelected(listener);
        }

        unOptionSelected(listener: {(option: SelectedOption<OPTION_DISPLAY_VALUE>): void;}) {
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
            var index = this.loadedListeners.indexOf(listener);
            this.loadedListeners.splice(index, 1);
        }

        onLoaded(listener: {(items: OPTION_DISPLAY_VALUE[]): void;}) {
            this.loadedListeners.push(listener);
        }

        unLoaded(listenerToBeRemoved: {(items: OPTION_DISPLAY_VALUE[]): void;}) {
            var index = this.loadedListeners.indexOf(listenerToBeRemoved);
            this.loadedListeners.splice(index, 1);
        }

        private notifyLoaded(items: OPTION_DISPLAY_VALUE[]) {
            this.loadedListeners.forEach((listener) => {
                listener(items);
            });
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

    class RichComboBoxComboBox<OPTION_DISPLAY_VALUE> extends ComboBox<OPTION_DISPLAY_VALUE> {

        private loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        private tempValue: string;

        public static debug: boolean = false;

        constructor(name: string, config: ComboBoxConfig<OPTION_DISPLAY_VALUE>,
                    loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>) {
            super(name, config);
            this.loader = loader;
        }

        protected doSetValue(value: string, silent?: boolean) {
            if (!this.loader.isLoaded()) {
                if (RichComboBox.debug) {
                    console.debug(this.toString() + ".doSetValue: loader is not loaded, saving temp value = " + value);
                }
                this.tempValue = value;
            }
            this.doWhenLoaded(() => {
                if (this.tempValue) {
                    if (RichComboBox.debug) {
                        console.debug(this.toString() + ".doSetValue: clearing temp value = " + this.tempValue);
                    }
                    delete this.tempValue;
                }
                super.doSetValue(value, silent);
            }, value);
        }

        protected doGetValue(): string {
            if (!this.loader.isLoaded() && this.tempValue != undefined) {
                if (RichComboBox.debug) {
                    console.debug("RichComboBox: loader is not loaded, returning temp value = " + this.tempValue);
                }
                return this.tempValue;
            } else {
                return super.doGetValue();
            }
        }

        private doWhenLoaded(callback: Function, value: string) {
            if (this.loader.isLoaded()) {
                var optionsMissing = !api.util.StringHelper.isEmpty(value) && this.splitValues(value).some((val) => {
                        return !this.getOptionByValue(val);
                    });
                if (optionsMissing) { // option needs loading
                    this.loader.preLoad(value).then(() => {
                        callback();
                    });
                } else { // empty option
                    callback();
                }
            } else {
                if (RichComboBox.debug) {
                    console.debug(this.toString() + '.doWhenLoaded: waiting to be loaded');
                }
                var singleLoadListener = () => {
                    if (RichComboBox.debug) {
                        console.debug(this.toString() + '.doWhenLoaded: on loaded');
                    }
                    callback();
                    this.loader.unLoadedData(singleLoadListener);
                };
                this.loader.onLoadedData(singleLoadListener);
                if (!api.util.StringHelper.isEmpty(value) && this.loader.isNotStarted()) {
                    this.loader.preLoad(value);
                }
            }
        }

        loadOptionsAfterShowDropdown(): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>();
            this.loader.load().then(() => {

                deferred.resolve(null);
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }
    }

    export class RichComboBoxBuilder<T> {

        comboBoxName: string;

        loader: api.util.loader.BaseLoader<any, T>;

        selectedOptionsView: SelectedOptionsView<T>;

        identifierMethod: string = "getId";

        maximumOccurrences: number = 0;

        optionDisplayValueViewer: Viewer<T>;

        delayedInputValueChangedHandling: number;

        nextInputFocusWhenMaxReached: boolean = true;

        hideComboBoxWhenMaxReached: boolean = true;

        minWidth: number;

        value: string;

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

        setValue(value: string): RichComboBoxBuilder<T> {
            this.value = value;
            return this;
        }

        build(): RichComboBox<T> {
            return new RichComboBox(this);
        }
    }


}
