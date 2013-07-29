module api_ui {

    export enum TextAreaSize {
        LARGE,
        MEDIUM,
        SMALL
    }

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

        setSize(size:TextAreaSize) {
            var sizeClass;
            switch (size) {
            case TextAreaSize.LARGE:
                sizeClass = "large";
                break;
            case TextAreaSize.MEDIUM:
                sizeClass = "medium";
                break;
            case TextAreaSize.SMALL:
                sizeClass = "small";
                break;
            default:
                break;
            }
            this.addClass(sizeClass);
        }
    }

}