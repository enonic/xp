module app.contextwindow.image {

    export class ImageSelectPanelSelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<api.content.ContentSummary> {

        private content:api.content.ContentSummary;

        constructor(option:api.ui.selector.Option<api.content.ContentSummary>) {
            this.content = option.displayValue;
            super(option);
        }

        layout() {

            var image = new api.dom.ImgEl();
            image.getEl().setSrc(this.content.getIconUrl());
            image.getEl().setHeight("48px");
            image.getEl().setWidth("48px");

            var container = new api.dom.DivEl("container");

            var title = new api.dom.DivEl("title");
            title.getEl().setInnerHtml(this.content.getName().toString());

            var subtitle = new api.dom.DivEl("subtitle");
            subtitle.getEl().setInnerHtml(api.util.limitString(this.content.getPath().toString(), 32));

            container.appendChild(title);
            container.appendChild(subtitle);


            var removeButton = new api.dom.AEl("remove");
            removeButton.onClicked((event:Event) => {
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