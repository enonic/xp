module api.dom {

    export class IEl extends Element {

        constructor(className?: string) {
            super(new ElementProperties().setTagName("i").setClassName(className));
        }
    }
}
