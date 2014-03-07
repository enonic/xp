module app.contextwindow.image {

    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import LoadingDataEvent = api.util.loader.event.LoadingDataEvent;

    export interface ImageSelectPanelConfig {

        liveEditWindow:any;
    }

    export class ImageSelectPanel extends api.ui.Panel {

        private liveEditWindow: any;

        private image: api.content.page.image.ImageComponent;

        private comboBox: api.ui.selector.combobox.ComboBox<api.content.ContentSummary>;

        private selectedOptionsView: ImageSelectPanelSelectedOptionsView;

        private templatePanel: api.ui.Panel;

        private recentPanel: RecentPanel;

        private deck: api.ui.DeckPanel;

        private selectedOption: api.ui.selector.Option<api.content.ContentSummary>;

        private liveEditItems: {[key: number]: api.content.ContentSummary };

        private liveEditIndex: number = 1;

        constructor(config: ImageSelectPanelConfig) {
            super("select-panel");
            var comboBoxWrapper = new api.dom.DivEl();
            this.liveEditWindow = config.liveEditWindow;

            this.liveEditItems = {};

            this.selectedOptionsView = new ImageSelectPanelSelectedOptionsView();
            this.selectedOptionsView.hide();
            this.comboBox = this.createComboBox();
            this.comboBox.addSelectedOptionRemovedListener(() => {
                this.liveEditWindow.LiveEdit.component.dragdropsort.EmptyComponent.restoreEmptyComponent();
                this.itemRemoved();
            });


            this.deck = new api.ui.DeckPanel();

            this.recentPanel = new RecentPanel();

            this.templatePanel = new api.ui.Panel("template-panel");
            this.templatePanel.getEl().setInnerHtml("Template goes here");

            this.deck.addPanel(this.recentPanel);
            this.deck.addPanel(this.templatePanel);

            this.deck.showPanel(0);


            comboBoxWrapper.appendChild(this.comboBox);
            comboBoxWrapper.appendChild(this.selectedOptionsView);
            comboBoxWrapper.getEl().setHeight("50px");
            this.appendChild(comboBoxWrapper);
            this.appendChild(this.deck);

//            SelectComponentEvent.on((event) => {
//                //TODO: set image here
//                if (!event.getComponent().isEmpty()) {
//                    this.image = new api.content.page.image.ImageComponent();
//                    this.itemSelected();
//                    if (event.getComponent().getItemId()) {
//                        console.log("itemId:", event.getComponent().getItemId());
//                        var itemId = event.getComponent().getItemId();
//                        this.setSelectedContent(this.liveEditItems[itemId]);
//                    }
//                } else if (this.selectedOption != null) {
//                    this.comboBox.removeSelectedItem(this.selectedOption, true);
//                }
//
//
//            });

//            ComponentDeselectEvent.on((event) => {
//                this.itemRemoved();
//            });

//            ComponentRemovedEvent.on((event) => {
//                if (this.selectedOption != null) {
//                    this.comboBox.removeSelectedItem(this.selectedOption);
//                    this.itemRemoved();
//                }
//            });

            this.addGridListeners();
        }

        private addGridListeners() {
            this.recentPanel.getGrid().setOnClick((event, data: api.ui.grid.GridOnClickData) => {
                var option = <api.ui.selector.Option<api.content.ContentSummary>> {
                    //TODO: what is value used for??
                    value: "test",
                    displayValue: this.recentPanel.getDataView().getItem(data.row)
                };

                this.comboBox.selectOption(option);
            });
        }

        private itemSelected() {
            this.selectedOptionsView.show();
            this.deck.showPanel(1);
        }

        private itemRemoved() {
            this.selectedOptionsView.hide();
            this.deck.showPanel(0);
        }

        private createComboBox(): api.ui.selector.combobox.ComboBox<api.content.ContentSummary> {

            var comboBoxConfig = <api.ui.selector.combobox.ComboBoxConfig<api.content.ContentSummary>> {
                rowHeight: 50,
                maximumOccurrences: 1,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView,
                hideComboBoxWhenMaxReached: true
            };

            var comboBox = new api.ui.selector.combobox.ComboBox("imagePicker", comboBoxConfig);

            comboBox.addSelectedOptionRemovedListener(()=> {
                this.selectedOption = null;
            });

            comboBox.onValueChanged((event: api.ui.selector.combobox.ComboBoxValueChangedEvent<api.content.ContentSummary>) => {
                contentSummaryLoader.search(event.getNewValue());
            });
            comboBox.onOptionSelected((event: api.ui.selector.OptionSelectedEvent<api.content.ContentSummary>) => {
                //TODO: Mocked live use of image
                var iconUrl = event.getItem().displayValue.getIconUrl();
                this.selectedOption = event.getItem();
                this.liveEditWindow.LiveEdit.component.dragdropsort.EmptyComponent.loadComponent('10070', this.liveEditIndex, iconUrl);
                this.liveEditItems[this.liveEditIndex] = event.getItem().displayValue;
                this.itemSelected();
                this.liveEditIndex++;
            });
            var contentSummaryLoader = new api.form.inputtype.content.ContentSummaryLoader();
            contentSummaryLoader.setAllowedContentTypes(["image"]);
            contentSummaryLoader.onLoadingData((event: LoadingDataEvent) => {
                comboBox.setLabel("Searching...");
            });
            contentSummaryLoader.onLoadedData((event: LoadedDataEvent<api.content.ContentSummary>) => {
                var options = this.createOptions(event.getData());
                comboBox.setOptions(options);
            });

            contentSummaryLoader.search("");

            return comboBox;
        }

        private createOptions(contents: api.content.ContentSummary[]): api.ui.selector.Option<api.content.ContentSummary>[] {
            var options = [];
            contents.forEach((content: api.content.ContentSummary) => {
                options.push({
                    value: content.getId(),
                    displayValue: content
                });
            });
            return options;
        }

        private optionFormatter(row: number, cell: number, content: api.content.ContentSummary, columnDef: any,
                                dataContext: api.ui.selector.Option<api.content.ContentSummary>): string {
            var img = new api.dom.ImgEl();
            img.setClass("icon");
            img.getEl().setSrc(content.getIconUrl());

            var contentSummary = new api.dom.DivEl();
            contentSummary.setClass("content-summary");

            var displayName = new api.dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", content.getDisplayName());
            displayName.getEl().setInnerHtml(content.getDisplayName());

            var path = new api.dom.DivEl();
            path.setClass("path");
            path.getEl().setAttribute("title", content.getPath().toString());
            path.getEl().setInnerHtml(content.getPath().toString());

            contentSummary.appendChild(displayName);
            contentSummary.appendChild(path);

            return img.toString() + contentSummary.toString();
        }

        private setSelectedContent(content: api.content.ContentSummary, removeCurrent: boolean = true) {
            api.util.assertNotNull(content, "Cannot set content null");
            var option: api.ui.selector.Option<api.content.ContentSummary> = {
                value: content.getId(),
                displayValue: content
            };
            if (this.selectedOption != null) {
                this.comboBox.removeSelectedOption(this.selectedOption, true);

            }
            this.selectedOption = option;
            this.comboBox.selectOption(this.selectedOption, true);
        }

        setImage(image: api.content.page.image.ImageComponent) {
            this.image = image;
            this.refreshUI();
        }

        private refreshUI() {
            new api.content.GetContentByIdRequest(this.image.getImage())
                .send()
                .done((jsonResponse: api.rest.JsonResponse<api.content.json.ContentSummaryJson>) => {
                    this.setSelectedContent(new api.content.ContentSummary(jsonResponse.getResult()))
                });
        }
    }
}

