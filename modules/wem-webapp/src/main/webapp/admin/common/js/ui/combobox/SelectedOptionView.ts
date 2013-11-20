module api_ui_combobox {

    export class SelectedOptionView<T> extends api_dom.DivEl{

        private option:Option<T>;

        private selectedOptionToBeRemovedListeners:{(toBeRemoved:SelectedOptionView<T>): void;}[] = [];

        constructor(option:Option<T>) {
            super("ComboBoxSelectedOptionView", "selected-option");
            this.option = option;
            this.layout();
        }

        getOption():Option<T> {
            return this.option;
        }

        layout() {
            var removeButtonEl = new api_dom.AEl(null, "remove");
            var optionValueEl = new api_dom.DivEl(null, 'option-value');
            optionValueEl.getEl().setInnerHtml(this.option.displayValue.toString());

            removeButtonEl.getEl().addEventListener('click', (event:Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(optionValueEl);
        }

        notifySelectedOptionToBeRemoved() {
            this.selectedOptionToBeRemovedListeners.forEach( (listener) => {
                listener(this);
            });
        }

        addSelectedOptionToBeRemovedListener(listener:{(toBeRemoved:SelectedOptionView<T>): void;}) {
            this.selectedOptionToBeRemovedListeners.push(listener);
        }

        removeSelectedOptionToBeRemovedListener(listener:{(toBeRemoved:SelectedOptionView<T>): void;}) {
            this.selectedOptionToBeRemovedListeners = this.selectedOptionToBeRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}