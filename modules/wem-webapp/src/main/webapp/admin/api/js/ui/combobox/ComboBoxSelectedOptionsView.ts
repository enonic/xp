module api_ui_combobox {

    export class ComboBoxSelectedOptionsView<T> extends api_dom.DivEl implements api_event.Observable {

        private listeners:ComboBoxSelectedOptionsViewListener<T>[] = [];

        constructor() {
            super(null, "selected-options");
        }

        addItem(item:OptionData<T>) {
            var option = new api_dom.DivEl(null, 'selected-option');
            var removeButton = new api_dom.AEl(null, "remove");
            var optionValue = new api_dom.DivEl(null, 'option-value');

            option.appendChild(removeButton);
            option.appendChild(optionValue);
            optionValue.getEl().setInnerHtml(item.displayValue.toString());

            this.appendChild(option);

            removeButton.getEl().addEventListener('click', (event:Event) => {
                option.remove();
                this.notifySelectedOptionRemoved(item);

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
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