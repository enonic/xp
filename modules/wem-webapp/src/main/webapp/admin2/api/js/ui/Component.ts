module api_ui {

    export class Component {

        private static constructorCounter:number = 0;

        private el:HTMLElementHelper;

        private id:string;

        constructor(name:string, elementName:string) {
            this.el = HTMLElementHelper.fromName(elementName);
            this.id = name + '-' + (++Component.constructorCounter);
            this.el.setId(this.id);
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

        appendChild(child:api_ui.Component) {
            this.el.appendChild(child.getEl().getHTMLElement());
        }

    }
}
