module api_dom {

    export class InputEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("input", idPrefix, className);
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

        setPlaceholder(value:string):InputEl {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder():string {
            return this.getEl().getAttribute('placeholder');
        }
    }
}
