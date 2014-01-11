module api.dom {

    export class H6El extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("h6").setGenerateId(generateId).setClassName(className));
        }
    }
}
