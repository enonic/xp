module api.dom {

    export class InputEl extends FormInputEl {

        constructor(className?: string, type?: string, prefix?: string, originalValue?: string) {
            super("input", className, prefix, originalValue);
            this.setType(type || 'text');

            this.onInput(this.handleInput.bind(this));
        }

        protected handleInput() {
            this.refreshDirtyState();
            this.refreshValueChanged();
        }

        getName(): string {
            return this.getEl().getAttribute('name');
        }

        setName(value: string): InputEl {
            this.getEl().setAttribute('name', value);
            return this;
        }

        getType(): string {
            return this.getEl().getAttribute('type');
        }

        setType(type: string): InputEl {
            this.getEl().setAttribute('type', type);
            return this;
        }

        setPlaceholder(value: string): InputEl {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder(): string {
            return this.getEl().getAttribute('placeholder');
        }

        getPattern(): string {
            return this.getEl().getAttribute('pattern');
        }

        setPattern(pattern: string): InputEl {
            this.getEl().setAttribute('pattern', pattern);
            return this;
        }

        reset() {
            this.getEl().setValue("");
        }

        /**
         * https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Forms_in_HTML
         * @returns {boolean}
         */
        isValid(): boolean {
            var validity: ValidityState = (<HTMLInputElement> this.getHTMLElement()).validity;
            return validity && validity.valid;
        }

        /**
         * https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/Forms_in_HTML
         * @returns {boolean}
         */
        validate(): boolean {
            return (<HTMLInputElement> this.getHTMLElement()).checkValidity();
        }

        setRequired(required: boolean): InputEl {
            if (required) {
                this.getEl().setAttribute('required', 'required');
            } else {
                this.getEl().removeAttribute('required');
            }
            return this;
        }

        isRequired(): boolean {
            return this.getEl().hasAttribute('required');
        }

        setReadOnly(readOnly: boolean) {
            super.setReadOnly(readOnly);

            if (readOnly) {
                this.getEl().setAttribute('readonly', 'readonly');
            } else {
                this.getEl().removeAttribute('readonly');
            }
        }
    }
}
