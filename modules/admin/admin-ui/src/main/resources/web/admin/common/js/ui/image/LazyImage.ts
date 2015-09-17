module api.ui.image {

    export class LazyImage extends api.dom.DivEl {

        private phantomImage: api.dom.ImgEl;

        constructor(src?: string) {
            super("lazy-image");

            this.addClass("empty");

            this.phantomImage = new api.dom.ImgEl(null, "phantom-image");

            this.phantomImage.onLoaded(() => {
                this.getEl().setBackgroundImage("url(" + this.phantomImage.getSrc() + ")");
                this.removeClass("empty");
            });

            this.setSrc(src);
        }

        setSrc(src: string) {
            if (!this.hasClass("empty")) {
                this.addClass("empty");
            }

            this.phantomImage.setSrc(src);
        }
    }
}
