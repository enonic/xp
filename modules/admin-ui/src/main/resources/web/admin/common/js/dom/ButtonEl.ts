module api.dom {

    export class ButtonEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("button").setClassName(className));
        }

    }
}
