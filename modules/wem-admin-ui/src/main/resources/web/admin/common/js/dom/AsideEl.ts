module api.dom {

    export class AsideEl extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("aside").setClassName(className));
        }
    }
}
