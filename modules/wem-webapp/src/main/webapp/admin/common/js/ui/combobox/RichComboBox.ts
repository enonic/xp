module api_ui_combobox {

    export class RichComboBox<T extends api_item.BaseItem> extends api_ui_form.CompositeFormInputEl {

        loader:api_rest.Loader;
        comboBoxView:api_dom.DivEl;

        selectedOptionsView:any;
        comboBox:api_ui_combobox.ComboBox<T>;

        constructor(loader:api_rest.Loader, selectedOptionsView:SelectedOptionsView<T>)
        {
            this.loader = loader;

            this.comboBoxView = new api_dom.DivEl();

            this.selectedOptionsView = selectedOptionsView;
            this.selectedOptionsView.hide();

            this.comboBox = this.createComboBox();

            super(this.comboBox, this.selectedOptionsView);
            this.addClass('item-selector');
        }


        private createComboBox():api_ui_combobox.ComboBox<T> {

            var comboBoxConfig = this.createConfig();

            var comboBox = new api_ui_combobox.ComboBox("itemSelector", comboBoxConfig);

            comboBox.addSelectedOptionRemovedListener(()=> {
                console.log("On selected option removed");
            });
            comboBox.addListener({
                                     onInputValueChanged: (oldValue, newValue, grid) => {
                                         this.loader.search(newValue);
                                     },
                                     onOptionSelected: (item:api_ui_combobox.Option<T>) => {
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

        private createOptions(contents:T[]):api_ui_combobox.Option<T>[] {
            var options = [];
            contents.forEach((moduleInst:T) => {
                options.push({
                                 value: moduleInst.getId(),
                                 displayValue: moduleInst
                             });
            });
            return options;
        }

        optionFormatter(row:number, cell:number, moduleInst:T, columnDef:any, dataContext:api_ui_combobox.Option<T>):string {

            return "";
        }

        createConfig():api_ui_combobox.ComboBoxConfig<T> {
            return  {
                rowHeight: 50,
                maximumOccurrences: 0,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                hideComboBoxWhenMaxReached: true
            };
        }
    }

    export class RichSelectedOptionView<T extends api_item.BaseItem> extends api_ui_combobox.SelectedOptionView<T> {

        private content:T;

        constructor(option:api_ui_combobox.Option<T>) {
            this.content = option.displayValue;
            super(option);
        }

        resolveIconUrl(content:T):string
        {
            return "";
        }

        resolveTitle(content:T):string
        {
            return "";
        }

        resolveSubTitle(content:T):string
        {
            return "";
        }

        layout() {

            var image = new api_dom.ImgEl();
            image.getEl().setSrc(this.resolveIconUrl(this.content));
            image.getEl().setHeight("48px");
            image.getEl().setWidth("48px");

            var container = new api_dom.DivEl(null, "container");

            var title = new api_dom.DivEl(null, "title");
            title.getEl().setInnerHtml(this.resolveTitle(this.content));

            var subtitle = new api_dom.DivEl(null, "subtitle");
            subtitle.getEl().setInnerHtml(api_util.limitString(this.resolveSubTitle(this.content), 16));

            container.appendChild(title);
            container.appendChild(subtitle);


            var removeButton = new api_dom.AEl(null, "remove");
            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });


            this.appendChild(image);
            this.appendChild(container);
            this.appendChild(removeButton);

        }
    }
}