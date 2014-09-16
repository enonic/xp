module api.ui.image {

    import ImgEl = api.dom.ImgEl;

    interface ImageStyles {
        width: number;
        height: number;
        paddingTop: number;
        paddingRight: number;
        paddingBottom: number;
        paddingLeft: number;
    }

    export class ImageCanvas extends api.dom.DivEl {

        private image: ImgEl;

        private width: number;

        private zoom: number;

        private enabled: boolean;

        constructor(image: ImgEl) {
            super('image-canvas');

            this.image = image;
            this.width = image.getEl().getWidth();
            this.zoom = 100;

            image.onLoaded(() => {
                if (this.enabled) {
                    this.updateCanvas();
                }
            });
        }

        setWidth(width: number) {
            this.width = width;
            if (this.enabled) {
                this.updateCanvas();
            }
        }

        setZoom(value: number) {
            this.zoom = value;
            if (this.enabled) {
                this.updateCanvas();
            }
        }

        setEnabled(isEnabled: boolean) {
            this.enabled = isEnabled;
            if (isEnabled) {
                this.insertAfterEl(this.image);
                this.image.unregisterFromParentElement(true);
                this.appendChild(this.image);

                this.updateCanvas();
            } else {
                this.image.unregisterFromParentElement(true);
                this.image.insertBeforeEl(this);
                this.unregisterFromParentElement(true);
                this.getEl().remove();

                this.disableCanvas();
            }
        }

        isEnabled(): boolean {
            return this.enabled;
        }

        private updateCanvas() {
            this.updateWidth();
            this.updateZoom();
        }

        private updateWidth() {
            this.image.getEl().setWidthPx(this.width);
            this.getEl().
                setWidthPx(this.width).
                setHeightPx(this.image.getEl().getHeight());
        }

        private updateZoom() {
            this.image.getEl().
                setWidthPx(this.width * (this.zoom / 100)).
                setMarginLeft(-(this.width * ((this.zoom / 100) - 1) / 2) + 'px').
                setMarginTop(-(this.getEl().getHeight() * ((this.zoom / 100) - 1) / 2) + 'px');
        }

        private disableCanvas() {
            this.image.getEl().setWidth('').setMarginLeft('').setMarginTop('');
        }
    }

}