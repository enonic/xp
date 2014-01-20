module api.dom {

    export class UlEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("ul").setClassName(className));
        }
    }
}