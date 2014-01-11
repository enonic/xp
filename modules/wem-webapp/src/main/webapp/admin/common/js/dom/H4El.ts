module api.dom {

    export class H4El extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("h4").setGenerateId(generateId).setClassName(className));
        }
    }
}
