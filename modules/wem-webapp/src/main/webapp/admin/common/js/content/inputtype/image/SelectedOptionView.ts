module api.content.inputtype.image {

    export class SelectedOptionView extends api.ui.selector.combobox.SelectedOptionView<api.content.ContentSummary> {

        private static IMAGE_SIZE: number = 270;

        private icon: api.dom.ImgEl;

        private check: api.ui.CheckboxInput;

        private selectionChangeListeners: {(option: SelectedOptionView, checked: boolean): void;}[] = [];

        private focusChangeListeners: {(option: SelectedOptionView, focused: boolean): void;}[] = [];

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

            this.check.onMouseDown((event: MouseEvent) => {
                // swallow event and prevent checkbox focus on click
                event.stopPropagation();
                event.preventDefault();
            })

            this.check.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.notifyChecked(event.getNewValue() == 'true');
            });

            this.check.onFocus(() => {
                this.notifyFocused(true);
            });
            this.check.onBlur(() => {
                this.notifyFocused(false);
            });
            this.appendChild(this.check);
        }

        getIcon(): api.dom.ImgEl {
            return this.icon;
        }

        getCheckbox(): api.ui.CheckboxInput {
            return this.check;
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

        unChecked(listener: {(option: SelectedOptionView, checked: boolean): void;}) {
            this.selectionChangeListeners = this.selectionChangeListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        // both focus nad blur events
        onFocused(listener: {(option: SelectedOptionView, focused: boolean): void;}) {
            this.focusChangeListeners.push(listener);
        }

        unFocused(listener: {(option: SelectedOptionView, focused: boolean): void;}) {
            this.focusChangeListeners = this.focusChangeListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifyFocused(focused: boolean) {
            this.focusChangeListeners.forEach((listener) => {
                listener(this, focused);
            });
        }

    }
}