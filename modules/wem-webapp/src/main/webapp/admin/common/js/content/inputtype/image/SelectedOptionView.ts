module api.content.inputtype.image {

    export class SelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<api.content.ContentSummary> {

        private static IMAGE_SIZE: number = 270;

        private icon: api.dom.ImgEl;

        private check: api.ui.CheckboxInput;

        private selectionChangeListeners: {(option: SelectedOptionView, checked: boolean): void;}[] = [];

        constructor(option: api.ui.selector.Option<api.content.ContentSummary>) {
            super(option);
        }

        layout() {
            var content = this.getOption().displayValue;
            this.icon = new api.dom.ImgEl(content.getIconUrl() + "?thumbnail=false&size=" + SelectedOptionView.IMAGE_SIZE);
            this.appendChild(this.icon);

            var label = new api.dom.DivEl("label");
            label.getEl().setInnerHtml(content.getName().toString());
            this.appendChild(label);

            this.check = new api.ui.CheckboxInput();
            this.check.onClicked((event: MouseEvent) => {
                // swallow event to prevent scaling when clicked on checkbox
                event.stopPropagation();
            });
            this.check.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyChecked(event.getNewValue() == 'true');
            });
            this.appendChild(this.check);
        }

        getIcon(): api.dom.ImgEl {
            return this.icon;
        }

        toggleChecked() {
            this.check.toggleChecked();
        }

        notifyChecked(checked: boolean) {
            this.selectionChangeListeners.forEach((listener) => {
                listener(this, checked);
            });
        }

        onChecked(listener: {(option: SelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners.push(listener);
        }

        removeCheckListener(listener: {(option: SelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners = this.selectionChangeListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }
}