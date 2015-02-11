module api.ui.image {

    import ImgEl = api.dom.ImgEl;

    export class ImageCanvas extends api.dom.DivEl {

        private image: ImgEl;

        private zoom: {x: number; y: number; factor: number; previous: number} = {x: -1, y: -1, factor: 1, previous: 1};

        private pan: {x: number; y: number; overrideZoom: boolean} = {x: 0, y: 0, overrideZoom: false};

        private enabled: boolean;

        private suspendRender: boolean;
        private imageRatio: number;
        private canvasWidth: number;
        private canvasHeight: number;

        private zoomChangeListeners: {(zoom: number): void}[] = [];
        private panChangeListeners: {(x: number, y: number): void}[] = [];

        constructor(image: ImgEl) {
            super('image-canvas');

            this.image = image;

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {
                console.group('on size changed');
                if (this.enabled) {
                    this.recalculateHeight(true, false);
                    this.renderCanvas();
                }
                console.groupEnd();
            });

            image.onLoaded((event: UIEvent) => {
                console.group('on image loaded');
                if (this.enabled) {
                    this.recalculateHeight(false, true);
                    this.renderCanvas();
                }
                console.groupEnd();
            });

            var prev;
            image.onMouseDown((event: MouseEvent) => {
                if (this.enabled) {
                    event.preventDefault();
                    event.stopPropagation();
                    prev = {x: event.clientX, y: event.clientY};
                    this.addClass('dragging');
                }
            });

            image.onMouseMove((event: MouseEvent) => {
                if (this.enabled && prev && this.zoom.factor > 1) {
                    // divide by canvasWidth because % are calculated from width
                    this.setPan(this.pan.x + (event.clientX - prev.x) / this.canvasWidth,
                            this.pan.y + (event.clientY - prev.y) / this.canvasWidth);
                    prev = {x: event.clientX, y: event.clientY};
                }
            });

            image.onMouseWheel((event: MouseEvent) => {
                if (this.enabled) {
                    var delta = (event['wheelDelta'] || -event.detail) > 0 ? 0.05 : -0.05;
                    var offset = this.getEl().getOffset();
                    // divide by canvasWidth because % are calculated from width
                    this.setZoom(this.getZoom() + delta, true, (event.clientX - offset.left) / this.canvasWidth,
                            (event.clientY - offset.top) / this.canvasWidth);
                }
            });

            var stopDrag = (event: MouseEvent) => {
                prev = undefined;
                this.removeClass('dragging');
            };

            image.onMouseLeave(stopDrag);
            image.onMouseUp(stopDrag);
        }

        setPan(x: number, y: number, override: boolean = true) {

            this.pan.x = x;
            this.pan.y = y;
            if (override) {
                this.pan.overrideZoom = true;
            }

            if (!this.enabled) {
                return;
            }
            console.group('setPan');
            console.debug('x = ' + x, 'y = ' + y);

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

        setZoom(value: number, override: boolean = true, x?: number, y?: number) {
            if (this.zoom.factor != value) {
                this.zoom.previous = this.zoom.factor;
                this.zoom.factor = +value.toFixed(3);
            }
            if (x) {
                this.zoom.x = x;
            }
            if (y) {
                this.zoom.y = y;
            }
            if (override) {
                this.pan.overrideZoom = false;
            }

            if (!this.enabled) {
                return;
            }
            console.group('setZoom');
            console.debug('zoom = ' + value, 'x = ', x, 'y = ', y);

            if (this.zoom.factor > 1) {
                // zoom to the center by default
                if (this.zoom.x < 0 && this.zoom.y < 0) {
                    this.zoom.x = 0.5;
                    // height percents are calculated from width in html
                    this.zoom.y = 0.5 * this.canvasHeight / this.canvasWidth;
                    console.debug('setting default zoom center to (' + this.zoom.x + ', ' + this.zoom.y + ')');
                }

                if (!this.hasClass('draggable')) {
                    this.addClass('draggable');
                }
            } else if (this.zoom.factor == 1) {
                // reset the pan override when zoom is turned off
                this.pan.overrideZoom = false;

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
                this.image.replaceWith(this);
                this.appendChild(this.image);

                this.enableCanvas();
            } else {
                this.removeChild(this.image);
                this.replaceWith(this.image);

                this.disableCanvas();
            }
        }

        isEnabled(): boolean {
            return this.enabled;
        }

        private renderCanvas() {
            if (this.suspendRender || !this.enabled) {
                return;
            }
            console.group('renderCanvas');
            console.debug('zoom', this.zoom, '\npan', this.pan);
            var imgEl = this.image.getEl();

            console.debug('canvas (' + this.canvasWidth + ', ' + this.canvasHeight + ')');

            var oldZoomFactor = this.zoom.previous,
                zoomFactor = Math.min(Math.max(this.zoom.factor, 1), 10);

            console.debug('old zoom factor = ' + oldZoomFactor + ', \nnew zoom factor after restraining = ' + zoomFactor);

            imgEl.setWidthPx(this.canvasWidth * zoomFactor);

            var imgHeight = imgEl.getHeight(),
                imgWidth = imgEl.getWidth();
            console.debug('image (' + imgWidth + ', ' + imgHeight + ')');

            var panX, panY;
            if (!this.pan.overrideZoom) {
                var zoomWidthPanFactor = (this.zoom.x - this.pan.x) / oldZoomFactor;
                var zoomHeightPanFactor = (this.zoom.y - this.pan.y) / oldZoomFactor;

                panX = this.zoom.x - zoomFactor * zoomWidthPanFactor;
                panY = this.zoom.y - zoomFactor * zoomHeightPanFactor;
                console.debug('based on zoom center, pan = (' + panX + ', ' + panY + ')');
            } else {
                panX = this.pan.x;
                panY = this.pan.y;
            }

            // restrain pan to image or canvas size whatever is larger
            var rightLimit = (this.canvasWidth - imgWidth) / this.canvasWidth;
            if (imgWidth > this.canvasWidth) {
                panX = Math.max(Math.min(panX, 0), rightLimit);
            } else {
                panX = Math.min(Math.max(panX, 0), rightLimit);
            }
            // divide by canvasWidth because % are calculated from the element width
            var bottomLimit = (this.canvasHeight - imgHeight) / this.canvasWidth;
            if (imgHeight > this.canvasHeight) {
                panY = Math.max(Math.min(panY, 0), bottomLimit);
            } else {
                panY = Math.min(Math.max(panY, 0), bottomLimit);
            }
            console.debug('after restraining to (0, 0, ' + rightLimit + ', ' + bottomLimit + '), \npan = (' + panX, ', ' + panY + ')');

            imgEl.setMarginLeft(panX * 100 + '%').setMarginTop(panY * 100 + '%');

            if (panX != this.pan.x || panY != this.pan.y) {
                this.pan.x = panX;
                this.pan.y = panY;
                this.notifyPanChanged(panX, panY);
            }
            if (zoomFactor != this.zoom.factor) {
                this.zoom.factor = zoomFactor;
                this.notifyZoomChanged(zoomFactor);
            }
            console.groupEnd();
        }

        private disableCanvas() {
            this.image.getEl().setWidth('').setMarginLeft('').setMarginTop('');
        }

        private enableCanvas() {

            this.recalculateHeight(true, true);

            this.suspendRender = true;
            this.setZoom(this.zoom.factor, false);
            this.setPan(this.pan.x, this.pan.y, false);
            this.suspendRender = false;
            this.renderCanvas();
        }

        private recalculateHeight(updateWidth: boolean, updateRatio: boolean) {
            if (updateWidth) {
                this.canvasWidth = this.getEl().getWidth();
                console.debug('new width = ' + this.canvasWidth);
            }
            if (updateRatio) {
                this.imageRatio = +(this.image.getEl().getWidth() / this.image.getEl().getHeight()).toFixed(3);
                console.debug('new ratio = ' + this.imageRatio);
            }
            this.canvasHeight = +(this.canvasWidth / this.imageRatio).toFixed(3);
            console.debug('new height = ' + this.canvasHeight);
            this.getEl().setHeightPx(this.canvasHeight);
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