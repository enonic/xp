module api.ui.selector.combobox {

    export class SelectedOptionView<T> extends api.dom.DivEl {

        private option: api.ui.selector.Option<T>;

        private selectedOptionToBeRemovedListeners: {(toBeRemoved: SelectedOptionView<T>): void;}[] = [];

        constructor(option: api.ui.selector.Option<T>) {
            super("selected-option");
            this.option = option;
            this.layout();
        }

        getOption(): api.ui.selector.Option<T> {
            return this.option;
        }

        layout() {
            var removeButtonEl = new api.dom.AEl("remove");
            var optionValueEl = new api.dom.DivEl('option-value');
            optionValueEl.getEl().setInnerHtml(this.option.displayValue.toString());

            removeButtonEl.onClicked((event: Event) => {
                this.notifySelectedOptionRemoveRequested();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(optionValueEl);
        }

        notifySelectedOptionRemoveRequested() {
            this.selectedOptionToBeRemovedListeners.forEach((listener) => {
                listener(this);
            });
        }

        onSelectedOptionRemoveRequest(listener: {(toBeRemoved: SelectedOptionView<T>): void;}) {
            this.selectedOptionToBeRemovedListeners.push(listener);
        }

        unSelectedOptionRemoveRequest(listener: {(toBeRemoved: SelectedOptionView<T>): void;}) {
            this.selectedOptionToBeRemovedListeners = this.selectedOptionToBeRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}