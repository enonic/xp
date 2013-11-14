module api_ui_combobox {

    export class ComboBoxSelectedOptionsView<T> extends api_dom.DivEl implements api_event.Observable {

        private listeners:ComboBoxSelectedOptionsViewListener<T>[] = [];

        private selectedOptionsList:ComboBoxSelectedOption<T>[] = [];

        constructor() {
            super("ComboBoxSelectedOptionsView", "selected-options");
        }

        addItem(item:OptionData<T>) {
            var optionEl = new api_dom.DivEl(null, 'selected-option');
            var removeButton = new api_dom.AEl(null, "remove");
            var optionValue = new api_dom.DivEl(null, 'option-value');

            optionEl.appendChild(removeButton);
            optionEl.appendChild(optionValue);
            optionValue.getEl().setInnerHtml(item.displayValue.toString());


            this.appendChild(optionEl);

            removeButton.getEl().addEventListener('click', (event:Event) => {
                this.removeItem(item);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }

        addOption(element:api_dom.Element, item:OptionData<T>) {
            this.selectedOptionsList.push(new ComboBoxSelectedOption<T>(element, item));
        }


        removeItem(item:OptionData<T>) {
            this.selectedOptionsList.forEach((optionView:ComboBoxSelectedOption) => {
                if (optionView.getItem() == item) {
                    optionView.getOptionEl().remove();
                }
            });
            this.notifySelectedOptionRemoved(item);
        }

        addListener(listener:ComboBoxSelectedOptionsViewListener<T>) {
            this.listeners.push(listener);
        }

        removeListener(listener:ComboBoxSelectedOptionsViewListener<T>) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        notifySelectedOptionRemoved(item:OptionData<T>) {
            this.listeners.forEach((listener:ComboBoxSelectedOptionsViewListener<T>) => {
                if (listener.onSelectedOptionRemoved) {
                    listener.onSelectedOptionRemoved(item);
                }
            });
        }
    }
}