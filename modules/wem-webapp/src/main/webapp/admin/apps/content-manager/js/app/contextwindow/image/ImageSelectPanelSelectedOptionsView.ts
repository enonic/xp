module app_contextwindow_image {
    export class ImageSelectPanelSelectedOptionsView extends api_ui_combobox.ComboBoxSelectedOptionsView<api_content.ContentSummary> {

        private selectedItem:api_ui_combobox.OptionData<api_content.ContentSummary>;

        constructor() {
            super();
        }

        addItem(imageData:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            this.selectedItem = imageData;
            var item = imageData.displayValue;

            var container = new api_dom.DivEl();

            var image = new api_dom.ImgEl();
            image.getEl().setSrc(item.getIconUrl());
            image.getEl().setHeight("32px");
            image.getEl().setWidth("32px");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(item.getName());

            var removeButton = new api_dom.AEl(null, "remove");
            removeButton.setText("Remove");
            removeButton.getEl().addEventListener('click', (event:Event) => {
                container.remove();
                this.notifySelectedOptionRemoved(imageData);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            container.appendChild(image);
            container.appendChild(label);
            container.appendChild(removeButton);


            this.appendChild(container);
        }
    }
}