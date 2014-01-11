module api.dom {

    export class H2El extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("h2").setGenerateId(generateId).setClassName(className));
        }
    }
}
