module api.ui.combobox {

    export class RichComboBox<T> extends api.ui.form.CompositeFormInputEl {

        loader:api.util.Loader;

        comboBoxView:api.dom.DivEl;

        selectedOptionsView:any;

        comboBox:api.ui.combobox.ComboBox<T>;

        identifierMethod:string;

        private loadingListeners:{():void;}[];

        private loadedListeners:{(modules:T[]):void;}[];

        private inputValueChangedListeners:{(oldValue: string, newValue: string, grid: api.ui.grid.Grid<Option<T>>):void;}[];

        private optionSelectedListeners:{(item:api.ui.combobox.Option<T>):void;}[];

        constructor(builder:RichComboBoxBuilder<T>)
        {
            this.identifierMethod = builder.identifierMethod;

            this.comboBoxView = new api.dom.DivEl();
            this.selectedOptionsView = builder.selectedOptionsView;
            this.selectedOptionsView.hide();
            this.comboBox = this.createComboBox(builder.comboBoxName);
            if (builder.loader) {
                this.setLoader(builder.loader);
            }

            super(this.comboBox, this.selectedOptionsView);

            this.loadedListeners = [];
            this.loadingListeners = [];
            this.inputValueChangedListeners = [];
            this.optionSelectedListeners = [];

            this.addClass('rich-combobox');
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
                    console.log("LOADED", modules, this.loader);
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
                maximumOccurrences: 0,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                hideComboBoxWhenMaxReached: true
            };
        }

        setLoader(loader:api.util.Loader) {
            this.loader = loader;
            this.setupLoader();
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

    export class RichComboBoxBuilder<T> {

        comboBoxName:string;

        loader:api.util.Loader;

        selectedOptionsView:SelectedOptionsView<T>;

        identifierMethod:string = "getId";


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

        build():RichComboBox<T> {
            return new RichComboBox(this);
        }
    }


}