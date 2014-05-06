module api.dom {

    export class OptionEl extends Element {

        constructor(value?:string, displayName?:string) {
            super(new ElementProperties().setTagName("option"));
            this.getEl().setValue(value);
            this.getEl().setInnerHtml(displayName);
        }
    }
}
