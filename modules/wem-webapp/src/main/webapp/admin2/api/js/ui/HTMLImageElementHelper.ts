module api_ui {

    export class HTMLImageElementHelper extends HTMLElementHelper {

        private el:HTMLImageElement;

        static create():HTMLElementHelper {
            return new HTMLImageElementHelper(<HTMLImageElement>document.createElement("img"));
        }

        constructor(element:HTMLImageElement) {
            super(<HTMLElement>element);
            this.el = element;
        }

        getHTMLElement():HTMLImageElement {
            return this.el;
        }

        setSrc(value:string):HTMLImageElementHelper{
            this.el.src = value;
            return this;
        }
    }
}
