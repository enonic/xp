module api.dom {

    export class FieldsetEl extends Element {

        constructor(className?:string) {
            super(new ElementProperties().setTagName("fieldset").setClassName(className));
        }
    }
}