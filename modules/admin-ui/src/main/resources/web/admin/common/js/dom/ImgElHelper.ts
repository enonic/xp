module api.dom {

    export class ImgHelper extends ElementHelper {

        private imgEl: HTMLImageElement;

        static create(): ElementHelper {
            return new ImgHelper(<HTMLImageElement>document.createElement("img"));
        }

        constructor(element: HTMLImageElement) {
            super(<HTMLElement>element);
            this.imgEl = element;
        }

        getHTMLElement(): HTMLImageElement {
            return this.imgEl;
        }

        setSrc(value: string): ImgHelper {
            this.imgEl.src = value;
            return this;
        }

        getSrc(): string {
            return this.imgEl.src;
        }

        getNaturalWidth() {
            return this.imgEl.naturalWidth;
        }

        getNaturalHeight() {
            return this.imgEl.naturalHeight;
        }
    }
}
