module api.dom {

    export class H1El extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("h1").setClassName(className));
        }

    }
}
