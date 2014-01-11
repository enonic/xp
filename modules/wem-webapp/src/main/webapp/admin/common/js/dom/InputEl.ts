module api.dom {

    export class InputEl extends FormInputEl {

        constructor(generateId?:boolean, className?:string, type?:string) {
            super("input", generateId, className);
            this.getHTMLElement().setAttribute('type', type || 'text');
        }

        setValue(value:string):InputEl {
            this.getEl().setValue(value);
            return this;
        }

        getValue():string {
            return this.getEl().getValue();
        }

        setName(value:string):InputEl {
            this.getEl().setAttribute('name', value);
            return this;
        }

        getName():string {
            return this.getEl().getAttribute('name');
        }
    }
}
