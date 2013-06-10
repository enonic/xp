module api_ui {

    export class Element {

        private static constructorCounter:number = 0;

        private el:HTMLElementHelper;

        private id:string;

        constructor(elementName:string, name?:string, elHelper?:HTMLElementHelper) {
            if (elHelper == null) {
                this.el = HTMLElementHelper.fromName(elementName);
            }
            else {
                this.el = elHelper;
            }
            if (name != null) {
                this.id = name + '-' + (++api_ui.Element.constructorCounter);
                this.el.setId(this.id);
            }
        }

        getId():string {
            return this.id;
        }

        getEl():HTMLElementHelper {
            return this.el;
        }

        getHTMLElement():HTMLElement {
            return this.el.getHTMLElement();
        }

        appendChild(child:api_ui.Element) {
            this.el.appendChild(child.getEl().getHTMLElement());
        }

        removeChildren() {
            var htmlEl = this.el.getHTMLElement();
            while (htmlEl.firstChild) {
                htmlEl.removeChild(htmlEl.firstChild);
            }
        }
    }
}
