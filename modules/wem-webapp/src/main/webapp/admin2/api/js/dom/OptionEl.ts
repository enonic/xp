module api_dom {

    export class OptionEl extends Element {

        constructor(value?:string, displayName?:string) {
            super("option");
            this.getEl().setValue(value);
            this.getEl().setInnerHtml(displayName);
        }
    }
}
