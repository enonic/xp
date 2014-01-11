module api.dom {
    export class FormInputEl extends Element {

        constructor(tagName:string, generateId?:boolean, className?:string, elHelper?:ElementHelper) {
            super(new ElementProperties().setTagName(tagName).setGenerateId(generateId).setClassName(className).setHelper(elHelper));
        }

        getValue():string {
            return this.getEl().getValue();
        }

        getName():string {
            return this.getEl().getAttribute("name");
        }

        setValue(value:string) {
            this.getEl().setValue(value);
        }
    }
}