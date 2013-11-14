module app_contextwindow_image {
    export class ImageSelectPanelSelectedOptionsView extends api_ui_combobox.ComboBoxSelectedOptionsView<api_content.ContentSummary> {

        constructor() {
            super();
        }

        addItem(item:api_ui_combobox.OptionData<api_content.ContentSummary>) {
            var content = item.displayValue;

            var container = new api_dom.DivEl();
            container.addClass("container");

            var image = new api_dom.ImgEl();
            image.getEl().setSrc(content.getIconUrl());
            image.getEl().setHeight("35px");
            image.getEl().setWidth("35px");

            var label = new api_dom.DivEl(null, "label");
            label.getEl().setInnerHtml(content.getName());

            var removeButton = new api_dom.AEl(null, "remove");
            removeButton.setText("&times;");
            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.removeItem(item);
                this.notifySelectedOptionRemoved(item);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            container.appendChild(image);
            container.appendChild(label);
            container.appendChild(removeButton);


            this.addOption(container, item);
            this.appendChild(container);
        }



    }
}