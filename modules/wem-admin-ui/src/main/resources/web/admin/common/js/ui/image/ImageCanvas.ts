module api.ui.image {

    import ImgEl = api.dom.ImgEl;

    export class ImageCanvas {

        private image: ImgEl;

        private width: number;

        private enabled: boolean;

        constructor(image: ImgEl) {
            this.image = image;
        }

        setWidth(width: number) {
            this.width = width;
            if (this.enabled) {
                this.updateCanvas();
            }
        }

        setEnabled(isEnabled: boolean) {
            this.enabled = isEnabled;
            isEnabled ? this.updateCanvas() : this.disableCanvas();
        }

        private updateCanvas() {
            var imageEl = this.image.getEl();
            imageEl.setWidthPx(this.width);
        }

        private disableCanvas() {
            this.image.getEl().setWidth('');
        }

    }

}