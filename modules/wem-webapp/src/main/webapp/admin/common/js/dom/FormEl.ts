module api.dom {

    export class FormEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("form").setGenerateId(generateId).setClassName(className));
        }
    }
}
