module api.dom {

    export class LiEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("li").setGenerateId(generateId).setClassName(className));
        }
    }
}
