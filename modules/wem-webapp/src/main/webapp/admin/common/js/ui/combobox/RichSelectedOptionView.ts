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

        layout()
        {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();
            namesAndIconView
                .setIconUrl(this.resolveIconUrl(this.content))
                .setMainName(this.resolveTitle(this.content))
                .setSubName(this.resolveSubTitle(this.content));

            var removeButton = new api.dom.AEl("remove");
            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButton);
            this.appendChild(namesAndIconView);
        }
    }
}