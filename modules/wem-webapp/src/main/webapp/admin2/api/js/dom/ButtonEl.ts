module api_dom {

    export class ButtonEl extends Element {

        constructor(idPrefix?:string, className?:string) {
            super("button", idPrefix, className);
        }

        setText(text:string) {
            this.getEl().setInnerHtml(text);
        }

        setClickListener(f:(event) => void) {
            this.getEl().addEventListener("click", f);
        }
    }
}
