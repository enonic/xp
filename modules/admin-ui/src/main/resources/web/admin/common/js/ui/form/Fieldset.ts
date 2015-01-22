module api.ui.form {
    export class Fieldset extends api.dom.FieldsetEl {

        private legend: api.dom.LegendEl;

        private items: api.ui.form.FormItem[] = [];

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        constructor(legend?: string) {
            super();
            if (legend) {
                this.legend = new api.dom.LegendEl(legend);
                this.appendChild(this.legend);
            }
        }

        add(formItem: FormItem) {
            formItem.onFocus((event: FocusEvent) => {
                this.notifyFocused(event);
            });

            formItem.onBlur((event: FocusEvent) => {
                this.notifyBlurred(event);
            });
            this.items.push(formItem);

            this.appendChild(formItem);
        }

        validate(validationResult:ValidationResult, markInvalid?: boolean) {
            this.items.forEach((item: api.ui.form.FormItem) => {
                item.validate(validationResult, markInvalid);
            });
        }

        setFieldsetData(data: any) {
            var input, inputValue;
            this.items.forEach((item: api.ui.form.FormItem) => {
                input = item.getInput();
                inputValue = data[input.getName()];
                if (inputValue) {
                    input.setValue(inputValue);
                }
            });
        }

        getFieldsetData(): any {
            var input, data = {};
            this.items.forEach((item: api.ui.form.FormItem) => {
                input = item.getInput();
                data[input.getName()] = input.getValue();
            });
            return data;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }
    }
}