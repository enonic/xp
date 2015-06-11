module api.ui.selector.combobox {

    export class RichSelectedOptionView<T extends api.item.Item> extends api.ui.selector.combobox.BaseSelectedOptionView<T> {

        private optionDisplayValue: T;

        private size: api.app.NamesAndIconViewSize;

        constructor(option: api.ui.selector.Option<T>, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            this.optionDisplayValue = option.displayValue;
            this.size = size;
            super(option);
        }

        resolveIconUrl(content: T): string {
            return "";
        }

        resolveTitle(content: T): string {
            return "";
        }

        resolveSubTitle(content: T): string {
            return "";
        }

        resolveIconClass(content: T): string {
            return "";
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(this.size).build();

            namesAndIconView
                .setMainName(this.resolveTitle(this.optionDisplayValue))
                .setSubName(this.resolveSubTitle(this.optionDisplayValue));

            var url = this.resolveIconUrl(this.optionDisplayValue);
            if (!api.util.StringHelper.isBlank(url)) {
                namesAndIconView.setIconUrl(this.resolveIconUrl(this.optionDisplayValue) + '?crop=false')
            } else {
                namesAndIconView.setIconClass(this.resolveIconClass(this.optionDisplayValue));
            }

            var removeButton = new api.dom.AEl("remove");
            removeButton.onClicked((event: Event) => {
                this.notifySelectedOptionRemoveRequested();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(new api.dom.DivEl("drag-control"));
            this.appendChild(removeButton);
            this.appendChild(namesAndIconView);
        }
    }
}