module api_dom {

    export class TextNode {

        private helper:api_dom.TextNodeHelper;

        constructor(text:string) {
            this.helper = api_dom.TextNodeHelper.fromString(text);
        }

        getText(): Text {
            return this.helper.getText();
        }
    }
}
