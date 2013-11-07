module app_contextwindow_image {
    export class ImageSelectPanel extends api_ui.Panel {

        private selectedOptionsView:ImageSelectPanelSelectedOptionsView;


        constructor(contextWindow:app_contextwindow.ContextWindow) {
            super("ImageSelectPanel");
            this.addClass("select-panel");

            this.selectedOptionsView = new ImageSelectPanelSelectedOptionsView();

            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                maximumOccurrences: 1,
                optionFormatter: this.optionFormatter,
                selectedOptionsView: this.selectedOptionsView
            };

            var comboBox = new api_ui_combobox.ComboBox("imagePicker", comboBoxConfig);

            var contentSummaryLoader = new api_form_input_type.ContentSummaryLoader();
            contentSummaryLoader.addListener({
                onLoading: () => {
                    comboBox.setLabel("Searching...");
                },
                onLoaded: (contentSummaries:api_content.ContentSummary[]) => {
                    var options = this.createOptions(contentSummaries);
                    console.log("options", options);
                    comboBox.setOptions(options);
                }
            });

            comboBox.getEl().addEventListener("keyup", (e) => {
                console.log(e);
                contentSummaryLoader.search("");
            });


            this.appendChild(comboBox);
            this.appendChild(this.selectedOptionsView);

            //this.getHTMLElement().appendChild(this.imageSelector.getHTMLElement());

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

