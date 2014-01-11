module api.dom {

    export class ButtonEl extends Element {

        constructor(generateId?:boolean, className?:string) {
            super(new ElementProperties().setTagName("button").setGenerateId(generateId).setClassName(className));
        }

        setText(text:string) {
            this.getEl().setInnerHtml(text);
        }

        setClickListener(f:(event) => void) {
            this.getEl().addEventListener("click", f);
        }
    }
}
