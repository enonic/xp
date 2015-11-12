module api.dom {

    export class DivEl extends Element {

        constructor(className?: string, usePrefix?: boolean) {
            super(new NewElementBuilder().setTagName("div").setClassName(className, usePrefix));
        }
    }
}
