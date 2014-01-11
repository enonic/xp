module api.dom {

    export class H5El extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("h5").setGenerateId(generateId).setClassName(className));
        }
    }
}
