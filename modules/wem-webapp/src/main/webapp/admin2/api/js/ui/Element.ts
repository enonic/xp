module api_ui {

    export class Element {

        private static constructorCounter:number = 0;

        private el:api_ui.ElementHelper;

        private id:string;

        constructor(elementName:string, idPrefix?:string, className?:string, elHelper?:api_ui.ElementHelper) {
            if (elHelper == null) {
                this.el = api_ui.ElementHelper.fromName(elementName);
            } else {
                this.el = elHelper;
            }
            if (idPrefix != null) {
                this.id = idPrefix + '-' + (++api_ui.Element.constructorCounter);
                this.el.setId(this.id);
            }
            if (className != null) {
                this.getHTMLElement().className = className;
            }
        }

        show() {
            // Using jQuery to show, since it seems to contain some smartness
            jQuery(this.el.getHTMLElement()).show();
        }

        hide() {
            // Using jQuery to hide, since it seems to contain some smartness
            jQuery(this.el.getHTMLElement()).hide();
        }

        empty() {
            this.el.setInnerHtml("");
        }

        getId():string {
            return this.id;
        }

        getEl():api_ui.ElementHelper {
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
