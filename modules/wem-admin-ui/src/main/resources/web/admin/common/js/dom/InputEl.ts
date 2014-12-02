module api.dom {

    export class InputEl extends FormInputEl {

        constructor(className?: string, type?: string) {
            super("input", className);
            this.setType(type || 'text');
        }

        getValue(): string {
            return this.getEl().getValue();
        }

        setValue(value: string): InputEl {
            this.getEl().setValue(value);
            return this;
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
    }
}
