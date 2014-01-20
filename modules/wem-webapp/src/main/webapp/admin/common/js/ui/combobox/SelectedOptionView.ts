module api.ui.combobox {

    export class SelectedOptionView<T> extends api.dom.DivEl{

        private option:Option<T>;

        private selectedOptionToBeRemovedListeners:{(toBeRemoved:SelectedOptionView<T>): void;}[] = [];

        constructor(option:Option<T>) {
            super("selected-option");
            this.option = option;
            this.layout();
        }

        getOption():Option<T> {
            return this.option;
        }

        layout() {
            var removeButtonEl = new api.dom.AEl("remove");
            var optionValueEl = new api.dom.DivEl('option-value');
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