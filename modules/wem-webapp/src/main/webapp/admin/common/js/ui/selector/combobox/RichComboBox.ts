module api.ui.selector.combobox {

    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class RichComboBox<T> extends api.ui.form.CompositeFormInputEl {

        loader: api.util.loader.BaseLoader<api.item.ItemJson, T>;

        comboBoxView: api.dom.DivEl;

        comboBoxName: string;

        selectedOptionsView: any;

        comboBox: api.ui.selector.combobox.ComboBox<T>;

        identifierMethod: string;

        maximumOccurrences: number;

        private loadingListeners: {():void;}[];

        private loadedListeners: {(items: T[]):void;}[];

        private inputValueChangedListeners: {(oldValue: string, newValue: string, grid: api.ui.grid.Grid<api.ui.selector.Option<T>>):void;}[];

        private optionSelectedListeners: {(item: api.ui.selector.Option<T>):void;}[];

        constructor(config: RichComboBoxBuilder<T>) {
            this.identifierMethod = config.identifierMethod;

            this.comboBoxView = new api.dom.DivEl();
            this.selectedOptionsView = config.selectedOptionsView;
            this.selectedOptionsView.hide();
            this.maximumOccurrences = config.maximumOccurrences;
            this.comboBox = this.createComboBox(config.comboBoxName);
            if (config.loader) {
                this.setLoader(config.loader);
            }

            super(this.comboBox, this.selectedOptionsView);

            this.loadedListeners = [];
            this.loadingListeners = [];
            this.inputValueChangedListeners = [];
            this.optionSelectedListeners = [];

            this.addClass('rich-combobox');
        }

        getSelectedValues(): T[] {
            return this.comboBox.getSelectedData().map((option: api.ui.selector.Option<T>) => {
                return option.displayValue;
            });
        }

        getValues(): T[] {
            return this.comboBox.getValues().map((option: api.ui.selector.Option<T>) => {
                return option.displayValue;
            });
        }

        getStringValues(): string[] {
            return this.comboBox.getSelectedData().map((option: api.ui.selector.Option<T>) => {
                return option.value;
            });
        }

        maximumOccurrencesReached(): boolean {
            return this.comboBox.maximumOccurrencesReached();
        }

        countSelected(): number {
            return this.comboBox.countSelected();
        }

        select(value: T) {
            this.comboBox.selectOption({
                value: value[this.identifierMethod](),
                displayValue: value
            });
        }

        private createComboBox(name: string): api.ui.selector.combobox.ComboBox<T> {

            var comboBoxConfig = this.createConfig();

            return new api.ui.selector.combobox.ComboBox(name, comboBoxConfig);
        }

        private setupLoader() {
            this.comboBox.addSelectedOptionRemovedListener(()=> {
                this.loader.search("");
            });
            this.comboBox.onValueChanged((event: ComboBoxValueChangedEvent<T>) => {
                this.loader.search(event.getNewValue());
                this.notifyInputValueChanged(event.getOldValue(), event.getNewValue(), event.getGrid());
            });
            this.comboBox.onOptionSelected((event: OptionSelectedEvent<T>) => {
                this.selectedOptionsView.show();
                this.notifyOptionSelected(event.getItem());
            });

            this.loader.onLoadingData((event: api.util.loader.event.LoadingDataEvent) => {
                this.comboBox.setLabel("Searching...");
                this.notifyLoading();
            });

            this.loader.onLoadedData((event: api.util.loader.event.LoadedDataEvent<T>) => {
                var options = this.createOptions(event.getData());
                this.comboBox.setOptions(options);
                this.notifyLoaded(event.getData());
            });

            this.loader.search("");
        }

        private createOptions(items: T[]): api.ui.selector.Option<T>[] {
            var options = [];
            items.forEach((itemInst: T) => {
                options.push({
                    value: itemInst[this.identifierMethod](),
                    displayValue: itemInst
                });
            });
            return options;
        }

        optionFormatter(row: number, cell: number, itemInst: T, columnDef: any, dataContext: api.ui.selector.Option<T>): string {

            return "";
        }

        createConfig(): api.ui.selector.combobox.ComboBoxConfig<T> {
            return  {
                rowHeight: 50,
                maximumOccurrences: this.maximumOccurrences,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                hideComboBoxWhenMaxReached: true
            };
        }

        setLoader(loader: api.util.loader.BaseLoader<api.item.ItemJson, T>) {
            this.loader = loader;
            this.setupLoader();
        }

        setInputIconUrl(url: string) {
            this.comboBox.setInputIconUrl(url);
        }

        addSelectedOptionRemovedListener(listener: {(option: SelectedOption<T>):void;}) {
            this.comboBox.addSelectedOptionRemovedListener(listener);
        }

        addInputValueChangedListener(listener: {(oldValue: string, newValue: string, grid: api.ui.grid.Grid<api.ui.selector.Option<T>>): void;}) {
            this.inputValueChangedListeners.push(listener);
        }

        addOptionSelectedListener(listener: {(item: api.ui.selector.Option<T>): void;}) {
            this.optionSelectedListeners.push(listener);
        }

        addLoadingListener(listener: {(): void;}) {
            this.loadingListeners.push(listener);
        }

        addLoadedListener(listener: {(items: T[]): void;}) {
            this.loadedListeners.push(listener);
        }

        removeLoadedListener(listenerToBeRemoved: {(items: T[]): void;}) {
            var index = this.loadedListeners.indexOf(listenerToBeRemoved);
            this.loadedListeners.splice(index, 1);
        }

        private notifyInputValueChanged(oldValue: string, newValue: string, grid: api.ui.grid.Grid<api.ui.selector.Option<T>>) {
            this.inputValueChangedListeners.forEach((listener) => {
                listener(oldValue, newValue, grid);
            });
        }

        private notifyOptionSelected(item: api.ui.selector.Option<T>) {
            this.optionSelectedListeners.forEach((listener) => {
                listener(item);
            });
        }

        private notifyLoading() {
            this.loadingListeners.forEach((listener) => {
                listener();
            });
        }

        private notifyLoaded(items: T[]) {
            this.loadedListeners.forEach((listener) => {
                listener(items);
            });
        }

        giveFocus(): boolean {
            return this.comboBox.giveFocus();
        }
    }

    export interface RichComboBoxConfig<T> {
        comboBoxName?:string;
        loader?:api.util.loader.BaseLoader<api.item.ItemJson, T>;
        selectedOptionsView?:SelectedOptionsView<T>;
        identifierMethod?:string;
    }

    export class RichComboBoxBuilder<T> {

        comboBoxName: string;

        loader: api.util.loader.BaseLoader<api.item.ItemJson, T>;

        selectedOptionsView: SelectedOptionsView<T>;

        identifierMethod: string = "getId";

        maximumOccurrences: number = 0;

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

        build(): RichComboBox<T> {
            return new RichComboBox(this);
        }
    }


}
