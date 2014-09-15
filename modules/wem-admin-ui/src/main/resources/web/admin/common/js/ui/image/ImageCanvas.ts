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

    export class ImageCanvas {

        private image: ImgEl;

        private originalStyles: ImageStyles;

        private width: number;

        private zoom: number;

        private enabled: boolean;

        constructor(image: ImgEl) {
            this.image = image;

            var imageEl = image.getEl();
            this.originalStyles = {
                width: imageEl.getWidth(),
                height: imageEl.getHeight(),
                paddingTop: imageEl.getPaddingTop(),
                paddingRight: imageEl.getPaddingRight(),
                paddingBottom: imageEl.getPaddingBottom(),
                paddingLeft: imageEl.getPaddingLeft()
            };

            this.width = this.originalStyles.width;
            this.zoom = 100;
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
            isEnabled ? this.updateCanvas() : this.disableCanvas();
        }

        private updateCanvas() {
            var imageEl = this.image.getEl();
            imageEl.setWidthPx(this.width);
            this.updateZoom();
        }

        private disableCanvas() {
            this.image.getEl().
                setWidthPx(this.originalStyles.width).
                setHeightPx(this.originalStyles.height).
                setPaddingTop(this.originalStyles.paddingTop + 'px').
                setPaddingRight(this.originalStyles.paddingRight + 'px').
                setPaddingBottom(this.originalStyles.paddingBottom + 'px').
                setPaddingLeft(this.originalStyles.paddingLeft + 'px');
        }

        private updateZoom() {
            if (this.width < this.originalStyles.width) {
                return;
            }

            var zoomedWidth = Math.min(this.originalStyles.width * (this.zoom / 100), this.width);
            var horizontalPadding = (this.width - zoomedWidth) / 2;
            var height = this.originalStyles.height * (this.width / this.originalStyles.width);
            var zoomedHeight = Math.min(this.originalStyles.height * (this.zoom / 100), height);
            var verticalPadding =  (height - zoomedHeight) / 2;

            this.image.getEl().
                setPaddingLeft(horizontalPadding + this.originalStyles.paddingLeft + 'px').
                setPaddingRight(horizontalPadding + this.originalStyles.paddingRight + 'px').
                setPaddingTop(verticalPadding + this.originalStyles.paddingTop + 'px').
                setPaddingBottom(verticalPadding + this.originalStyles.paddingBottom + 'px');
        }
    }

}