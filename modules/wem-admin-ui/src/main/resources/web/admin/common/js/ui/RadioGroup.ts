module api.ui {

    export class RadioGroup extends api.dom.FormInputEl {

        public static ORIENTATION_VERTICAL = "vertical";
        public static ORIENTATION_HORIZONTAL = "horizontal";

        private name: string;
        private options: RadioButton[] = [];

        private oldValue: string = "";

        private valueChangedListeners: {(event: ValueChangedEvent):void}[] = [];

        constructor(name: string, orientation?: string) {
            super("div", "radio-group");
            this.name = name;
            if (RadioGroup.ORIENTATION_VERTICAL == orientation) {
                this.addClass("vertical");
            }

        }

        public addOption(value: string, label: string, checked?: boolean) {
            var radio = new RadioButton(label, value, this.name, checked);
            this.options.push(radio);
            this.appendChild(radio);
            radio.onClicked((event: MouseEvent) => {
                this.notifyValueChanged(this.oldValue, this.getValue());
                this.oldValue = this.getValue();
            });
        }

        setValue(value: string): RadioGroup {
            var option;
            for (var i = 0; i < this.options.length; i++) {
                option = this.options[i];
                option.setChecked(option.getValue() == value);
            }
            return this;
        }

        getValue(): string {
            var option;
            for (var i = 0; i < this.options.length; i++) {
                option = this.options[i];
                if (option.isChecked()) {
                    return option.getValue();
                }
            }
            return undefined;
        }

        getName(): string {
            return this.name;
        }

        onValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((currentListener: (event: ValueChangedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyValueChanged(oldValue: string, newValue: string) {
            this.valueChangedListeners.forEach((listener: (event: ValueChangedEvent)=>void)=> {
                listener.call(this, new ValueChangedEvent(oldValue, newValue));
            })
        }
    }


    export class RadioButton extends api.dom.FormInputEl {

        private radio: api.dom.InputEl;
        private label: api.dom.LabelEl;

        constructor(label: string, value: string, name: string, checked?: boolean) {
            super("span", "radio-button");

            this.radio = new api.dom.InputEl();
            this.radio.getEl().setAttribute('type', 'radio');
            this.radio.setName(name).setValue(value);
            this.appendChild(this.radio);

            this.label = new api.dom.LabelEl(label, this.radio);
            this.appendChild(this.label);

            this.setChecked(checked);
        }

        setValue(value: string): RadioButton {
            this.radio.setValue(value);
            return this;
        }

        getValue(): string {
            return this.radio.getValue();
        }

        getName(): string {
            return this.radio.getName();
        }

        public isChecked(): boolean {
            return this.radio.getHTMLElement()['checked'];
        }

        public setChecked(checked: boolean = false): RadioButton {
            this.radio.getHTMLElement()['checked'] = checked;
            return this;
        }

    }
}