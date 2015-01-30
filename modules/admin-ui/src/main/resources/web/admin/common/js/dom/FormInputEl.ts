module api.dom {
    export class FormInputEl extends FormItemEl {

        constructor(tagName: string, className?: string) {
            super(tagName, className);
            this.addClass('form-input');
        }

        getValue(): string {
            return this.getEl().getValue();
        }

        setValue(value: string): FormInputEl {
            this.getEl().setValue(value);
            return this;
        }

        onChange(listener: (event: Event) => void) {
            this.getEl().addEventListener("change", listener);
        }

        unChange(listener: (event: Event) => void) {
            this.getEl().removeEventListener("change", listener);
        }

        onInput(listener: (event: Event) => void) {
            this.getEl().addEventListener("input", listener);
        }

        unInput(listener: (event: Event) => void) {
            this.getEl().removeEventListener("input", listener);
        }
    }
}