module api.dom {

    export class EmEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("em").setGenerateId(generateId).setClassName(className));
        }
    }
}
