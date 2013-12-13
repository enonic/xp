module app_contextwindow_image {

    export class ImageSelectPanelSelectedOptionView extends api_ui_combobox.SelectedOptionView<api_content.ContentSummary> {

        private content:api_content.ContentSummary;

        constructor(option:api_ui_combobox.Option<api_content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        layout() {

            var image = new api_dom.ImgEl();
            image.getEl().setSrc(this.content.getIconUrl());
            image.getEl().setHeight("48px");
            image.getEl().setWidth("48px");

            var container = new api_dom.DivEl(null, "container");

            var title = new api_dom.DivEl(null, "title");
            title.getEl().setInnerHtml(this.content.getName().toString());

            var subtitle = new api_dom.DivEl(null, "subtitle");
            subtitle.getEl().setInnerHtml(api_util.limitString(this.content.getPath().toString(), 32));

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