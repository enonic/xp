module api.dom {

    export class PEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("p").setGenerateId(generateId).setClassName(className));
        }
    }
}
