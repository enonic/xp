module api.dom {
    export class FormInputEl extends Element {

        constructor(tagName: string, className?: string, elHelper?: ElementHelper) {
            super(new ElementProperties().setTagName(tagName).setClassName(className).setHelper(elHelper));
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

        onInput(listener: (event:Event) => void) {
            this.getEl().addEventListener("input", listener);
        }

        unInput(listener: (event:Event) => void) {
            this.getEl().removeEventListener("input", listener);
        }
    }
}