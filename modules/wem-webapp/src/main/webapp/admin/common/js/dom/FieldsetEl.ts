module api.dom {

    export class FieldsetEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("fieldset").setGenerateId(generateId).setClassName(className));
        }
    }
}