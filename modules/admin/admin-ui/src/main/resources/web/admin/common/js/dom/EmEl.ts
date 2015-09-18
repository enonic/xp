module api.dom {

    export class EmEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("em").setClassName(className));
        }
    }
}
