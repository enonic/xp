module api.dom {

    export class UlEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("ul").setClassName(className));
        }
    }
}