module api.dom {

    export class DivEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("div").setGenerateId(generateId).setClassName(className));
        }
    }
}
