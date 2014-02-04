module api.ui.combobox {

    export class RichComboBox<T> extends api.ui.form.CompositeFormInputEl {

        loader:api.util.Loader;

        comboBoxView:api.dom.DivEl;

        comboBoxName:string;

        selectedOptionsView:any;

        comboBox:api.ui.combobox.ComboBox<T>;

        identifierMethod:string;

        maximumOccurrences:number;

        private loadingListeners:{():void;}[];

        private loadedListeners:{(modules:T[]):void;}[];

        private inputValueChangedListeners:{(oldValue: string, newValue: string, grid: api.ui.grid.Grid<Option<T>>):void;}[];

        private optionSelectedListeners:{(item:api.ui.combobox.Option<T>):void;}[];

        constructor(config:RichComboBoxBuilder<T>)
        {
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

        getValues(): T[] {
            return this.comboBox.getSelectedData().map((option: api.ui.combobox.Option<T>) => {
                return option.displayValue;
            });
        }

        getStringValues(): string[] {
            return this.comboBox.getSelectedData().map((option: api.ui.combobox.Option<T>) => {
                return option.value;
            });
        }

        maximumOccurrencesReached():boolean {
            return this.comboBox.maximumOccurrencesReached();
        }

        select(value:T) {
            this.comboBox.selectOption({
                value: value[this.identifierMethod](),
                displayValue: value
            });
        }

        private createComboBox(name:string):api.ui.combobox.ComboBox<T> {

            var comboBoxConfig = this.createConfig();

            return new api.ui.combobox.ComboBox(name, comboBoxConfig);
        }

        private setupLoader() {
            this.comboBox.addSelectedOptionRemovedListener(()=> {
                this.loader.search("");
            });
            this.comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loader.search(newValue);
                    this.notifyInputValueChanged(oldValue, newValue, grid);
                },
                onOptionSelected: (item:api.ui.combobox.Option<T>) => {
                    this.selectedOptionsView.show();
                    this.notifyOptionSelected(item);
                }
            });

            this.loader.addListener({
                onLoading: () => {
                    this.comboBox.setLabel("Searching...");
                    this.notifyLoading();
                },
                onLoaded: (modules:T[]) => {
                    var options = this.createOptions(modules);
                    this.comboBox.setOptions(options);
                    this.notifyLoaded(modules);
                    console.log("RichComboBox content loaded", modules, this.loader);
                }
            });

            this.loader.search("");
        }

        private createOptions(contents:T[]):api.ui.combobox.Option<T>[] {
            var options = [];
            contents.forEach((moduleInst:T) => {
                options.push({
                    value: moduleInst[this.identifierMethod](),
                    displayValue: moduleInst
                });
            });
            return options;
        }

        optionFormatter(row:number, cell:number, moduleInst:T, columnDef:any, dataContext:api.ui.combobox.Option<T>):string {

            return "";
        }

        createConfig():api.ui.combobox.ComboBoxConfig<T> {
            return  {
                rowHeight: 50,
                maximumOccurrences: this.maximumOccurrences,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                hideComboBoxWhenMaxReached: true
            };
        }

        setLoader(loader:api.util.Loader) {
            this.loader = loader;
            this.setupLoader();
        }

        setInputIconUrl(url:string) {
            this.comboBox.setInputIconUrl(url);
        }

        addSelectedOptionRemovedListener(listener:{(option: SelectedOption<T>):void;}) {
            this.comboBox.addSelectedOptionRemovedListener(listener);
        }

        addInputValueChangedListener(listener:{(oldValue: string, newValue: string, grid: api.ui.grid.Grid<Option<T>>): void;}) {
            this.inputValueChangedListeners.push(listener);
        }

        addOptionSelectedListener(listener:{(item:api.ui.combobox.Option<T>): void;}) {
            this.optionSelectedListeners.push(listener);
        }

        addLoadingListener(listener:{(): void;}) {
            this.loadingListeners.push(listener);
        }

        addLoadedListener(listener:{(modules:T[]): void;}) {
            this.loadedListeners.push(listener);
        }

        removeLoadedListener(listenerToBeRemoved:{(modules:T[]): void;}) {
            var index = this.loadedListeners.indexOf(listenerToBeRemoved);
            this.loadedListeners.splice(index, 1);
            console.log("removing listener from",index, this.loadedListeners);
        }

        private notifyInputValueChanged(oldValue: string, newValue: string, grid: api.ui.grid.Grid<Option<T>>) {
            this.inputValueChangedListeners.forEach( (listener) => {
                listener(oldValue, newValue, grid);
            });
        }

        private notifyOptionSelected(item:api.ui.combobox.Option<T>) {
            this.optionSelectedListeners.forEach( (listener) => {
                listener(item);
            });
        }

        private notifyLoading() {
            this.loadingListeners.forEach( (listener) => {
                listener();
            });
        }

        private notifyLoaded(modules:T[]) {
            this.loadedListeners.forEach( (listener) => {
                listener(modules);
            });
        }

        giveFocus():boolean {
            return this.comboBox.giveFocus();
        }
    }

    export interface RichComboBoxConfig<T> {
        comboBoxName?:string;
        loader?:api.util.Loader;
        selectedOptionsView?:SelectedOptionsView<T>;
        identifierMethod?:string;
    }

    export class RichComboBoxBuilder<T> {

        comboBoxName:string;

        loader:api.util.Loader;

        selectedOptionsView:SelectedOptionsView<T>;

        identifierMethod:string = "getId";

        maximumOccurrences:number = 0;

        setComboBoxName(comboBoxName:string):RichComboBoxBuilder<T> {
            this.comboBoxName = comboBoxName;
            return this;
        }

        setIdentifierMethod(identifierMethod:string):RichComboBoxBuilder<T> {
            this.identifierMethod = identifierMethod;
            return this;
        }

        setLoader(loader:api.util.Loader):RichComboBoxBuilder<T> {
            this.loader = loader;
            return this;
        }

        setSelectedOptionsView(selectedOptionsView:SelectedOptionsView<T>):RichComboBoxBuilder<T> {
            this.selectedOptionsView = selectedOptionsView;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences:number):RichComboBoxBuilder<T> {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        build():RichComboBox<T> {
            return new RichComboBox(this);
        }
    }


}