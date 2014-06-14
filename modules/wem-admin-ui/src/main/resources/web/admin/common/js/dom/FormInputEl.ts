module api.dom {
    export class FormInputEl extends Element {

        constructor(tagName: string, className?: string) {
            super(new NewElementBuilder().
                setTagName(tagName).
                setClassName(className));
            this.addClass('form-input');
        }

        getValue(): string {
            return this.getEl().getValue();
        }

        getName(): string {
            return this.getEl().getAttribute("name");
        }

        setValue(value: string) {
            this.getEl().setValue(value);
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