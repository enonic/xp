module api.ui{

    export class Button extends api.dom.ButtonEl {

        private labelEl:api.dom.SpanEl;

        constructor(label:string) {
            super("Button");

            this.setEnabled(true);

            this.labelEl = new api.dom.SpanEl(null, "label");
            this.labelEl.getEl().setInnerHtml(label);
            this.appendChild(this.labelEl);
        }

        setEnabled(value:boolean) {
            this.getEl().setDisabled(!value);
        }

        isEnabled() {
            return !this.getEl().isDisabled();
        }

    }
}
