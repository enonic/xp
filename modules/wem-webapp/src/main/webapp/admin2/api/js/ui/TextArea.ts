module api_ui {
    export class TextArea extends api_dom.Element {

        constructor(name:string) {
            super("textarea");
            this.getEl().setAttribute("name", name);
        }

        setText(text:string) {
            this.getEl().setValue(text);
        }

        getText():string {
            return this.getEl().getValue();
        }
    }

}