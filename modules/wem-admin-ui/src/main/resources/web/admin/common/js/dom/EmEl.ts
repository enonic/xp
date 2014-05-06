module api.dom {

    export class EmEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("em").setClassName(className));
        }
    }
}
