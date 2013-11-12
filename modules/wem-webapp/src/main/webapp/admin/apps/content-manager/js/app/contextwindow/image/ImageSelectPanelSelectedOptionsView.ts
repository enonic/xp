module app_contextwindow_image {
    export class ImageSelectPanelSelectedOptionsView extends api_ui_combobox.ComboBoxSelectedOptionsView<api_content.ContentSummary> {

        private selectedItem:api_ui_combobox.OptionData<api_content.ContentSummary>;

        private container:api_dom.DivEl;

        constructor() {
            super();
        }

        addItem(imageData:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            this.selectedItem = imageData;
            var item = imageData.displayValue;

            this.container = new api_dom.DivEl();
            this.container.addClass("container");

            var image = new api_dom.ImgEl();
            image.getEl().setSrc(item.getIconUrl());
            image.getEl().setHeight("35px");
            image.getEl().setWidth("35px");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(item.getName());

            var removeButton = new api_dom.AEl(null, "remove");
            removeButton.setText("&times;");
            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.container.remove();
                this.notifySelectedOptionRemoved(imageData);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.container.appendChild(image);
            this.container.appendChild(label);
            this.container.appendChild(removeButton);


            this.appendChild(this.container);
        }



        removeItem(item:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            this.container.remove();
            //this.notifySelectedOptionRemoved(item);
            this.selectedItem = null;
        }
    }
}