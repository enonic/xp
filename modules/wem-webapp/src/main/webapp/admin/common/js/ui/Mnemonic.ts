module api_ui {

    export class Mnemonic {

        private value:string;

        constructor(value:string) {

            this.value = value;
        }

        getValue():string {
            return this.value;
        }

        toKeyBinding(callback?:(e:ExtendedKeyboardEvent, combo:string) => any):KeyBinding {
            return new KeyBinding("alt+" + this.getValue(), callback);
        }

        underlineMnemonic(text:string):Node[] {

            var nodes:Node[] = [];
            var mStart:number = text.indexOf(this.value);
            if (mStart == -1) {
                mStart = text.indexOf(this.value.toLowerCase());
                if (mStart == -1) {
                    mStart = text.indexOf(this.value.toUpperCase());
                }
            }

            if (mStart == 0) {
                var underlineEl = new api_dom.Element("u");
                nodes.push(underlineEl.getHTMLElement());
                underlineEl.getEl().appendChild(document.createTextNode(text.charAt(0)));
                nodes.push(document.createTextNode(text.substr(1, text.length)));
            }
            else {
                nodes.push(document.createTextNode(text.substr(0, mStart)));
                var underlineEl = new api_dom.Element("u");
                nodes.push(underlineEl.getHTMLElement());
                underlineEl.getEl().appendChild(document.createTextNode(text.charAt(mStart)));
                nodes.push(document.createTextNode(text.substr(mStart + 1, text.length)));
            }

            return nodes;
        }
    }
}
