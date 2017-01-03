module api.app.wizard {

    export class FormIcon extends api.dom.ButtonEl {

        private img: api.dom.ImgEl;

        constructor(iconUrl: string, className?: string) {
            super("form-icon" + (className ? " " + className : ""));
            let el = this.getEl();

            this.img = new api.dom.ImgEl(iconUrl);

            el.appendChild(this.img.getHTMLElement());
        }

        setSrc(src: string) {
            this.img.setSrc(src);
        }
    }

}
