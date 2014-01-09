module api.ui.combobox {

    export class RichComboBox<T extends api.item.BaseItem> extends api.ui.form.CompositeFormInputEl {

        loader:api.util.Loader;
        te
        comboBoxView:api.dom.DivEl;

        selectedOptionsView:any;
        comboBox:api.ui.combobox.ComboBox<T>;

        constructor(builder:RichComboBoxBuilder<T>)
        {
            this.loader = builder.loader;

            this.comboBoxView = new api.dom.DivEl();

            this.selectedOptionsView = builder.selectedOptionsView;
            this.selectedOptionsView.hide();

            this.comboBox = this.createComboBox(builder.comboBoxName);

            super(this.comboBox, this.selectedOptionsView);
            this.addClass('rich-combobox');
        }


        private createComboBox(name:string):api.ui.combobox.ComboBox<T> {

            var comboBoxConfig = this.createConfig();

            var comboBox = new api.ui.combobox.ComboBox(name, comboBoxConfig);

            comboBox.addSelectedOptionRemovedListener(()=> {
                this.loader.search("");
            });
            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    this.loader.search(newValue);
                },
                onOptionSelected: (item:api.ui.combobox.Option<T>) => {
                    this.selectedOptionsView.show();
                }
            });

            this.loader.addListener({
                onLoading: () => {
                    comboBox.setLabel("Searching...");
                },
                onLoaded: (modules:T[]) => {
                    var options = this.createOptions(modules);
                    comboBox.setOptions(options);
                }
            });

            this.loader.search("");

            return comboBox;
        }

        private createOptions(contents:T[]):api.ui.combobox.Option<T>[] {
            var options = [];
            contents.forEach((moduleInst:T) => {
                options.push({
                    value: moduleInst.getId(),
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
    }

    export class RichComboBoxBuilder<T extends api.item.BaseItem> {

        comboBoxName:string;

        loader:api.util.Loader;

        selectedOptionsView:SelectedOptionsView<T>


        setComboBoxName(comboBoxName:string):RichComboBoxBuilder<T> {
            this.comboBoxName = comboBoxName;
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