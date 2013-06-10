module api_ui {

    export class ImgHelper extends api_ui.ElementHelper {

        private el:HTMLImageElement;

        static create():ElementHelper {
            return new api_ui.ImgHelper(<HTMLImageElement>document.createElement("img"));
        }

        constructor(element:HTMLImageElement) {
            super(<HTMLElement>element);
            this.el = element;
        }

        getHTMLElement():HTMLImageElement {
            return this.el;
        }

        setSrc(value:string):api_ui.ImgHelper{
            this.el.src = value;
            return this;
        }
    }
}
