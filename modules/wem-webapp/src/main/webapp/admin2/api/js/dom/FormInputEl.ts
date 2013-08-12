module api_dom {
    export class FormInputEl extends Element implements api_ui.FormInput {

        constructor(elementName:string, idPrefix?:string, className?:string, elHelper?:ElementHelper) {
            super(elementName, idPrefix, className, elHelper);
        }

        getValue():string {
            return this.getEl().getValue();
        }

        getName():string {
            return this.getEl().getAttribute("name");
        }
    }
}