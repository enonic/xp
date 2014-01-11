module api.dom {

    export class UlEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("ul").setGenerateId(generateId).setClassName(className));
        }
    }
}
