module api.ui.selector.combobox {

    export class SelectedOptionView<T> extends api.dom.DivEl {

        private option: api.ui.selector.Option<T>;

        private optionValue: api.dom.DivEl;

        private selectedOptionToBeRemovedListeners: {(): void;}[] = [];

        constructor(option: api.ui.selector.Option<T>) {
            super("selected-option");
            this.option = option;
            this.layout();
        }

        setOption(option: api.ui.selector.Option<T>) {
            this.option = option;
            if (this.optionValue) {
                this.optionValue.getEl().setInnerHtml(this.option.displayValue.toString());
            }
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
                listener();
            });
        }

        onSelectedOptionRemoveRequest(listener: {(): void;}) {
            this.selectedOptionToBeRemovedListeners.push(listener);
        }

        unSelectedOptionRemoveRequest(listener: {(): void;}) {
            this.selectedOptionToBeRemovedListeners = this.selectedOptionToBeRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}