module api.dom {

    export class DivEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("div").setClassName(className));
        }
    }
}
