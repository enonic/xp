module app_contextwindow_image {
    export class ImageSelectPanel extends api_ui.Panel {




        constructor(contextWindow:app_contextwindow.ContextWindow) {
            super("ImageSelectPanel");
            this.addClass("select-panel");

//            var input:api_form.Input = new api_form.Input("imagePicker");
//            input.setInputType(new api_form.InputTypeName("ImageSelector", false));
//            input.setOccurences(1,1);
//
//            var inputTypeView = api_form_input.InputTypeManager.createView(input.getInputType().getName(), {"relationshipType": {"name": "link"}});
//            this.getHTMLElement().appendChild(inputTypeView.getHTMLElement());
//            inputTypeView.layout(input, null);

            var comboBoxConfig = <api_ui_combobox.ComboBoxConfig<api_content.ContentSummary>> {
                rowHeight: 50,
                maximumOccurrences: 1
            };

            var comboBox = new api_ui_combobox.ComboBox("imagePicker", comboBoxConfig);

            this.appendChild(comboBox);

            //this.getHTMLElement().appendChild(this.imageSelector.getHTMLElement());

        }


    }
}