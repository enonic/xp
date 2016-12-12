module api.dom {

    export class SpanEl extends Element {

        constructor(className?: string, prefix?: string) {
            super(new NewElementBuilder().setTagName("span").setClassName(className, prefix));
        }
    }
}