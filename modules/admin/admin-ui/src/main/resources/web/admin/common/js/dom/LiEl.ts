module api.dom {

    export class LiEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("li").setClassName(className));
        }
    }
}
