module api.dom {

    export class H5El extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("h5").setClassName(className));
        }
    }
}
