module api.dom {

    export class H2El extends Element {

        constructor(className?: string) {
            super(new NewElementBuilder().setTagName("h2").setClassName(className));
        }

        setText(value: string): H2El {
            this.getEl().setInnerHtml(value);
            return this;
        }
    }
}
