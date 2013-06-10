module api_ui {

    export class AbstractEl {

        private static constructorCounter:number = 0;

        private el:HTMLElementHelper;

        private id:string;

        constructor(elementName:string, name:string) {
            if (elementName === "img") {
                this.el = HTMLImageElementHelper.create();
            }
            else {
                this.el = HTMLElementHelper.fromName(elementName);
            }
            this.id = name + '-' + (++api_ui.AbstractEl.constructorCounter);
            this.el.setId(this.id);
        }

        getId():string {
            return this.id;
        }

        getEl():HTMLElementHelper {
            return this.el;
        }

        getImg():HTMLImageElementHelper {
            return <HTMLImageElementHelper>this.el;
        }

        getHTMLElement():HTMLElement {
            return this.el.getHTMLElement();
        }

        appendChild(child:api_ui.AbstractEl) {
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
