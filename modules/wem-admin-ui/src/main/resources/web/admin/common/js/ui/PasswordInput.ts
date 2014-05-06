module api.ui {

    export class PasswordInput extends api.dom.InputEl {

        constructor(className?:string) {
            super(className);

            this.getEl().setAttribute('type', 'password');
        }

        setPlaceholder(value:string):PasswordInput {
            this.getEl().setAttribute('placeholder', value);
            return this;
        }

        getPlaceholder():string {
            return this.getEl().getAttribute('placeholder');
        }
    }
}