module api.dom {

    export class H3El extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("h3").setClassName(className));
        }

    }
}
