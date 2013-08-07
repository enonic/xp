module api_dom {

    export class TextNodeHelper {

        private text:Text;

        static fromString(text:string):api_dom.TextNodeHelper {
            return new api_dom.TextNodeHelper(document.createTextNode(text));
        }

        constructor(text:Text) {
            this.text = text;
        }

        getText():Text {
            return this.text;
        }
    }
}
