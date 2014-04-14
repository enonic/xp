module api.ui.selector.combobox {

    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import OptionFilterInputValueChangedEvent = api.ui.selector.OptionFilterInputValueChangedEvent;
    import Viewer = api.ui.Viewer;

    export class RichComboBox<OPTION_DISPLAY_VALUE> extends api.ui.form.CompositeFormInputEl {

        loader: api.util.loader.BaseLoader<api.item.ItemJson, OPTION_DISPLAY_VALUE>;

        comboBoxView: api.dom.DivEl;

        comboBoxName: string;

        selectedOptionsView: any;

        comboBox: api.ui.selector.combobox.ComboBox<OPTION_DISPLAY_VALUE>;

        identifierMethod: string;

        maximumOccurrences: number;

        private delayedInputValueChangedHandling: number;

        private optionDisplayValueViewer:Viewer<OPTION_DISPLAY_VALUE>;

        private loadingListeners: {():void;}[];

        private loadedListeners: {(items: OPTION_DISPLAY_VALUE[]):void;}[];

        private optionSelectedListeners: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>):void;}[];

        constructor(config: RichComboBoxBuilder<OPTION_DISPLAY_VALUE>) {

            this.loadedListeners = [];
            this.loadingListeners = [];
            this.optionSelectedListeners = [];

            this.identifierMethod = config.identifierMethod;

            this.comboBoxView = new api.dom.DivEl();
            this.delayedInputValueChangedHandling = config.delayedInputValueChangedHandling;
            this.selectedOptionsView = config.selectedOptionsView;
            this.selectedOptionsView.hide();
            this.maximumOccurrences = config.maximumOccurrences;
            this.optionDisplayValueViewer = config.optionDisplayValueViewer;
            this.comboBox = this.createComboBox(config.comboBoxName);
            if (config.loader) {
                this.setLoader(config.loader);
            }

            super(this.comboBox, this.selectedOptionsView);

            this.addClass('rich-combobox');
        }

        getSelectedValues(): OPTION_DISPLAY_VALUE[] {
            return this.comboBox.getSelectedOptions().map((option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                return option.displayValue;
            });
        }

        getValues(): OPTION_DISPLAY_VALUE[] {
            return this.comboBox.getOptions().map((option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                return option.displayValue;
            });
        }

        getStringValues(): string[] {
            return this.comboBox.getSelectedOptions().map((option: api.ui.selector.Option<OPTION_DISPLAY_VALUE>) => {
                return option.value;
            });
        }

        maximumOccurrencesReached(): boolean {
            return this.comboBox.maximumOccurrencesReached();
        }

        countSelected(): number {
            return this.comboBox.countSelectedOptions();
        }

        select(value: OPTION_DISPLAY_VALUE) {
            this.comboBox.selectOption({
                value: value[this.identifierMethod](),
                displayValue: value
            });
        }

        private createComboBox(name: string): api.ui.selector.combobox.ComboBox<OPTION_DISPLAY_VALUE> {

            var comboBoxConfig = this.createConfig();

            return new api.ui.selector.combobox.ComboBox(name, comboBoxConfig);
        }

        private setupLoader() {
            this.comboBox.addSelectedOptionRemovedListener(()=> {
                this.loader.search("");
            });
            this.comboBox.onOptionFilterInputValueChanged((event: OptionFilterInputValueChangedEvent<OPTION_DISPLAY_VALUE>) => {
                this.loader.search(event.getNewValue());
            });
            this.comboBox.onOptionSelected((event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>) => {
                this.selectedOptionsView.show();
                this.notifyOptionSelected(event);
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

            this.loader.search("");
        }

        private createOptions(items: OPTION_DISPLAY_VALUE[]): api.ui.selector.Option<OPTION_DISPLAY_VALUE>[] {
            var options = [];
            items.forEach((itemInst: OPTION_DISPLAY_VALUE) => {
                options.push({
                    value: itemInst[this.identifierMethod](),
                    displayValue: itemInst
                });
            });
            return options;
        }

        createConfig(): api.ui.selector.combobox.ComboBoxConfig<OPTION_DISPLAY_VALUE> {
            return  {
                maximumOccurrences: this.maximumOccurrences,
                selectedOptionsView: this.selectedOptionsView,
                optionDisplayValueViewer: this.optionDisplayValueViewer,
                hideComboBoxWhenMaxReached: true,
                delayedInputValueChangedHandling: this.delayedInputValueChangedHandling
            };
        }

        setLoader(loader: api.util.loader.BaseLoader<api.item.ItemJson, OPTION_DISPLAY_VALUE>) {
            this.loader = loader;
            this.setupLoader();
        }

        getLoader():api.util.loader.BaseLoader<api.item.ItemJson, OPTION_DISPLAY_VALUE> {
            return this.loader;
        }

        setInputIconUrl(url: string) {
            this.comboBox.setInputIconUrl(url);
        }

        addSelectedOptionRemovedListener(listener: {(option: SelectedOption<OPTION_DISPLAY_VALUE>):void;}) {
            this.comboBox.addSelectedOptionRemovedListener(listener);
        }

        onOptionSelected(listener: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.optionSelectedListeners.push(listener);
        }

        unOptionSelected(listener: {(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>): void;}) {
            this.optionSelectedListeners.filter((currentListener: (event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>) =>void) => {
                return listener != currentListener;
            });
        }

        private notifyOptionSelected(event: OptionSelectedEvent<OPTION_DISPLAY_VALUE>) {
            this.optionSelectedListeners.forEach((listener) => {
                listener(event);
            });
        }

        addLoadingListener(listener: {(): void;}) {
            this.loadingListeners.push(listener);
        }

        addLoadedListener(listener: {(items: OPTION_DISPLAY_VALUE[]): void;}) {
            this.loadedListeners.push(listener);
        }

        removeLoadedListener(listenerToBeRemoved: {(items: OPTION_DISPLAY_VALUE[]): void;}) {
            var index = this.loadedListeners.indexOf(listenerToBeRemoved);
            this.loadedListeners.splice(index, 1);
        }

        private notifyLoading() {
            this.loadingListeners.forEach((listener) => {
                listener();
            });
        }

        private notifyLoaded(items: OPTION_DISPLAY_VALUE[]) {
            this.loadedListeners.forEach((listener) => {
                listener(items);
            });
        }

        giveFocus(): boolean {
            return this.comboBox.giveFocus();
        }
    }

    export class RichComboBoxBuilder<T> {

        comboBoxName: string;

        loader: api.util.loader.BaseLoader<api.item.ItemJson, T>;

        selectedOptionsView: SelectedOptionsView<T>;

        identifierMethod: string = "getId";

        maximumOccurrences: number = 0;

        optionDisplayValueViewer: Viewer<T>;

        delayedInputValueChangedHandling: number;

        setComboBoxName(comboBoxName: string): RichComboBoxBuilder<T> {
            this.comboBoxName = comboBoxName;
            return this;
        }

        setIdentifierMethod(identifierMethod: string): RichComboBoxBuilder<T> {
            this.identifierMethod = identifierMethod;
            return this;
        }

        setLoader(loader: api.util.loader.BaseLoader<api.item.ItemJson, T>): RichComboBoxBuilder<T> {
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

        build(): RichComboBox<T> {
            return new RichComboBox(this);
        }
    }


}
