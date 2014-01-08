module api.ui.combobox {

    export class RichSelectedOptionView<T extends api.item.BaseItem> extends api.ui.combobox.SelectedOptionView<T> {

        private content:T;

        constructor(option:api.ui.combobox.Option<T>) {
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

            var image = new api.dom.ImgEl();
            image.getEl().setSrc(this.resolveIconUrl(this.content));
            image.getEl().setHeight("48px");
            image.getEl().setWidth("48px");

            var container = new api.dom.DivEl(null, "container");

            var title = new api.dom.DivEl(null, "title");
            title.getEl().setInnerHtml(this.resolveTitle(this.content));

            var subtitle = new api.dom.DivEl(null, "subtitle");
            subtitle.getEl().setInnerHtml(api.util.limitString(this.resolveSubTitle(this.content), 16));

            container.appendChild(title);
            container.appendChild(subtitle);


            var removeButton = new api.dom.AEl(null, "remove");
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