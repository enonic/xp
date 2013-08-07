module api_ui{

    export class Button extends api_dom.ButtonEl {

        private labelEl:api_dom.LabelEl;

        constructor(label:string) {
            super("Button");

            this.setEnabled(true);

            this.labelEl = new api_dom.LabelEl(label, <api_dom.Element>this);
            this.appendChild(this.labelEl);
        }

        setEnabled(value:bool) {
            this.getEl().setDisabled(!value);
        }

        isEnabled() {
            return !this.getEl().isDisabled();
        }

    }
}
