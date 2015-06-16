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

    export class ImageEditor extends api.dom.DivEl {

        private canvas: DivEl;
        private image: ImgEl;
        private clip: Element;
        private dragHandle: Element;
        private zoomSlider: Element;
        private zoomLine: Element;
        private zoomKnob: Element;

        private focusData: {x: number; y: number; r: number; auto: boolean} = {
            x: 0,
            y: 0,
            r: 0,
            auto: true
        };
        private revertFocusData: {x: number; y: number; r: number; auto: boolean};

        private cropData: {x: number; y: number; w: number; h: number; auto: boolean} = {
            x: 0,
            y: 0,
            w: 0,
            h: 0,
            auto: true
        };
        private revertCropData: {x: number; y: number; w: number; h: number; auto: boolean};

        private imgW: number;
        private imgH: number;

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
        private cropEditModeListeners: {(edit: boolean, position: Rect): void}[] = [];

        public static debug = false;

        constructor(src?: string) {
            super('image-editor');

            this.canvas = new DivEl('image-canvas');

            this.image = new ImgEl(null, 'image-bg');
            this.image.onLoaded((event: UIEvent) => this.updateImageDimensions());

            var clipHtml = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1">' +
                           '    <image width="100" height="100" xlink:href="' + ImgEl.PLACEHOLDER + '"/>' +
                           '    <g class="edit-group focus-group">' +
                           '        <clipPath id="focalClipPath">' +
                           '            <circle cx="0" cy="0" r="0" class="clip-circle"/>' +
                           '        </clipPath>' +
                           '        <circle cx="0" cy="0" r="0" class="stroke-circle"/>' +
                           '    </g>' +
                           '    <g class="edit-group crop-group">' +
                           '        <clipPath id="cropClipPath">' +
                           '            <rect x="0" y="0" width="0" height="0"/>' +
                           '        </clipPath>' +
                           '        <svg id="dragHandle" class="drag-handle">' +
                           '            <defs>' +
                           '                <polygon id="drag-triangle" class="drag-triangle" points="8,0,16,8,0,8"/>' +
                           '            </defs>' +
                           '            <circle cx="16" cy="16" r="16"/>' +
                           '            <use xlink:href="#drag-triangle" x="8" y="6"/>' +
                           '            <use xlink:href="#drag-triangle" x="8" y="18" transform="rotate(180, 16, 22)"/>' +
                           '        </svg>' +
                           '        <svg id="zoomSlider" class="zoom-slider">' +
                           '            <rect x="0" y="0" width="40" height="200" rx="20" ry="20"/>' +
                           '            <line id="zoomLine" x1="20" y1="20" x2="20" y2="180"/>' +
                           '            <circle id="zoomKnob" cx="20" cy="-1" r="8"/>' +
                           '        </svg>' +
                           '    </g>' +
                           '</svg>';

            this.clip = Element.fromString(clipHtml);

            this.dragHandle = this.clip.findChildById('dragHandle', true);
            this.zoomSlider = this.clip.findChildById('zoomSlider', true);
            this.zoomLine = this.zoomSlider.findChildById('zoomLine');
            this.zoomKnob = this.zoomSlider.findChildById('zoomKnob');

            this.canvas.appendChildren(this.image, this.clip);

            this.appendChildren(this.canvas, this.createToolbar());

            if (src) {
                this.setSrc(src);
            }

            this.setFocusAutoPositioned(true);
            this.setCropAutoPositioned(true);
        }

        remove(): ImageEditor {
            if (this.isFocusEditMode()) {
                this.setFocusEditMode(false);
            }
            super.remove();
            return this;
        }

        getImage(): api.dom.ImgEl {
            return this.image;
        }

        setSrc(src: string) {
            this.image.setSrc(src);
            var image = this.clip.getHTMLElement().querySelector('image');
            image.setAttribute('xlink:href', src);
        }

        getSrc(): string {
            return this.image.getSrc();
        }

        private setImageClipPath(path: string) {
            var image = this.clip.getHTMLElement().querySelector('image');
            image.setAttribute('clip-path', 'url(#' + path + ')');
        }

        /**
         * Converts point from px to %
         * @param point point object to normalize
         * @returns {Point} normalized to 0-1 point
         */
        private normalizePoint(point: Point): Point {
            return {
                x: point.x / this.imgW,
                y: point.y / this.imgH
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
                x: x * this.imgW,
                y: y * this.imgH
            }
        }

        /**
         * Converts rectangle from px to %
         * @param rect rectangle object to normalize
         * @returns {Rect} normalized to 0-1 rectangle
         */
        private normalizeRect(rect: Rect): Rect {
            return {
                x: rect.x / this.imgW,
                y: rect.y / this.imgH,
                w: rect.w / this.imgW,
                h: rect.h / this.imgH
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
            return {
                x: x * this.imgW,
                y: y * this.imgH,
                w: w * this.imgW,
                h: h * this.imgH
            }
        }

        /**
         * Converts radius from px to % of the smallest dimension
         * @param r
         * @returns {number} normalized to 0-1 radius
         */
        private normalizeRadius(r: number): number {
            return r / Math.min(this.imgW, this.imgH);
        }

        /**
         * Converts radius from % of the smallest dimension to px
         * @param r
         * @returns {number} denormalized radius
         */
        private denormalizeRadius(r: number): number {
            return r * Math.min(this.imgW, this.imgH);
        }

        private restrainWidth(x: number, width: number = 0) {
            return Math.max(0, Math.min(this.imgW - width, x));
        }

        private restrainHeight(y: number, height: number = 0) {
            return Math.max(0, Math.min(this.imgH - height, y));
        }

        private restrainRadius(r: number) {
            return Math.max(0, Math.min(this.imgH, this.imgW, r));
        }

        private getOffsetX(e: MouseEvent): number {
            return e.clientX - this.getEl().getOffset().left;
        }

        private getOffsetY(e: MouseEvent): number {
            return e.clientY - this.getEl().getOffset().top;
        }

        private isImageLoaded(): boolean {
            return this.image.isLoaded() && !this.image.isPlaceholder();
        }

        private updateImageDimensions() {
            var imgEl = this.image.getEl();
            this.imgW = imgEl.getNaturalWidth() + imgEl.getBorderLeftWidth() + imgEl.getBorderRightWidth() +
                        imgEl.getPaddingLeft() + imgEl.getPaddingRight();
            this.imgH = imgEl.getNaturalHeight() + imgEl.getBorderTopWidth() + imgEl.getBorderBottomWidth() +
                        imgEl.getPaddingTop() + imgEl.getPaddingBottom();

            // calculate radius first as it will be needed when setting position
            var autoPositioned = this.focusData.auto;
            this.setFocusRadius(0.25);
            this.setFocusAutoPositioned(autoPositioned);

            if (this.isImageLoaded() && this.revertFocusData) {
                // position was set while image was not yet loaded ( saved in 0-1 format )
                this.setFocusPosition(this.revertFocusData.x, this.revertFocusData.y);
                this.revertFocusData = undefined;
            } else if (this.focusData.auto) {
                // set position to center by default
                this.resetFocusPosition();
            }

            if (this.isImageLoaded() && this.revertCropData) {
                // crop was set while images was not yet loaded ( saved in 0-1 format );
                this.setCropPosition(this.revertCropData.x, this.revertCropData.y, this.revertCropData.w, this.revertCropData.h);
                this.revertCropData = undefined;
            } else if (this.cropData.auto) {
                this.resetCropPosition();
            }

            var image = this.clip.getHTMLElement().querySelector('image');
            image.setAttribute('width', this.imgW.toString());
            image.setAttribute('height', this.imgH.toString());

            if (this.image.isLoaded()) {
                this.updateFocusMaskPosition();
                this.updateCropMaskPosition();
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
            this.setImageClipPath('focalClipPath');
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
                // update mask position in case it was updated during stand by
                this.updateFocusMaskPosition();
            } else {
                this.unbindFocusMouseListeners();
                if (!applyChanges) {
                    this.setFocusPositionPx({x: this.revertFocusData.x, y: this.revertFocusData.y});
                    this.setFocusRadiusPx(this.revertFocusData.r);
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
                this.revertFocusData = {
                    x: x,
                    y: y,
                    r: this.focusData.r,
                    auto: this.focusData.auto
                }
            }
        }

        private setFocusPositionPx(position: Point) {
            var oldX = this.focusData.x,
                oldY = this.focusData.y;

            this.focusData.x = this.restrainWidth(position.x);
            this.focusData.y = this.restrainHeight(position.y);
            this.setFocusAutoPositioned(false);

            if (oldX != this.focusData.x || oldY != this.focusData.y) {
                this.notifyFocusPositionChanged(this.focusData);

                if (this.image.isLoaded() && this.isFocusEditMode()) {
                    this.updateFocusMaskPosition();
                }
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
            this.setFocusPosition(0.5, 0.5);
            this.setFocusAutoPositioned(true);
        }

        setFocusRadius(r: number) {
            return this.setFocusRadiusPx(this.denormalizeRadius(r));
        }

        private setFocusRadiusPx(r: number) {
            var oldR = this.focusData.r;
            this.focusData.r = this.restrainRadius(r);
            this.setFocusAutoPositioned(false);

            if (oldR != this.focusData.r) {
                this.notifyFocusRadiusChanged(this.focusData.r);

                if (this.image.isLoaded() && this.isFocusEditMode()) {
                    this.updateFocusMaskPosition();
                }
            }
        }

        /**
         * Returns the radius normalized by smallest dimension
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
                mouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
            };
            this.clip.onMouseDown(this.mouseDownListener);

            this.mouseMoveListener = (event: MouseEvent) => {
                if (mouseDown) {
                    var restrainedPos = {
                        x: this.restrainWidth(this.focusData.x + this.getOffsetX(event) - lastPos.x),
                        y: this.restrainHeight(this.focusData.y + this.getOffsetY(event) - lastPos.y)
                    };
                    this.setFocusPositionPx(restrainedPos);

                    lastPos = restrainedPos;
                }
            };
            api.dom.Body.get().onMouseMove(this.mouseMoveListener);

            this.mouseUpListener = (event: MouseEvent) => {
                if (mouseDown) {
                    // allow focus positioning by clicking
                    var restrainedPos = {
                        x: this.restrainWidth(this.getOffsetX(event)),
                        y: this.restrainHeight(this.getOffsetY(event))
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
            var circles = this.clip.getHTMLElement().querySelectorAll('.focus-group circle');

            for (var i = 0; i < circles.length; i++) {
                var circle = <HTMLElement> circles[i];
                circle.setAttribute('r', this.focusData.r.toString());
                circle.setAttribute('cx', this.focusData.x.toString());
                circle.setAttribute('cy', this.focusData.y.toString());
            }
        }

        /*
         *  Crop related methods
         */

        setCropEditMode(edit: boolean, applyChanges: boolean = true) {
            this.toggleClass('edit-mode edit-crop', edit);
            this.setImageClipPath('cropClipPath');
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
                // update mask position in case it was updated during stand by
                this.updateCropMaskPosition();
            } else {
                this.unbindCropMouseListeners();
                if (!applyChanges) {
                    this.moveZoomKnobTo(this.revertCropData);
                    this.setCropAutoPositioned(this.revertCropData.auto);
                }
                this.revertCropData = undefined;
            }
            // notify position updated in case we exit edit mode and apply changes
            this.notifyCropModeChanged(edit, !edit && applyChanges ? this.getCropPosition() : undefined);
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
                this.moveZoomKnobTo(this.denormalizeRect(x, y, w, h));
            } else {
                // use revert position to temporary save values until the image is loaded
                this.revertCropData = {
                    x: x,
                    y: y,
                    w: w,
                    h: h,
                    auto: this.cropData.auto
                }
            }
        }

        private setCropPositionPx(crop: Rect) {
            var oldX = this.cropData.x,
                oldY = this.cropData.y,
                oldW = this.cropData.w,
                oldH = this.cropData.h;

            this.cropData.x = this.restrainWidth(crop.x);
            this.cropData.y = this.restrainHeight(crop.y);
            this.cropData.w = this.restrainWidth(crop.w);
            this.cropData.h = this.restrainHeight(crop.h);
            this.setCropAutoPositioned(false);

            if (oldX != this.cropData.x ||
                oldY != this.cropData.y ||
                oldW != this.cropData.w ||
                oldH != this.cropData.h) {

                this.notifyCropPositionChanged(this.cropData);

                if (this.image.isLoaded() && this.isCropEditMode()) {
                    this.updateCropMaskPosition();
                }
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
            this.moveZoomKnobTo(this.denormalizeRect(0, 0, 1, 1));
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
            resetButton.setEnabled(false).addClass('red').onClicked((event: MouseEvent) => this.resetCropPosition());

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
            var clipEl = this.clip.getHTMLElement(),
                rect = clipEl.querySelector('#cropClipPath rect'),
                drag = clipEl.querySelector('#dragHandle'),
                zoom = clipEl.querySelector('#zoomSlider');

            rect.setAttribute('x', this.cropData.x.toString());
            rect.setAttribute('y', this.cropData.y.toString());
            rect.setAttribute('width', this.cropData.w.toString());
            rect.setAttribute('height', this.cropData.h.toString());

            // 16 is the half-size of drag
            drag.setAttribute('x', (this.cropData.x + this.cropData.w / 2 - 16).toString());
            drag.setAttribute('y', (this.cropData.y + this.cropData.h - 16).toString());

            // 40px is the width of zoom control + 20px to the edge of the canvas
            zoom.setAttribute('x', (this.imgW - 20 - 40).toString());
            // 200px is the height of the zoom control
            zoom.setAttribute('y', ((this.imgH - 200) / 2 ).toString());
        }

        private moveZoomKnobBy(delta: number) {
            var zoomLineEl = this.zoomLine.getHTMLElement(),
                zoomKnobEl = this.zoomKnob.getHTMLElement();

            var sliderStart = parseInt(zoomLineEl.getAttribute('y1')),
                sliderEnd = parseInt(zoomLineEl.getAttribute('y2')),
                sliderLength = sliderEnd - sliderStart,
                knobY = parseInt(zoomKnobEl.getAttribute('cy')),
                knobNewY = Math.max(sliderStart, Math.min(sliderEnd, knobY + delta));

            if (knobNewY != knobY) {
                zoomKnobEl.setAttribute('cy', knobNewY.toString());

                var knobDeltaPct = (knobNewY - knobY) / sliderLength,
                    newW = this.restrainWidth(this.cropData.w + this.imgW * knobDeltaPct),
                    newH = this.restrainHeight(this.cropData.h + this.imgH * knobDeltaPct),
                    newX = this.cropData.x - (newW - this.cropData.w) / 2,
                    newY = this.cropData.y - (newH - this.cropData.h) / 2;

                this.setCropPositionPx({
                    x: this.restrainWidth(newX, newW),
                    y: this.restrainHeight(newY, newH),
                    w: newW,
                    h: newH
                });
            }
        }

        private moveZoomKnobTo(rect: Rect) {
            var zoomKnobEl = this.zoomKnob.getHTMLElement(),
                zoomLineEl = this.zoomLine.getHTMLElement();

            var sliderStart = parseInt(zoomLineEl.getAttribute('y1')),
                sliderEnd = parseInt(zoomLineEl.getAttribute('y2')),
                sliderLength = sliderEnd - sliderStart,
                knobPct = rect.w / this.imgW,
                knobY = parseInt(zoomKnobEl.getAttribute('cy')),
                knobNewY = Math.max(sliderStart, Math.min(sliderEnd, sliderStart + knobPct * sliderLength));

            if (knobNewY != knobY) {
                zoomKnobEl.setAttribute('cy', knobNewY.toString());

                var newW = this.restrainWidth(rect.w),
                    newH = this.restrainHeight(rect.h);

                this.setCropPositionPx({
                    x: this.restrainWidth(rect.x, newW),
                    y: this.restrainHeight(rect.y, newH),
                    w: newW,
                    h: newH
                });
            }
        }

        private isInsideCropRect(x: number, y: number) {
            return x >= this.cropData.x && x <= this.cropData.x + this.cropData.w &&
                   y >= this.cropData.y && y <= this.cropData.y + this.cropData.h;

        }

        private isInsideCanvas(x: number, y: number) {
            return x >= 0 && x <= this.imgW && y >= 0 && y <= this.imgH;
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

                this.moveZoomKnobBy(delta);
            };
            this.clip.onMouseWheel(this.mouseWheelListener);

            this.mouseDownListener = (event: MouseEvent) => {
                var x = this.getOffsetX(event),
                    y = this.getOffsetY(event);

                if (this.isInsideCropRect(x, y)) {
                    panMouseDown = true;
                    lastPos = {
                        x: x,
                        y: y
                    };
                }
            };
            this.clip.onMouseDown(this.mouseDownListener);

            this.mouseMoveListener = (event: MouseEvent) => {

                var currPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };

                if (!this.isInsideCanvas(currPos.x, currPos.y)) {
                    // only act when mouse is inside canvas
                    return;
                }

                if (zoomMouseDown) {

                    this.moveZoomKnobBy(this.getOffsetY(event) - lastPos.y);

                } else if (dragMouseDown) {

                    var deltaY = this.getOffsetY(event) - lastPos.y,
                        newH = this.cropData.h + deltaY;

                    if (newH > 0) {

                        this.setCropPositionPx({
                            x: this.cropData.x,
                            y: this.cropData.y,
                            w: this.cropData.w,
                            h: this.restrainHeight(newH, this.cropData.y)
                        });
                    }
                } else if (panMouseDown) {

                    this.setCropPositionPx({
                        x: this.restrainWidth(this.cropData.x + currPos.x - lastPos.x, this.cropData.w),
                        y: this.restrainHeight(this.cropData.y + currPos.y - lastPos.y, this.cropData.h),
                        w: this.cropData.w,
                        h: this.cropData.h
                    });

                }

                lastPos = currPos;
            };
            api.dom.Body.get().onMouseMove(this.mouseMoveListener);

            this.mouseUpListener = (event: MouseEvent) => {
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
        onCropModeChanged(listener: (edit: boolean, position: Rect) => void) {
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

        private notifyCropModeChanged(edit: boolean, position: Rect) {
            var normalizedPosition;
            if (position) {
                // position can be undefined when auto positioned
                normalizedPosition = this.normalizeRect(position);
            }
            this.cropEditModeListeners.forEach((listener) => {
                listener(edit, normalizedPosition);
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