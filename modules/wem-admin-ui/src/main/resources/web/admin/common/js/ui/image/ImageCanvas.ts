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

        private zoom: {factor: number; center: {x: number; y: number}} = {factor: 100, center: null};

        private pan: {x: number; y: number; overrideZoomCenter: boolean} = {x: 0, y: 0, overrideZoomCenter: false};

        private enabled: boolean;

        private suspendRender: boolean;

        private widthChangeListeners: {(width: number): void}[] = [];
        private zoomChangeListeners: {(zoom: number): void}[] = [];
        private panChangeListeners: {(x: number, y: number): void}[] = [];

        constructor(image: ImgEl) {
            super('image-canvas');

            this.image = image;
            this.width = image.getEl().getWidth();

            image.onLoaded(() => {
                if (this.enabled) {
                    this.renderCanvas();
                }
            });

            var prev;
            image.onMouseDown((event: MouseEvent) => {
                event.preventDefault();
                event.stopPropagation();
                prev = {x: event.clientX, y: event.clientY};
                this.addClass('dragging');
            });

            image.onMouseMove((event: MouseEvent) => {
                if (prev && this.zoom.factor > 100) {
                    this.setPan(this.pan.x + event.clientX - prev.x, this.pan.y + event.clientY - prev.y);
                    prev = {x: event.clientX, y: event.clientY};
                }
            });

            image.onMouseWheel((event: MouseEvent) => {
                if (this.enabled) {
                    var delta = (event['wheelDelta'] || -event.detail) > 0 ? 5 : -5;
                    var offset = this.getEl().getOffset();
                    this.setZoom(this.getZoom() + delta, {x: event.clientX - offset.left, y: event.clientY - offset.top});
                }
            });

            var stopDrag = (event: MouseEvent) => {
                prev = undefined;
                this.removeClass('dragging');
            };

            image.onMouseLeave(stopDrag);
            image.onMouseUp(stopDrag);
        }

        setPan(x: number, y: number) {
            console.debug('x = ' + x, 'y = ' + y);
            this.pan.x = x;
            this.pan.y = y;
            this.pan.overrideZoomCenter = x != 0 || y != 0;
            this.zoom.center = null;
            if (!this.enabled) {
                return;
            }
            console.group('setPan');

            this.renderCanvas();

            if (this.pan.x == x && this.pan.y == y) {
                // if they differ then notify has been called in renderCanvas
                this.notifyPanChanged(this.pan.x, this.pan.y);
            }
            console.groupEnd();
        }

        getPan(): {x: number; y: number} {
            return {
                x: this.pan.x,
                y: this.pan.y
            }
        }

        setWidth(width: number) {
            console.debug('width = ' + width);
            this.width = width;
            if (!this.enabled) {
                return;
            }
            console.group('setWidth');

            this.renderCanvas();

            this.notifyWidthChanged(this.width);
            console.groupEnd();
        }

        getWidth(): number {
            return this.width;
        }

        setZoom(value: number, center?: {x: number; y: number}) {
            console.debug('zoom = ' + value, 'center', center);
            this.zoom.factor = value;
            this.zoom.center = center;
            if (!this.enabled) {
                return;
            }
            console.group('setZoom');

            if (this.zoom.factor > 100 && !this.hasClass('draggable')) {
                this.addClass('draggable');
            } else if (this.zoom.factor == 100) {
                // reset the pan override when it is turned off
                this.pan.overrideZoomCenter = false;
                this.zoom.center = null;

                if (this.hasClass('draggable')) {
                    this.removeClass('draggable');
                }
            }

            this.renderCanvas();

            if (this.zoom.factor == value) {
                // if they differ then notify has been called in renderCanvas
                this.notifyZoomChanged(this.zoom.factor);
            }
            console.groupEnd();
        }

        getZoom(): number {
            return this.zoom.factor;
        }

        setEnabled(isEnabled: boolean) {
            this.enabled = isEnabled;
            if (isEnabled) {
                this.insertAfterEl(this.image);
                this.image.unregisterFromParentElement(true);
                this.appendChild(this.image);

                this.enableCanvas();
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

        private renderCanvas() {
            console.debug('width', this.width, '\nzoom', this.zoom, '\npan', this.pan);
            if (this.suspendRender) {
                return false;
            }
            console.group('renderCanvas');

            var imgEl = this.image.getEl();
            var oldImgWidth = imgEl.getWidth(),
                oldImgHeight = imgEl.getHeight();

            var oldZoom = 100 * oldImgWidth / this.width;

            var zoom = Math.min(Math.max(this.zoom.factor, 100), 1000);
            console.debug('zoom after restraining', zoom);
            imgEl.setWidthPx(this.width * (zoom / 100));

            var imgHeight = imgEl.getHeight(),
                imgWidth = imgEl.getWidth();
            console.debug('imgWidth', imgWidth, '\nimgHeight', imgHeight);

            var panX = this.pan.x,
                panY = this.pan.y;

            var canvasWidth = this.width,
                canvasHeight = Math.round(this.width * imgHeight / imgWidth);

            this.getEl().setWidthPx(canvasWidth).setHeightPx(canvasHeight);
            console.debug('canvasWidth', canvasWidth, '\ncanvasHeight', canvasHeight);

            if (!this.zoom.center && !this.pan.overrideZoomCenter) {
                // zoom to the center by default
                this.zoom.center = {
                    x: canvasWidth / 2,
                    y: canvasHeight / 2
                };
                console.debug('setting default zoom center to', this.zoom.center);
            }

            if (this.zoom.center) {
                var zoomWidthPanFactor = (this.zoom.center.x + Math.abs(panX)) / oldImgWidth;
                var zoomHeightPanFactor = (this.zoom.center.y + Math.abs(panY)) / oldImgHeight;

                panX = -imgWidth * zoomWidthPanFactor + this.zoom.center.x;
                panY = -imgHeight * zoomHeightPanFactor + this.zoom.center.y;
                console.debug('based on zoom center, pan x = ', panX, 'pan y = ', panY);
            }

            // restrain pan to image or canvas whatever is larger
            if (imgWidth > canvasWidth) {
                panX = Math.max(Math.min(panX, 0), canvasWidth - imgWidth);
            } else {
                panX = Math.min(Math.max(panX, 0), canvasWidth - imgWidth);
            }
            if (imgHeight > canvasHeight) {
                panY = Math.max(Math.min(panY, 0), canvasHeight - imgHeight);
            } else {
                panY = Math.min(Math.max(panY, 0), canvasHeight - imgHeight);
            }
            console.debug('after restraining, pan x = ' + panX, 'pan y = ' + panY);

            imgEl.setMarginLeft(panX + 'px').setMarginTop(panY + 'px');

            if (panX != this.pan.x || panY != this.pan.y) {
                this.pan.x = panX;
                this.pan.y = panY;
                this.notifyPanChanged(panX, panY);
            }
            if (zoom != this.zoom.factor) {
                this.zoom.factor = zoom;
                this.notifyZoomChanged(zoom);
            }
            console.groupEnd();
        }

        private disableCanvas() {
            this.image.getEl().setWidth('').setMarginLeft('').setMarginTop('');
        }

        private enableCanvas() {
            this.suspendRender = true;
            this.setWidth(this.width);
            this.setZoom(this.zoom.factor);
            this.setPan(this.pan.x, this.pan.y);
            this.suspendRender = false;
            this.renderCanvas();
        }

        onWidthChanged(listener: (width: number) => void) {
            this.widthChangeListeners.push(listener);
        }

        unWidthChanged(listener: (width: number) => void) {
            this.widthChangeListeners = this.widthChangeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyWidthChanged(width: number) {
            console.debug('notifyWidthChanged', width);
            this.widthChangeListeners.forEach((listener) => {
                listener(width);
            });
        }

        onZoomChanged(listener: (zoom: number) => void) {
            this.zoomChangeListeners.push(listener);
        }

        unZoomChanged(listener: (zoom: number) => void) {
            this.zoomChangeListeners = this.zoomChangeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyZoomChanged(zoom: number) {
            console.debug('notifyZoomChanged', zoom);
            this.zoomChangeListeners.forEach((listener) => {
                listener(zoom);
            });
        }

        onPanChanged(listener: (x: number, y: number) => void) {
            this.panChangeListeners.push(listener);
        }

        unPanChanged(listener: (x: number, y: number) => void) {
            this.panChangeListeners = this.panChangeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyPanChanged(x: number, y: number) {
            console.debug('notifyPanChanged', x, y);
            this.panChangeListeners.forEach((listener) => {
                listener(x, y);
            });
        }
    }

}