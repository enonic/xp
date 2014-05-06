module api.dom {

    export class LiEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("li").setClassName(className));
        }
    }
}
