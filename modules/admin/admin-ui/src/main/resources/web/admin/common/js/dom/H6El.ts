module api.dom {

    export class H6El extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("h6").setClassName(className));
        }

    }
}
