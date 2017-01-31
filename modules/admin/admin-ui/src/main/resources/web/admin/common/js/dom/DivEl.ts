module api.dom {

    export class DivEl extends Element {

        constructor(className?: string, prefix?: string) {
            super(new NewElementBuilder().setTagName('div').setClassName(className, prefix));
        }
    }
}
