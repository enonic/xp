module api.ui.selector.combobox {

    export class RichSelectedOptionView<T extends api.item.Item> extends api.ui.selector.combobox.SelectedOptionView<T> {

        private optionDisplayValue:T;

        constructor(option:api.ui.selector.combobox.Option<T>) {
            this.optionDisplayValue = option.displayValue;
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
                .setIconUrl(this.resolveIconUrl(this.optionDisplayValue))
                .setMainName(this.resolveTitle(this.optionDisplayValue))
                .setSubName(this.resolveSubTitle(this.optionDisplayValue));

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