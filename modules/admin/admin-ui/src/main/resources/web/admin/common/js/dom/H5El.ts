module api.dom {

    export class H5El extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("h5").setClassName(className));
        }
    }
}
