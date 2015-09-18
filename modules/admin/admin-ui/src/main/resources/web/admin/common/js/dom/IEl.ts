module api.dom {

    export class IEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("i").setClassName(className));
        }
    }
}
