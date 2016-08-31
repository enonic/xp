module api.ui.selector.combobox {

    export class RichSelectedOptionView<T> extends api.ui.selector.combobox.BaseSelectedOptionView<T> {

        private optionDisplayValue: T;

        private size: api.app.NamesAndIconViewSize;
        
        private editable: boolean;
        private draggable: boolean;
        private removable: boolean;

        constructor(option: api.ui.selector.Option<T>, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            this.optionDisplayValue = option.displayValue;
            this.size = size;

            this.editable = false;
            this.draggable = false;
            this.removable = true;

            super(option);
        }

        protected setIsEditable(value: boolean) {
            this.editable = value;
        }

        protected setIsDraggable(value: boolean) {
            this.draggable = value;
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

        protected createActionButtons(content: T): api.dom.Element[] {
            var buttons = [];
            if (this.draggable) {
                buttons.push(new api.dom.DivEl("drag-control"));
            }
            if (this.editable) {
                buttons.push(this.createEditButton(content));
            }
            if (this.removable) {
                buttons.push(this.createRemoveButton());
            }
            return buttons;
        }

        protected createView(content: T): api.dom.Element {

            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(this.size).build();

            namesAndIconView
                .setMainName(this.resolveTitle(content))
                .setSubName(this.resolveSubTitle(content));

            var url = this.resolveIconUrl(content);
            if (!api.util.StringHelper.isBlank(url)) {
                namesAndIconView.setIconUrl(this.resolveIconUrl(content) + '?crop=false')
            } else {
                namesAndIconView.setIconClass(this.resolveIconClass(content));
            }

            return namesAndIconView;
        }

        protected createEditButton(content: T): api.dom.AEl {
            let editButton = new api.dom.AEl("edit");
            editButton.onClicked((event: Event) => {
                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return editButton;
        }

        protected createRemoveButton(): api.dom.AEl {
            let removeButton = new api.dom.AEl("remove");
            removeButton.onClicked((event: Event) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            return removeButton;
        }

        doRender(): wemQ.Promise<boolean> {
            this.appendChildren(...this.createActionButtons(this.optionDisplayValue));
            this.appendChild(this.createView(this.optionDisplayValue));

            return wemQ(true);
        }
    }
}