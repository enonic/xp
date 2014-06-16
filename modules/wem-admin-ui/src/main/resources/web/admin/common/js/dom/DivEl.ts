module api.dom {

    export class DivEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("div").setClassName(className));
        }
    }
}
