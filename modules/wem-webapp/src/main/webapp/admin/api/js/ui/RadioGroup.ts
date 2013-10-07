module api_ui {

    export class RadioGroup extends api_dom.FormInputEl {

        public static ORIENTATION_VERTICAL = "vertical";
        public static ORIENTATION_HORIZONTAL = "horizontal";

        private name:string;
        private options:RadioButton[] = [];

        constructor(name:string, orientation?:string) {
            super("div", "RadioGroup", "radio-group");
            this.name = name;
            if (RadioGroup.ORIENTATION_VERTICAL == orientation) {
                this.addClass("vertical");
            }
        }

        public addOption(value:string, label:string, checked?:boolean) {
            var radio = new RadioButton(label, value, this.name, checked);
            this.options.push(radio);
            this.appendChild(radio);
        }

        setValue(value:string):void {
            var option;
            for (var i = 0; i < this.options.length; i++) {
                option = this.options[i];
                option.setChecked(option.getValue() == value);
            }
        }

        getValue():string {
            var option;
            for (var i = 0; i < this.options.length; i++) {
                option = this.options[i];
                if (option.isChecked()) {
                    return option.getValue();
                }
            }
            return undefined;
        }

        getName():string {
            return this.name;
        }
    }


    export class RadioButton extends api_dom.FormInputEl {

        private radio:api_dom.InputEl;
        private label:api_dom.LabelEl;

        constructor(label:string, value:string, name:string, checked?:boolean) {
            super("span", "RadioButton", "radio-button");

            this.radio = new api_dom.InputEl();
            this.radio.getEl().setAttribute('type', 'radio');
            this.radio.setName(name).setValue(value);
            this.appendChild(this.radio);

            this.label = new api_dom.LabelEl(label, this.radio);
            this.appendChild(this.label);

            this.setChecked(checked);
        }

        setValue(value:string):void {
            this.radio.setValue(value);
        }

        getValue():string {
            return this.radio.getValue();
        }

        getName():string {
            return this.radio.getName();
        }

        public isChecked():boolean {
            return this.radio.getHTMLElement()['checked'];
        }

        public setChecked(checked:boolean = false):RadioButton {
            this.radio.getHTMLElement()['checked'] = checked;
            return this;
        }

    }
}