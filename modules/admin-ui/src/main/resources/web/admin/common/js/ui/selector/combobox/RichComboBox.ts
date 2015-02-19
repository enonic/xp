module api.ui.selector.combobox {

    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import Viewer = api.ui.Viewer;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Option = api.ui.selector.Option;

    export class RichComboBox<OPTION_DISPLAY_VALUE> extends api.dom.CompositeFormInputEl {

        private loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        private comboBoxView: api.dom.DivEl;

        private comboBoxName: string;

        private selectedOptionsView: SelectedOptionsView<OPTION_DISPLAY_VALUE>;

        private comboBox: RichComboBoxComboBox<OPTION_DISPLAY_VALUE>;

        private identifierMethod: string;

        private maximumOccurrences: number;

        private minWidth: number;

        private delayedInputValueChangedHandling: number;

        private optionDisplayValueViewer: Viewer<OPTION_DISPLAY_VALUE>;

        private loadingListeners: {():void;}[];

        private loadedListeners: {(items: OPTION_DISPLAY_VALUE[]):void;}[];

        private setNextInputFocusWhenMaxReached: boolean;

        constructor(builder: RichComboBoxBuilder<OPTION_DISPLAY_VALUE>) {

            this.loadedListeners = [];
            this.loadingListeners = [];

            this.comboBoxName = builder.comboBoxName;
            this.identifierMethod = builder.identifierMethod;

            this.comboBoxView = new api.dom.DivEl();
            this.delayedInputValueChangedHandling = builder.delayedInputValueChangedHandling;
            this.selectedOptionsView = builder.selectedOptionsView;
            this.selectedOptionsView.hide();
            this.maximumOccurrences = builder.maximumOccurrences;
            this.minWidth = builder.minWidth;
            this.optionDisplayValueViewer = builder.optionDisplayValueViewer;
            this.setNextInputFocusWhenMaxReached = builder.nextInputFocusWhenMaxReached;

            var comboBoxConfig: ComboBoxConfig<OPTION_DISPLAY_VALUE> = {
                maximumOccurrences: this.maximumOccurrences,
                selectedOptionsView: this.selectedOptionsView,
                optionDisplayValueViewer: this.optionDisplayValueViewer,
                hideComboBoxWhenMaxReached: true,
                setNextInputFocusWhenMaxReached: this.setNextInputFocusWhenMaxReached,
                delayedInputValueChangedHandling: this.delayedInputValueChangedHandling,
                minWidth: this.minWidth
            };

            this.loader = builder.loader;
            this.comboBox = new RichComboBoxComboBox<OPTION_DISPLAY_VALUE>(name, comboBoxConfig, this.loader);
            this.setupLoader();

            this.comboBox.onClicked((event: MouseEvent) => {
                this.comboBox.giveFocus();
            });

            super(this.comboBox, this.selectedOptionsView);

            this.addClass('rich-combobox');
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

        getValues(): string[] {
            return this.comboBox.getOptions().map((option: Option<OPTION_DISPLAY_VALUE>) => {
                return option.value;
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

        select(value: OPTION_DISPLAY_VALUE) {
            this.comboBox.selectOption(this.createOption(value));
        }

        selectByOption(option: Option<OPTION_DISPLAY_VALUE>) {
            this.comboBox.selectOption(option);
        }

        deselect(value: OPTION_DISPLAY_VALUE) {
            this.comboBox.deselectOption(this.createOption(value));
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

        private getDisplayValueId(value: OPTION_DISPLAY_VALUE): string {
            var val = value[this.identifierMethod]();
            return typeof val == 'object' && val['toString'] ? val.toString() : val;
        }

        private createOption(value: OPTION_DISPLAY_VALUE): Option<OPTION_DISPLAY_VALUE> {
            return {
                value: this.getDisplayValueId(value),
                displayValue: value
            }
        }

        private setupLoader() {

            this.comboBox.onOptionFilterInputValueChanged((event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>) => {

                this.loader.search(event.getNewValue()).
                    then((result: OPTION_DISPLAY_VALUE[]) => {

                        if (!this.comboBox.isDropdownShown()) {
                            this.comboBox.showDropdown();
                            this.comboBox.giveInputFocus();
                        }
                        return result;
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();

            });

            this.comboBox.onOptionSelected((event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>) => {
                this.selectedOptionsView.show();
            });

            this.loader.onLoadingData((event: api.util.loader.event.LoadingDataEvent) => {
                this.comboBox.setEmptyDropdownText("Searching...");
                this.notifyLoading();
            });

            this.loader.onLoadedData((event: api.util.loader.event.LoadedDataEvent<OPTION_DISPLAY_VALUE>) => {
                var options = this.createOptions(event.getData());
                this.comboBox.setOptions(options);
                this.notifyLoaded(event.getData());
            });
        }

        createOptions(items: OPTION_DISPLAY_VALUE[]): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            var options = [];
            items.forEach((itemInst: OPTION_DISPLAY_VALUE) => {
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

        onOptionSelected(listener: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.onOptionSelected(listener);
        }

        unOptionSelected(listener: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.comboBox.unOptionSelected(listener);
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

        setValue(value: string): RichComboBox<OPTION_DISPLAY_VALUE> {
            if (this.loader.isLoaded()) {
                super.setValue(value);
            } else {
                var singleLoadListener = (event) => {
                    super.setValue(value);
                    this.loader.unLoadedData(singleLoadListener);
                };
                this.loader.onLoadedData(singleLoadListener);
                if (this.loader.isNotStarted()) {
                    this.loader.load();
                }
            }
            return this;
        }
    }

    class RichComboBoxComboBox<OPTION_DISPLAY_VALUE> extends ComboBox<OPTION_DISPLAY_VALUE> {

        private loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>;

        constructor(name: string, config: ComboBoxConfig<OPTION_DISPLAY_VALUE>,
                    loader: api.util.loader.BaseLoader<any, OPTION_DISPLAY_VALUE>) {
            super(name, config);
            this.loader = loader;
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

        minWidth: number;

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

        setMinWidth(value: number): RichComboBoxBuilder<T> {
            this.minWidth = value;
            return this;
        }

        build(): RichComboBox<T> {
            return new RichComboBox(this);
        }
    }


}
