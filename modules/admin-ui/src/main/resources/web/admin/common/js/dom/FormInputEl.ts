module api.dom {
    export class FormInputEl extends Element {

        constructor(tagName: string, className?: string) {
            super(new NewElementBuilder().
                setTagName(tagName).
                setClassName(className));
            this.addClass('form-input');
        }

        getName(): string {
            return this.getEl().getAttribute("name");
        }

        setName(name: string): FormInputEl {
            this.getEl().setAttribute("name", name);
            return this;
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