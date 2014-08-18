module api.dom {

    export class SpanEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("span").setClassName(className));
        }
    }
}