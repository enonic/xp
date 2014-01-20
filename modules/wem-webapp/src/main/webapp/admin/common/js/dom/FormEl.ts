module api.dom {

    export class FormEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("form").setClassName(className));
        }
    }
}
