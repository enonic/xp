module api_ui{

    export class Button extends api_dom.ButtonEl {

        private labelEl:api_dom.SpanEl;

        constructor(label:string) {
            super("Button");

            this.setEnabled(true);

            this.labelEl = new api_dom.SpanEl(null, "label");
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
