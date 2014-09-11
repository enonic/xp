module api.dom {

    export class SelectEl extends FormInputEl {

        constructor(className?:string) {
            super("select", className);
        }

        setName(value:string):SelectEl {
            this.getEl().setAttribute('name', value);
            return this;
        }
    }
}
