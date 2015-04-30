module api.ui.button {

    export class Button extends api.dom.ButtonEl {

        private labelEl: api.dom.SpanEl;

        constructor(label?: string) {
            super("button");

            this.labelEl = new api.dom.SpanEl();
            if (label) {
                this.labelEl.getEl().setInnerHtml(label);
            }
            this.appendChild(this.labelEl);
        }

        setEnabled(value: boolean) {
            this.getEl().setDisabled(!value);
        }

        isEnabled() {
            return !this.getEl().isDisabled();
        }

        setLabel(label: string) {
            this.labelEl.getEl().setInnerHtml(label);
        }

        getLabel(): string {
            return this.labelEl.getEl().getInnerHtml();
        }
    }
}
