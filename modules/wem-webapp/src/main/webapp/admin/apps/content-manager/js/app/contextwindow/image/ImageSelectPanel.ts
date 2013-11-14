module app_contextwindow_image {
    export class ImageSelectPanel extends api_ui.Panel {

        private contextWindow:app_contextwindow.ContextWindow;

        private comboBox:api_ui_combobox.ComboBox<api_content.ContentSummary>;

        private selectedOptionsView:ImageSelectPanelSelectedOptionsView;

        private templateForm:api_ui.Panel;

        private selectedItem:api_ui_combobox.OptionData<api_content.ContentSummary>;

        private liveEditItems:{[key: number]: api_ui_combobox.OptionData<api_content.ContentSummary>};

        private liveEditIndex:number = 1;

        constructor(contextWindow:app_contextwindow.ContextWindow) {
            super("ImageSelectPanel");
            this.addClass("select-panel");
            this.contextWindow = contextWindow;

            this.liveEditItems = {};

            this.selectedOptionsView = new ImageSelectPanelSelectedOptionsView();
            this.selectedOptionsView.addListener({
                onSelectedOptionRemoved: (item:api_ui_combobox.OptionData<api_content.ContentSummary>) => {
                    this.contextWindow.getLiveEditWindow().LiveEdit.component.dragdropsort.EmptyComponent.restoreEmptyComponent();
                }
            });
            this.comboBox = this.createComboBox();

            this.templateForm = new api_ui.Panel();
            this.templateForm.getEl().setInnerHtml("Template goes here");
            this.templateForm.hide();

            this.appendChild(this.comboBox);
            this.appendChild(this.selectedOptionsView);
            this.appendChild(this.templateForm);

            app_contextwindow.ComponentSelectEvent.on((event) => {
                if (!event.getComponent().isEmpty()) {
                    this.itemSelected();
                    if (event.getComponent().getItemId()) {
                        console.log("itemId:",event.getComponent().getItemId());

                        this.comboBox.removeSelectedItem(this.selectedItem);
                        var itemId = event.getComponent().getItemId();
                        this.selectedItem = this.liveEditItems[itemId];
                        this.comboBox.selectOption(this.selectedItem);
                    }
                } else {
                    this.comboBox.removeSelectedItem(this.selectedItem);
                }


            });

            app_contextwindow.ComponentDeselectEvent.on((event) => {
                this.itemRemoved();
            });

            app_contextwindow.ComponentRemovedEvent.on((event) => {
                this.comboBox.removeSelectedItem(this.selectedItem);
                this.itemRemoved();
            });
        }

        private itemSelected() {
            //this.comboBox.hide();
            this.templateForm.show();
        }

        private itemRemoved() {
            this.comboBox.show();
            this.templateForm.hide();
        }

        private createComboBox():api_ui_combobox.ComboBox<api_content.ContentSummary> {

            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                maximumOccurrences: 1,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView
            };

            var comboBox = new api_ui_combobox.ComboBox("imagePicker", comboBoxConfig);

            comboBox.addListener({
                onInputValueChanged: (oldValue, newValue, grid) => {
                    contentSummaryLoader.search(newValue);
                },
                onSelectedOptionRemoved: (item:api_ui_combobox.OptionData<api_content.ContentSummary>) => {
                    this.selectedItem = null;
                },
                onOptionSelected: (item:api_ui_combobox.OptionData<api_content.ContentSummary>) => {
                    this.selectedItem = item;
                    this.contextWindow.getLiveEditWindow().LiveEdit.component.dragdropsort.EmptyComponent.loadComponent('10070', this.liveEditIndex);
                    this.liveEditItems[this.liveEditIndex] = item;
                    this.itemSelected();
                    this.liveEditIndex++;
                }
            });

            var contentSummaryLoader = new api_form_inputtype_content.ContentSummaryLoader();
            contentSummaryLoader.addListener({
                onLoading: () => {
                    comboBox.setLabel("Searching...");
                },
                onLoaded: (contentSummaries:api_content.ContentSummary[]) => {
                    var options = this.createOptions(contentSummaries);
                    comboBox.setOptions(options);
                }
            });

            contentSummaryLoader.search("");

            return comboBox;
        }

        private createOptions(contents:api_content.ContentSummary[]):api_ui_combobox.OptionData<api_content.ContentSummary>[] {
            var options = [];
            contents.forEach((content:api_content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row:number, cell:number, content:api_content.ContentSummary, columnDef:any, dataContext:api_ui_combobox.OptionData<api_content.ContentSummary>):string {
            var img = new api_dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api_dom.DivEl();
            contentSummary.setClass("content-summary");

            var displayName = new api_dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api_dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }
    }
}

