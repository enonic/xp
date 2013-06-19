module api_dom {

    export class ImgHelper extends ElementHelper {

        private el:HTMLImageElement;

        static create():ElementHelper {
            return new ImgHelper(<HTMLImageElement>document.createElement("img"));
        }

        constructor(element:HTMLImageElement) {
            super(<HTMLElement>element);
            this.el = element;
        }

        getHTMLElement():HTMLImageElement {
            return this.el;
        }

        setSrc(value:string):ImgHelper {
            this.el.src = value;
            return this;
        }
    }
}
