module api.ui.image {

    import ImgEl = api.dom.ImgEl;
    import DivEl = api.dom.DivEl;
    import Button = api.ui.button.Button;
    import Element = api.dom.Element;

    export interface Point {
        x: number;
        y: number;
    }

    export interface Rect extends Point {
        w: number;
        h: number;
    }

    interface FocusData extends Point {
        r: number;
        auto: boolean;
    }

    interface CropData extends Rect {
        auto: boolean;
    }

    interface ZoomData extends Rect {}

    export class ImageEditor extends api.dom.DivEl {

        private frame: DivEl;
        private canvas: DivEl;
        private image: ImgEl;
        private clip: Element;
        private dragHandle: Element;
        private zoomSlider: Element;
        private zoomSliderHeight: number = 200;
        private zoomLine: Element;
        private zoomKnob: Element;
        private focusClipPath: Element;
        private cropClipPath: Element;

        private focusData: FocusData = {x: 0, y: 0, r: 0, auto: true};
        private revertFocusData: FocusData;

        private cropData: CropData = {x: 0, y: 0, w: 0, h: 0, auto: true};
        private revertCropData: CropData;

        private zoomData: ZoomData = {x: 0, y: 0, w: 0, h: 0};
        private revertZoomData: ZoomData;

        private imgW: number;
        private imgH: number;
        private frameW: number;
        private frameH: number;
        private maxZoom = 1;
        private imageSmallerThanFrame: boolean = false;

        private mouseUpListener;
        private mouseMoveListener;
        private mouseDownListener;
        private mouseWheelListener;
        private dragMouseDownListener;
        private knobMouseDownListener;

        private buttonsContainer: DivEl;
        private focalButtonsContainer: DivEl;
        private cropButtonsContainer: DivEl;

        private focalPointButton: Button;
        private cropButton: Button;

        private focusPositionChangedListeners: {(position: Point): void}[] = [];
        private autoFocusChangedListeners: {(auto: boolean): void}[] = [];
        private focusRadiusChangedListeners: {(r: number): void}[] = [];
        private focusEditModeListeners: {(edit: boolean, position: Point): void}[] = [];

        private cropPositionChangedListeners: {(position: Rect): void}[] = [];
        private autoCropChangedListeners: {(auto: boolean): void}[] = [];
        private cropEditModeListeners: {(edit: boolean, position: Rect, zoomPosition: Rect): void}[] = [];

        public static debug = false;

        constructor(src?: string) {
            super('image-editor');

            var toolbar = this.createToolbar();
            this.frame = new DivEl('image-frame');
            this.canvas = new DivEl('image-canvas');

            this.image = new ImgEl(null, 'image-bg');
            this.image.onLoaded((event: UIEvent) => {
                if (this.isImageLoaded()) {
                    // check that real image has been loaded
                    this.appendChild(toolbar);
                    this.updateImageDimensions();
                }
            });

            var myId = this.getId();

            var clipHtml = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1">' +
                           '    <defs>' +
                           '        <clipPath id="' + myId + '-focusClipPath">' +
                           '            <circle cx="0" cy="0" r="0" class="clip-circle"/>' +
                           '        </clipPath>' +
                           '        <clipPath id="' + myId + '-cropClipPath">' +
                           '            <rect x="0" y="0" width="0" height="0"/>' +
                           '        </clipPath>' +
                           '    </defs>' +
                           '    <image xlink:href="' + ImgEl.PLACEHOLDER + '" width="100%" height="100%"/>' +   // FF won't apply css dimensions to image
                           '    <g class="edit-group focus-group">' +
                           '        <circle cx="0" cy="0" r="0" class="stroke-circle"/>' +
                           '    </g>' +
                           '    <g class="edit-group crop-group">' +
                           '        <svg id="' + myId + '-dragHandle" class="drag-handle">' +
                           '            <defs>' +
                           '                <polygon id="' + myId + '-dragTriangle" class="drag-triangle" points="8,0,16,8,0,8"/>' +
                           '            </defs>' +
                           '            <circle cx="16" cy="16" r="16"/>' +
                           '            <use xlink:href="#' + myId + '-dragTriangle" x="8" y="6"/>' +
                           '            <use xlink:href="#' + myId + '-dragTriangle" x="8" y="18" transform="rotate(180, 16, 22)"/>' +
                           '        </svg>' +
                           '        <svg id="' + myId + '-zoomSlider" class="zoom-slider">' +
                           '            <rect x="0" y="0" width="40" height="' + this.zoomSliderHeight + '" rx="20" ry="20"/>' +
                           '            <line id="' + myId + '-zoomLine" x1="20" y1="20" x2="20" y2="180"/>' +
                           '            <circle id="' + myId + '-zoomKnob" cx="20" cy="-1" r="8"/>' +
                           '        </svg>' +
                           '    </g>' +
                           '</svg>';

            this.clip = Element.fromString(clipHtml);

            this.dragHandle = this.clip.findChildById(myId + '-dragHandle', true);
            this.zoomSlider = this.clip.findChildById(myId + '-zoomSlider', true);
            this.focusClipPath = this.clip.findChildById(myId + '-focusClipPath', true);
            this.cropClipPath = this.clip.findChildById(myId + '-cropClipPath', true);
            this.zoomLine = this.zoomSlider.findChildById(myId + '-zoomLine');
            this.zoomKnob = this.zoomSlider.findChildById(myId + '-zoomKnob');

            // prevent FF image dragging
            this.clip.getHTMLElement().querySelector('image')['ondragstart'] = function () {
                return false
            };

            this.canvas.appendChildren(this.image, this.clip);

            this.frame.appendChild(this.canvas);

            this.appendChild(this.frame);

            if (src) {
                this.setSrc(src);
            }

            this.setFocusAutoPositioned(true);
            this.setCropAutoPositioned(true);
        }

        remove(): ImageEditor {
            if (this.isFocusEditMode()) {
                this.setFocusEditMode(false, false);
            } else if (this.isCropEditMode()) {
                this.setCropEditMode(false, false);
            }
            super.remove();
            return this;
        }

        setSrc(src: string) {
            this.image.setSrc(src);
            var image = this.clip.getHTMLElement().querySelector('image');
            image.setAttribute('xlink:href', src);
        }

        getSrc(): string {
            return this.image.getSrc();
        }

        getImage(): ImgEl {
            return this.image;
        }

        private setImageClipPath(path: Element) {
            var image = this.clip.getHTMLElement().querySelector('image');
            image.setAttribute('clip-path', 'url(#' + path.getId() + ')');
        }

        /**
         * Converts point from px to %
         * @param point point object to normalize
         * @returns {Point} normalized to 0-1 point
         */
        private normalizePoint(point: Point): Point {
            return {
                x: point.x / Math.min(this.frameW, this.imgW),
                y: point.y / Math.min(this.frameH, this.imgH)
            }
        }

        /**
         * Converts point from % to px
         * @param x
         * @param y
         * @returns {Point} denormalized point
         */
        private denormalizePoint(x: number, y: number): Point {
            return {
                x: x * Math.min(this.frameW, this.imgW),
                y: y * Math.min(this.frameH, this.imgH)
            }
        }

        /**
         * Converts rectangle from px to %
         * @param rect rectangle object to normalize
         * @returns {Rect} normalized to 0-1 rectangle
         */
        private normalizeRect(rect: Rect): Rect {
            var minW = Math.min(this.frameW, this.imgW);
            var minH = Math.min(this.frameH, this.imgH);
            return {
                x: rect.x / minW,
                y: rect.y / minH,
                w: rect.w / minW,
                h: rect.h / minH
            }
        }

        /**
         * Converts rectangle from % to px
         * @param x
         * @param y
         * @param w
         * @param h
         * @returns {Rect} denormalized rectangle
         */
        private denormalizeRect(x: number, y: number, w: number, h: number): Rect {
            var minW = Math.min(this.frameW, this.imgW);
            var minH = Math.min(this.frameH, this.imgH);
            return {
                x: x * minW,
                y: y * minH,
                w: w * minW,
                h: h * minH
            }
        }

        /**
         * Converts radius from px to % of the smallest dimension
         * @param r
         * @returns {number} normalized to 0-1 radius
         */
        private normalizeRadius(r: number): number {
            return r / Math.min(this.frameW, this.frameH, this.imgW, this.imgH);
        }

        /**
         * Converts radius from % of the smallest dimension to px
         * @param r
         * @returns {number} denormalized radius
         */
        private denormalizeRadius(r: number): number {
            return r * Math.min(this.frameW, this.frameH, this.imgW, this.imgH);
        }

        private getOffsetX(e: MouseEvent, relativeToZoom?: boolean): number {
            return e.clientX - this.getEl().getOffset().left - (relativeToZoom ? this.zoomData.x : 0);
        }

        private getOffsetY(e: MouseEvent, relativeTooom?: boolean): number {
            return e.clientY - this.getEl().getOffset().top - (relativeTooom ? this.zoomData.y : 0);
        }

        private isImageLoaded(): boolean {
            return this.image.isLoaded() && !this.image.isPlaceholder();
        }

        private updateImageDimensions() {
            var imgEl = this.image.getEl(),
                frameEl = this.frame.getEl();

            this.frameW = frameEl.getWidthWithBorder();

            this.imgW = imgEl.getNaturalWidth();
            this.imgH = imgEl.getNaturalHeight();
            this.maxZoom = this.imgW / this.frameW;

            this.frameH = (this.frameW * this.imgH) / this.imgW;

            this.imageSmallerThanFrame = this.imgW < this.frameW;

            if (ImageEditor.debug) {
                console.group('ImageEditor.updateImageDimensions');
                console.log('Image loaded: ' + this.frameW + ' x ' + this.frameH + ', frame: ' + this.frameW + ' x ' + this.frameH);
            }

            this.frame.getEl().setWidthPx(this.frameW).setHeightPx(this.frameH);

            if (this.revertZoomData) {
                // zoom was set while images was not yet loaded (saved in px);
                this.setZoomPositionPx(this.denormalizeRect(
                    this.revertZoomData.x,
                    this.revertZoomData.y,
                    this.revertZoomData.w,
                    this.revertZoomData.h));

                this.revertZoomData = undefined;
            } else if (this.cropData.auto) {
                // use cropData.auto flag for zoom as well
                this.resetZoomPosition();
            }

            // crop depends on zoom so init it after
            if (this.revertCropData) {
                // crop was set while images was not yet loaded (saved in px);
                this.setCropPositionPx(this.denormalizeRect(
                    this.revertCropData.x,
                    this.revertCropData.y,
                    this.revertCropData.w,
                    this.revertCropData.h));

                this.revertCropData = undefined;
            } else if (this.cropData.auto) {
                this.resetCropPosition();
            }

            // focus is not modifiable for now so just reset it to default value
            this.resetFocusRadius();

            // focus depends on zoom so init it after
            if (this.revertFocusData) {
                // position was set while image was not yet loaded
                this.setFocusPositionPx(this.denormalizePoint(
                    this.revertFocusData.x,
                    this.revertFocusData.y));

                this.revertFocusData = undefined;
            } else if (this.focusData.auto) {
                // set position to center by default
                this.resetFocusPosition();
            }

            this.updateFocusMaskPosition();
            this.updateCropMaskPosition();
            this.updateZoomPosition();

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        private createToolbar(): DivEl {
            var toolbar = new DivEl('image-toolbar');

            this.focalPointButton = new Button();
            this.focalPointButton.addClass('no-bg icon-center_focus_strong').onClicked((event: MouseEvent) => this.setFocusEditMode(true));

            this.cropButton = new Button();
            this.cropButton.addClass('no-bg icon-crop').onClicked((event: MouseEvent) => this.setCropEditMode(true));

            this.buttonsContainer = new DivEl('buttons-container');
            this.buttonsContainer.appendChildren(this.focalPointButton, this.cropButton);

            this.focalButtonsContainer = this.createFocalButtonsContainer();

            this.cropButtonsContainer = this.createCropButtonsContainer();

            toolbar.appendChildren(this.buttonsContainer, this.focalButtonsContainer, this.cropButtonsContainer);

            return toolbar;
        }

        private setShaderVisible(visible: boolean) {
            new api.app.wizard.MaskWizardPanelEvent(visible).fire();
        }


        /*
         *  Focus related methods
         */

        setFocusEditMode(edit: boolean, applyChanges: boolean = true) {
            this.toggleClass('edit-mode edit-focus', edit);
            this.setImageClipPath(this.focusClipPath);
            this.setShaderVisible(edit);

            this.buttonsContainer.setVisible(!edit);
            this.focalButtonsContainer.setVisible(edit);

            if (edit) {
                this.bindFocusMouseListeners();
                var focusPosition = this.getFocusPositionPx();
                this.revertFocusData = {
                    x: focusPosition.x,
                    y: focusPosition.y,
                    r: this.getFocusRadiusPx(),
                    auto: this.focusData.auto
                };
                // set mask position instead of just updating to restrain coordinates
                // in case it was updated during stand by or the crop has been changed
                this.setFocusPositionPx(this.revertFocusData);
            } else {
                this.unbindFocusMouseListeners();
                if (!applyChanges) {
                    this.setFocusPositionPx({x: this.revertFocusData.x, y: this.revertFocusData.y}, false);
                    this.setFocusRadiusPx(this.revertFocusData.r, false);
                    this.setFocusAutoPositioned(this.revertFocusData.auto);
                }
                this.revertFocusData = undefined;
            }
            // notify position updated in case we exit edit mode and apply changes
            this.notifyFocusModeChanged(edit, !edit && applyChanges ? this.getFocusPosition() : undefined);
        }

        isFocusEditMode(): boolean {
            return this.hasClass('edit-focus');
        }

        /**
         * Sets the center of the focal point
         * @param x horizontal value in 0-1 interval
         * @param y vertical value in 0-1 interval
         * @returns {undefined}
         */
        setFocusPosition(x: number, y: number) {

            if (this.isImageLoaded()) {
                this.setFocusPositionPx(this.denormalizePoint(x, y));
            } else {
                // use revert position to temporary save values until the image is loaded
                // can't denormalize until image is loaded
                this.revertFocusData = {
                    x: x,
                    y: y,
                    r: this.focusData.r,
                    auto: this.focusData.auto
                }
            }
        }

        private setFocusPositionPx(position: Point, updateAuto: boolean = true) {
            var oldX = this.focusData.x,
                oldY = this.focusData.y;

            if (ImageEditor.debug) {
                console.group('ImageEditor.setFocusPositionPx');
                console.log('Before restraining', position.x - oldX, position.y - oldY, position);
            }

            this.focusData.x = this.restrainFocusX(position.x);
            this.focusData.y = this.restrainFocusY(position.y);

            if (updateAuto) {
                this.setFocusAutoPositioned(false);
            }

            if (oldX != this.focusData.x || oldY != this.focusData.y) {
                this.notifyFocusPositionChanged(this.focusData);

                if (ImageEditor.debug) {
                    console.log('After restraining', this.focusData.x - oldX, this.focusData.y - oldY, this.focusData);
                }

                if (this.isImageLoaded()) {
                    this.updateFocusMaskPosition();
                }
            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        /**
         * Returns the center of the focal point as 0-1 values
         * @returns {{x, y}|Point}
         */
        getFocusPosition(): Point {
            return this.normalizePoint(this.getFocusPositionPx());
        }

        private getFocusPositionPx(): Point {
            return {
                x: this.focusData.x,
                y: this.focusData.y
            }
        }

        resetFocusPosition() {
            var denormalizedPoint = this.denormalizePoint(0.5, 0.5);
            // make sure it resets to the center of the crop area
            this.setFocusPositionPx({
                x: denormalizedPoint.x + this.cropData.x,
                y: denormalizedPoint.y + this.cropData.y
            }, false);
            this.setFocusAutoPositioned(true);
        }

        resetFocusRadius() {
            this.setFocusRadiusPx(this.denormalizeRadius(0.25), false);
        }

        setFocusRadius(r: number) {
            return this.setFocusRadiusPx(this.denormalizeRadius(r));
        }

        private setFocusRadiusPx(r: number, updateAuto: boolean = true) {
            var oldR = this.focusData.r;

            if (ImageEditor.debug) {
                console.group('ImageEditor.setFocusRadiusPx');
                console.log('Before restraining', r - oldR, r);
            }

            this.focusData.r = this.restrainFocusRadius(r);

            if (updateAuto) {
                this.setFocusAutoPositioned(false);
            }

            if (oldR != this.focusData.r) {
                this.notifyFocusRadiusChanged(this.focusData.r);

                if (ImageEditor.debug) {
                    console.log('After restraining', this.focusData.r - oldR, this.focusData);
                }

                if (this.isImageLoaded()) {
                    this.updateFocusMaskPosition();
                }
            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        /**
         * Returns the radius normalized by smallest dimension either image or frame
         * @returns {number}
         */
        getFocusRadius(): number {
            return this.normalizeRadius(this.getFocusRadiusPx());
        }

        private getFocusRadiusPx(): number {
            return this.focusData.r;
        }

        private setFocusAutoPositioned(auto: boolean) {
            var autoChanged = this.focusData.auto != auto;
            this.focusData.auto = auto;
            this.focalPointButton.toggleClass('manual', !auto);
            if (autoChanged) {
                this.notifyFocusAutoPositionedChanged(auto);
            }
        }

        private bindFocusMouseListeners() {
            var mouseDown: boolean = false;
            var lastPos: Point;

            if (ImageEditor.debug) {
                console.log('ImageEditor.bindFocusMouseListeners');
            }

            this.mouseDownListener = (event: MouseEvent) => {

                if (ImageEditor.debug) {
                    console.log('ImageEditor.mouseDownListener');
                }

                mouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event, true),
                    y: this.getOffsetY(event, true)
                };
            };
            this.clip.onMouseDown(this.mouseDownListener);

            this.mouseMoveListener = (event: MouseEvent) => {
                if (mouseDown) {
                    if (ImageEditor.debug) {
                        console.log('ImageEditor.mouseMoveListener');
                    }

                    var restrainedPos = {
                        x: this.restrainFocusX(this.focusData.x + this.getOffsetX(event, true) - lastPos.x),
                        y: this.restrainFocusY(this.focusData.y + this.getOffsetY(event, true) - lastPos.y)
                    };
                    this.setFocusPositionPx(restrainedPos);

                    lastPos = restrainedPos;
                }
            };
            api.dom.Body.get().onMouseMove(this.mouseMoveListener);

            this.mouseUpListener = (event: MouseEvent) => {
                if (mouseDown) {
                    if (ImageEditor.debug) {
                        console.log('ImageEditor.mouseUpListener');
                    }
                    // allow focus positioning by clicking
                    var restrainedPos = {
                        x: this.restrainFocusX(this.getOffsetX(event, true)),
                        y: this.restrainFocusY(this.getOffsetY(event, true))
                    };
                    this.setFocusPositionPx(restrainedPos);

                    mouseDown = false;
                }
            };
            api.dom.Body.get().onMouseUp(this.mouseUpListener);
        }

        private unbindFocusMouseListeners() {
            if (ImageEditor.debug) {
                console.log('ImageEditor.unbindFocusMouseListeners');
            }

            api.dom.Body.get().unMouseMove(this.mouseMoveListener);
            api.dom.Body.get().unMouseUp(this.mouseUpListener);
        }

        private createFocalButtonsContainer(): DivEl {
            var setFocusButton = new Button('Set Focus');
            setFocusButton.setEnabled(false).addClass('blue').onClicked((event: MouseEvent) => this.setFocusEditMode(false));

            var resetButton = new Button('Reset');
            resetButton.setEnabled(false).addClass('red').onClicked((event: MouseEvent) => this.resetFocusPosition());

            var cancelButton = new Button('Cancel');
            cancelButton.onClicked((event: MouseEvent) => this.setFocusEditMode(false, false));

            this.onFocusAutoPositionedChanged((auto) => {
                resetButton.setEnabled(!auto);
                setFocusButton.setEnabled(!auto);
            });

            var focalButtonsContainer = new DivEl('edit-container');
            focalButtonsContainer.setVisible(false).appendChildren(setFocusButton, resetButton, cancelButton);

            return focalButtonsContainer;
        }

        private updateFocusMaskPosition() {
            var clipCircle = this.focusClipPath.getHTMLElement().querySelector('circle'),
                strokeCircle = this.clip.getHTMLElement().querySelector('.focus-group circle');

            if (ImageEditor.debug) {
                console.log('ImageEditor.updateFocusPosition', this.focusData);
            }

            var circles = [clipCircle, strokeCircle];
            for (var i = 0; i < circles.length; i++) {
                var circle = <HTMLElement> circles[i];
                circle.setAttribute('r', this.focusData.r.toString());
                circle.setAttribute('cx', this.focusData.x.toString());
                circle.setAttribute('cy', this.focusData.y.toString());
            }
        }

        private restrainFocusX(x: number) {
            return Math.max(this.cropData.x, Math.min(this.cropData.x + this.cropData.w, x));
        }

        private restrainFocusY(y: number) {
            return Math.max(this.cropData.y, Math.min(this.cropData.y + this.cropData.h, y));
        }

        private restrainFocusRadius(r: number) {
            return Math.max(0, Math.min(this.frameW, this.frameH, r));
        }


        /*
         *  Crop related methods
         */

        setCropEditMode(edit: boolean, applyChanges: boolean = true) {
            this.toggleClass('edit-mode edit-crop', edit);
            this.setImageClipPath(this.cropClipPath);
            this.setShaderVisible(edit);

            this.buttonsContainer.setVisible(!edit);
            this.cropButtonsContainer.setVisible(edit);

            if (edit) {
                this.bindCropMouseListeners();
                var cropPosition = this.getCropPositionPx();
                this.revertCropData = {
                    x: cropPosition.x,
                    y: cropPosition.y,
                    w: cropPosition.w,
                    h: cropPosition.h,
                    auto: this.cropData.auto
                };
                var zoomPosition = this.getZoomPositionPx();
                this.revertZoomData = {
                    x: zoomPosition.x,
                    y: zoomPosition.y,
                    w: zoomPosition.w,
                    h: zoomPosition.h
                };
                // update mask position in case it was updated during stand by
                this.updateCropMaskPosition();
                this.updateZoomPosition();
            } else {
                this.unbindCropMouseListeners();
                if (!applyChanges) {
                    this.setZoomPositionPx(this.revertZoomData, false);
                    this.setCropPositionPx(this.revertCropData, false);
                    this.setCropAutoPositioned(this.revertCropData.auto);
                }
                this.revertCropData = undefined;
                this.revertZoomData = undefined;
            }
            // notify position updated in case we exit edit mode and apply changes
            this.notifyCropModeChanged(edit,
                !edit && applyChanges ? this.getCropPosition() : undefined,
                !edit && applyChanges ? this.getZoomPosition() : undefined);
        }

        isCropEditMode(): boolean {
            return this.hasClass('edit-crop');
        }

        /**
         * Sets the crop area
         * @param x
         * @param y
         * @param w
         * @param h
         */
        setCropPosition(x: number, y: number, w: number, h: number) {
            if (this.isImageLoaded()) {
                this.setCropPositionPx(this.denormalizeRect(x, y, w, h));
            } else {
                // use revert position to temporary save values until the image is loaded
                // can't denormalize until image is loaded
                this.revertCropData = {
                    x: x,
                    y: y,
                    w: w,
                    h: h,
                    auto: this.cropData.auto
                }
            }
        }

        private setCropPositionPx(crop: Rect, updateAuto: boolean = true) {

            if(this.isCropAreaSmallerThanZoomSlider(crop.h)) {
                return;
            }

            var oldX = this.cropData.x,
                oldY = this.cropData.y,
                oldW = this.cropData.w,
                oldH = this.cropData.h;

            if (ImageEditor.debug) {
                console.group('ImageEditor.setCropPositionPx');
                console.log('Before restraining', crop.x - oldX, crop.y - oldY, crop);
            }

            this.cropData.w = this.restrainCropW(crop.w);
            this.cropData.h = this.restrainCropH(crop.h);
            this.cropData.x = this.restrainCropX(crop.x);
            this.cropData.y = this.restrainCropY(crop.y);

            this.updateFrameHeight();

            if (updateAuto) {
                this.setCropAutoPositioned(false);
            }

            if (oldX != this.cropData.x ||
                oldY != this.cropData.y ||
                oldW != this.cropData.w ||
                oldH != this.cropData.h) {

                var dx = this.cropData.x - oldX,
                    dy = this.cropData.y - oldY;

                if (ImageEditor.debug) {
                    console.log('After restraining', dx, dy, this.cropData);
                }

                this.notifyCropPositionChanged(this.cropData);

                if (this.isImageLoaded() && this.isCropEditMode()) {
                    this.updateCropMaskPosition();
                }
            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        /**
         * Gets the crop area
         * @returns {{x, y, w, h}|Rect}
         */
        getCropPosition(): Rect {
            return this.normalizeRect(this.getCropPositionPx());
        }

        private getCropPositionPx(): Rect {
            return {
                x: this.cropData.x,
                y: this.cropData.y,
                w: this.cropData.w,
                h: this.cropData.h
            }
        }

        resetCropPosition() {
            this.setCropPositionPx(this.denormalizeRect(0, 0, 1, 1), false);
            this.setCropAutoPositioned(true);
        }

        private setCropAutoPositioned(auto: boolean) {
            var autoChanged = this.cropData.auto != auto;
            this.cropData.auto = auto;
            this.cropButton.toggleClass('manual', !auto);
            if (autoChanged) {
                this.notifyCropAutoPositionedChanged(auto);
            }
        }

        private createCropButtonsContainer(): DivEl {
            var cropButton = new Button('Crop');
            cropButton.setEnabled(false).addClass('blue').onClicked((event: MouseEvent) => this.setCropEditMode(false));

            var resetButton = new Button('Reset');
            resetButton.setEnabled(false).addClass('red').onClicked((event: MouseEvent) => {
                this.resetZoomPosition();
                this.resetCropPosition();
            });

            var cancelButton = new Button('Cancel');
            cancelButton.onClicked((event: MouseEvent) => this.setCropEditMode(false, false));

            this.onCropAutoPositionedChanged((auto) => {
                resetButton.setEnabled(!auto);
                cropButton.setEnabled(!auto);
            });

            var cropButtonsContainer = new DivEl('edit-container');
            cropButtonsContainer.setVisible(false).appendChildren(cropButton, resetButton, cancelButton);
            return cropButtonsContainer;

        }

        private updateCropMaskPosition() {
            var zoom = this.zoomSlider.getHTMLElement(),
                rect = this.cropClipPath.getHTMLElement().querySelector('rect'),
                drag = this.dragHandle.getHTMLElement();

            if (ImageEditor.debug) {
                console.log('ImageEditor.updateCropPosition', this.cropData);
            }

            rect.setAttribute('x', this.cropData.x.toString());
            rect.setAttribute('y', this.cropData.y.toString());
            rect.setAttribute('width', this.cropData.w.toString());
            rect.setAttribute('height', this.cropData.h.toString());

            // 16 is the half-size of drag
            drag.setAttribute('x', (this.cropData.x + this.cropData.w / 2 - 16).toString());
            drag.setAttribute('y', (this.cropData.y + this.cropData.h - 16).toString());

            // 40px is the width of zoom control + 20px to the edge of the canvas
            zoom.setAttribute('x', (this.cropData.x + this.cropData.w - 20 - 40).toString());
            // 200px is the height of the zoom control
            zoom.setAttribute('y', (this.cropData.y + (this.cropData.h - 200) / 2 ).toString());
        }

        private isInsideCrop(x: number, y: number) {
            return x >= this.zoomData.x + this.cropData.x &&
                   x <= (this.zoomData.x + this.cropData.x + this.cropData.w) &&
                   y >= this.zoomData.y + this.cropData.y &&
                   y <= (this.zoomData.y + this.cropData.y + this.cropData.h);

        }

        private bindCropMouseListeners() {
            var dragMouseDown = false,
                zoomMouseDown = false,
                panMouseDown = false;
            var lastPos: Point;

            if (ImageEditor.debug) {
                console.log('ImageEditor.bindCropMouseListeners');
            }

            this.dragMouseDownListener = (event: MouseEvent) => {
                event.stopPropagation();
                event.preventDefault();

                if (ImageEditor.debug) {
                    console.log('ImageEditor.dragMouseListener');
                }

                dragMouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
                this.dragHandle.addClass('active');
            };
            this.dragHandle.onMouseDown(this.dragMouseDownListener);

            this.knobMouseDownListener = (event: MouseEvent) => {
                event.stopPropagation();
                event.preventDefault();

                if (ImageEditor.debug) {
                    console.log('ImageEditor.knobMouseListener');
                }

                zoomMouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
                this.zoomSlider.addClass('active');
            };
            this.zoomKnob.onMouseDown(this.knobMouseDownListener);

            this.mouseWheelListener = (event: WheelEvent) => {
                event.preventDefault();
                event.stopPropagation();

                if (ImageEditor.debug) {
                    console.log('ImageEditor.wheelMouseListener');
                }

                if (!this.imageSmallerThanFrame) {
                    var delta;
                    switch (event.deltaMode) {
                    case WheelEvent.DOM_DELTA_PIXEL:
                        delta = event.deltaY / 10;
                        break;
                    case WheelEvent.DOM_DELTA_LINE:
                        delta = event.deltaY * 10 / 3;
                        break;
                    case WheelEvent.DOM_DELTA_PAGE:
                        delta = event.deltaY * 100; //approximate value, change if needed
                        break;
                    }

                    this.moveZoomKnobByPx(delta);
                }
            };
            this.clip.onMouseWheel(this.mouseWheelListener);

            this.mouseDownListener = (event: MouseEvent) => {
                var x = this.getOffsetX(event),
                    y = this.getOffsetY(event);

                if (ImageEditor.debug) {
                    console.log('ImageEditor.mouseDownListener');
                }

                if (this.isInsideCrop(x, y)) {
                    if (ImageEditor.debug) {
                        console.log('click inside crop area');
                    }
                    panMouseDown = true;
                    lastPos = {
                        x: x,
                        y: y
                    };
                } else {
                    if (ImageEditor.debug) {
                        console.log('click outside crop area');
                    }
                }
            };
            this.clip.onMouseDown(this.mouseDownListener);

            this.mouseMoveListener = (event: MouseEvent) => {

                var currPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };

                if (!this.isInsideZoom(currPos.x, currPos.y)) {
                    // only act when mouse is inside zoomed image
                    return;
                }

                if (zoomMouseDown) {

                    this.moveZoomKnobByPx(currPos.y - lastPos.y);

                } else if (dragMouseDown) {

                    var deltaY = this.getOffsetY(event) - lastPos.y,
                        newH = this.cropData.h + deltaY;

                    if (newH > 0) {

                        this.setCropPositionPx({
                            x: this.cropData.x,
                            y: this.cropData.y,
                            w: this.cropData.w,
                            h: newH
                        });
                    }
                } else if (panMouseDown) {

                    this.setZoomPositionPx({
                        x: this.zoomData.x + currPos.x - lastPos.x,
                        y: this.zoomData.y + currPos.y - lastPos.y,
                        w: this.zoomData.w,
                        h: this.zoomData.h
                    });

                }

                lastPos = currPos;
            };
            api.dom.Body.get().onMouseMove(this.mouseMoveListener);

            this.mouseUpListener = (event: MouseEvent) => {
                if (ImageEditor.debug) {
                    console.log('ImageEditor.mouseUpListener');
                }

                if (dragMouseDown) {
                    dragMouseDown = false;
                    this.dragHandle.removeClass('active');
                } else if (zoomMouseDown) {
                    zoomMouseDown = false;
                    this.zoomSlider.removeClass('active');
                } else if (panMouseDown) {
                    panMouseDown = false;
                }
            };
            api.dom.Body.get().onMouseUp(this.mouseUpListener);
        }

        private unbindCropMouseListeners() {
            if (ImageEditor.debug) {
                console.log('ImageEditor.unbindCropMouseListeners');
            }

            this.dragHandle.unMouseDown(this.dragMouseDownListener);
            this.zoomKnob.unMouseDown(this.knobMouseDownListener);
            this.clip.unMouseWheel(this.mouseWheelListener);
            this.clip.unMouseDown(this.mouseDownListener);

            api.dom.Body.get().unMouseMove(this.mouseMoveListener);
            api.dom.Body.get().unMouseUp(this.mouseUpListener);
        }

        /**
         * Crop coordinate system starts in the top left corner of the zoom rectangle
         * @param x
         * @returns {number}
         */
        private restrainCropX(x: number) {
            return Math.max(0, Math.min(this.zoomData.w - this.cropData.w, x));
        }

        private restrainCropY(y: number) {
            return Math.max(0, Math.min(this.zoomData.h - this.cropData.h, y));
        }

        private restrainCropW(x: number) {
            return Math.max(0, Math.min(this.zoomData.w, x));
        }

        private restrainCropH(y: number) {
            return Math.max(0, Math.min(this.zoomData.h, y));
        }


        /*
         *  Zoom related methods
         */

        setZoomPosition(x: number, y: number, w: number, h: number) {
            if (this.isImageLoaded()) {
                this.setZoomPositionPx(this.denormalizeRect(x, y, w, h));
            } else {
                // use revert position to temporary save values until the image is loaded
                // can't denormalize until image is loaded
                this.revertZoomData = {
                    x: x,
                    y: y,
                    w: w,
                    h: h
                }
            }
        }

        private setZoomPositionPx(zoom: Rect, updateAuto: boolean = true) {
            var oldX = this.zoomData.x,
                oldY = this.zoomData.y,
                oldW = this.zoomData.w,
                oldH = this.zoomData.h;

            if (ImageEditor.debug) {
                console.group('ImageEditor.setZoomPositionPx');
                console.log('Before restraining', zoom.x - oldX, zoom.y - oldY, zoom);
            }

            this.zoomData.w = this.restrainZoomW(zoom.w);
            this.zoomData.h = this.restrainZoomH(zoom.h);
            this.zoomData.x = this.restrainZoomX(zoom.x);
            this.zoomData.y = this.restrainZoomY(zoom.y);

            if (oldX != this.zoomData.x ||
                oldY != this.zoomData.y ||
                oldW != this.zoomData.w ||
                oldH != this.zoomData.h) {

                var dx = this.zoomData.x - oldX,
                    dy = this.zoomData.y - oldY;

                if (ImageEditor.debug) {
                    console.log('After restraining', dx, dy, this.zoomData);
                }

                if (this.isImageLoaded()) {
                    this.updateZoomPosition();
                }

                if (!this.imageSmallerThanFrame) {
                    // update crop position for it to stay in place as zoom changes parent svg size
                    this.setCropPositionPx({
                        x: this.cropData.x - dx,
                        y: this.cropData.y - dy,
                        w: this.cropData.w,
                        h: this.cropData.h
                    }, updateAuto);

                } else if (updateAuto) {
                    // don't forget to flag crop as manually overridden
                    this.setCropAutoPositioned(false);
                }
            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        getZoomPosition(): Rect {
            return this.normalizeRect(this.getZoomPositionPx());
        }

        private getZoomPositionPx(): Rect {
            return {
                x: this.zoomData.x,
                y: this.zoomData.y,
                w: this.zoomData.w,
                h: this.zoomData.h
            }
        }

        resetZoomPosition() {

            var w = Math.min(this.imgW, this.frameW),
                h = Math.min(this.imgH, this.frameH);

            this.setZoomPositionPx({
                w: w,
                h: h,
                x: (this.frameW - w) / 2,
                y: (this.frameH - h) / 2
            }, false);

            this.setCropAutoPositioned(true);
        }

        private isInsideZoom(x: number, y: number) {
            return x >= this.zoomData.x && x <= (this.zoomData.x + this.zoomData.w) &&
                   y >= this.zoomData.y && y <= (this.zoomData.y + this.zoomData.h);
        }

        private moveZoomKnobByPx(delta: number) {

            var zoomLineEl = this.zoomLine.getHTMLElement(),
                zoomKnobEl = this.zoomKnob.getHTMLElement();

            var sliderStart = parseInt(zoomLineEl.getAttribute('y1')),
                sliderEnd = parseInt(zoomLineEl.getAttribute('y2')),
                sliderLength = sliderEnd - sliderStart,
                knobY = parseInt(zoomKnobEl.getAttribute('cy')),
                knobNewY = Math.max(sliderStart, Math.min(sliderEnd, knobY + delta));

            if (knobNewY != knobY) {
                zoomKnobEl.setAttribute('cy', knobNewY.toString());

                var knobPct = 1 - (knobNewY - sliderStart) / sliderLength,
                    zoomCoeff = 1 + knobPct * ( this.maxZoom - 1),
                    newW = this.restrainZoomW(this.frameW * zoomCoeff),
                    newH = this.restrainZoomH(this.frameH * zoomCoeff),
                    newX = this.zoomData.x - (newW - this.zoomData.w) / 2,
                    newY = this.zoomData.y - (newH - this.zoomData.h) / 2;

                this.setZoomPositionPx({
                    x: newX,
                    y: newY,
                    w: newW,
                    h: newH
                });
            }
        }

        private updateZoomPosition() {

            if (ImageEditor.debug) {
                console.log('ImageEditor.updateZoomPosition', this.zoomData);
            }

            this.canvas.getEl().
                setWidthPx(this.zoomData.w).
                setHeightPx(this.zoomData.h).
                setLeftPx(this.zoomData.x).
                setTopPx(this.zoomData.y);

            this.zoomSlider.setVisible(!this.imageSmallerThanFrame);

            if (this.imageSmallerThanFrame) {
                // zoom is disabled in this case
                return;
            }

            var zoomKnobEl = this.zoomKnob.getHTMLElement(),
                zoomLineEl = this.zoomLine.getHTMLElement();

            var sliderStart = parseInt(zoomLineEl.getAttribute('y1')),
                sliderEnd = parseInt(zoomLineEl.getAttribute('y2')),
                sliderLength = sliderEnd - sliderStart,
                knobPct = 1 - (this.zoomData.w / this.frameW - 1 ) / (this.maxZoom - 1),
                knobNewY = Math.max(sliderStart, Math.min(sliderEnd, sliderStart + knobPct * sliderLength));

            zoomKnobEl.setAttribute('cy', knobNewY.toString());
        }

        private isCropAreaSmallerThanZoomSlider(height: number): boolean {
            return height < this.zoomSliderHeight;
        }

        private updateFrameHeight() {  // making bottom border and everything underneath the image draggable
            this.frame.getEl().setHeightPx(this.cropData.h);
            wemjq(this.frame.getHTMLElement()).closest(".result-container").height(this.cropData.h);
        }

        /**
         * Zoom coordinates system starts in the top left corner of the original image
         * @param x
         * @returns {number}
         */
        private restrainZoomX(x: number) {
            var deltaW = this.frameW - this.zoomData.w;
            return Math.max(this.imageSmallerThanFrame ? 0 : deltaW,
                Math.min(this.imageSmallerThanFrame ? deltaW : 0, x));
        }

        private restrainZoomY(y: number) {
            var deltaH = this.frameH - this.zoomData.h;
            return Math.max(this.imageSmallerThanFrame ? 0 : deltaH,
                Math.min(this.imageSmallerThanFrame ? deltaH : 0, y));
        }

        private restrainZoomW(x: number) {
            return Math.max(this.imageSmallerThanFrame ? this.imgW : this.frameW,
                Math.min(this.imageSmallerThanFrame ? this.frameW : this.imgW, x));
        }

        private restrainZoomH(y: number) {
            return Math.max(this.imageSmallerThanFrame ? this.imgH : this.frameH,
                Math.min(this.imageSmallerThanFrame ? this.frameH : this.imgH, y));
        }


        /*
         *   Focus related listeners
         */

        /**
         * Bind listener to focus edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        onFocusModeChanged(listener: (edit: boolean, position: Point) => void) {
            this.focusEditModeListeners.push(listener);
        }

        /**
         * Unbind listener from focus edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        unFocusModeChanged(listener: (edit: boolean, position: Point) => void) {
            this.focusEditModeListeners = this.focusEditModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyFocusModeChanged(edit: boolean, position: Point) {
            this.focusEditModeListeners.forEach((listener) => {
                listener(edit, position);
            })
        }

        onFocusAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.autoFocusChangedListeners.push(listener);
        }

        unFocusAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.autoFocusChangedListeners = this.autoFocusChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusAutoPositionedChanged(auto: boolean) {
            this.autoFocusChangedListeners.forEach((listener) => {
                listener(auto);
            })
        }

        onFocusPositionChanged(listener: (position: Point) => void) {
            this.focusPositionChangedListeners.push(listener);
        }

        unFocusPositionChanged(listener: (position: Point) => void) {
            this.focusPositionChangedListeners = this.focusPositionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusPositionChanged(position: Point) {
            var normalizedPosition = this.normalizePoint(position);
            this.focusPositionChangedListeners.forEach((listener) => {
                listener(normalizedPosition);
            })
        }

        onFocusRadiusChanged(listener: (r: number) => void) {
            this.focusRadiusChangedListeners.push(listener);
        }

        unFocusRadiusChanged(listener: (r: number) => void) {
            this.focusRadiusChangedListeners = this.focusRadiusChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusRadiusChanged(r: number) {
            var normalizedRadius = this.normalizeRadius(r);
            this.focusRadiusChangedListeners.forEach((listener) => {
                listener(normalizedRadius);
            })
        }


        /*
         *   Crop related listeners
         */

        /**
         * Bind listener to focus edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        onCropModeChanged(listener: (edit: boolean, position: Rect, zoomPosition: Rect) => void) {
            this.cropEditModeListeners.push(listener);
        }

        /**
         * Unbind listener from crop edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        unCropModeChanged(listener: (edit: boolean, position: Rect) => void) {
            this.cropEditModeListeners = this.cropEditModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyCropModeChanged(edit: boolean, position: Rect, zoomPosition: Rect) {
            this.cropEditModeListeners.forEach((listener) => {
                listener(edit, position, zoomPosition);
            })
        }

        onCropAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.autoCropChangedListeners.push(listener);
        }

        unCropAutoPositionedChanged(listener: (auto: boolean) => void) {
            this.autoCropChangedListeners = this.autoCropChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyCropAutoPositionedChanged(auto: boolean) {
            this.autoCropChangedListeners.forEach((listener) => {
                listener(auto);
            })
        }

        onCropPositionChanged(listener: (position: Rect) => void) {
            this.cropPositionChangedListeners.push(listener);
        }

        unCropPositionChanged(listener: (position: Rect) => void) {
            this.cropPositionChangedListeners = this.cropPositionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyCropPositionChanged(position: Rect) {
            var normalizedPosition = this.normalizeRect(position);
            this.cropPositionChangedListeners.forEach((listener) => {
                listener(normalizedPosition);
            })
        }
    }

}