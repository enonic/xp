module api.ui.image {

    import ImgEl = api.dom.ImgEl;
    import DivEl = api.dom.DivEl;
    import Button = api.ui.button.Button;

    export class ImageEditor extends api.dom.DivEl {

        private canvas: DivEl;
        private image: ImgEl;
        private clip: api.dom.Element;

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

        private buttonsContainer: DivEl;
        private focalButtonsContainer: DivEl;
        private cropButtonsContainer: DivEl;

        private focalPointButton: Button;
        private cropButton: Button;

        private focusPositionChangedListeners: {(position: {x: number; y: number}): void}[] = [];
        private autoFocusChangedListeners: {(auto: boolean): void}[] = [];
        private focusRadiusChangedListeners: {(r: number): void}[] = [];
        private focusEditModeListeners: {(edit: boolean, position: {x: number; y:number}): void}[] = [];

        private cropPositionChangedListeners: {(position: {x: number; y: number; w: number; h: number}): void}[] = [];
        private autoCropChangedListeners: {(auto: boolean): void}[] = [];
        private cropEditModeListeners: {(edit: boolean, position: {x: number; y:number; w: number; h: number}): void}[] = [];

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
                           '        <circle cx="0" cy="0" r="0" stroke="red" fill-opacity="0" stroke-width="4" class="stroke-circle"/>' +
                           '    </g>' +
                           '    <g class="edit-group crop-group">' +
                           '        <clipPath id="cropClipPath">' +
                           '            <rect x="0" y="0" width="0" height="0"/>' +
                           '        </clipPath>' +
                           '        <svg id="drag-handle">' +
                           '            <defs>' +
                           '                <polygon id="drag-triangle" class="drag-triangle" points="8,0,16,8,0,8"/>' +
                           '            </defs>' +
                           '            <circle cx="16" cy="16" r="16"/>' +
                           '            <use xlink:href="#drag-triangle" x="8" y="6"/>' +
                           '            <use xlink:href="#drag-triangle" x="8" y="18" transform="rotate(180, 16, 22)"/>' +
                           '        </svg>' +
                           '    </g>' +
                           '</svg>';

            this.clip = api.dom.Element.fromString(clipHtml);
            this.canvas.appendChildren(this.image, this.clip);

            this.appendChildren(this.canvas, this.createToolbar());

            if (src) {
                this.setSrc(src);
            }

            this.setFocusAutoPositioned(true);
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
            var image = <Element> this.clip.getHTMLElement().getElementsByTagName('image')[0];
            image.setAttribute('xlink:href', src);
        }

        getSrc(): string {
            return this.image.getSrc();
        }

        private setImageClipPath(path: string) {
            var image = <Element> this.clip.getHTMLElement().getElementsByTagName('image')[0];
            image.setAttribute('clip-path', 'url(#' + path + ')');
        }

        /**
         * Converts point from px to %
         * @param point
         * @returns {{x: number, y: number}}
         */
        private normalizePoint(point: {x: number; y: number}): {x: number; y: number} {
            return {
                x: point.x / this.imgW,
                y: point.y / this.imgH
            }
        }

        /**
         * Converts point from % to px
         * @param x
         * @param y
         * @returns {{x: number, y: number}}
         */
        private denormalizePoint(x: number, y: number): {x: number; y: number} {
            return {
                x: x * this.imgW,
                y: y * this.imgH
            }
        }

        /**
         * Converts rectangle from px to %
         * @param x
         * @param y
         * @param w
         * @param h
         * @returns {{x: number, y: number, w: number, h: number}}
         */
        private normalizeRect(rect: {x: number; y: number; w: number; h: number}): {x: number; y: number; w: number; h: number} {
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
         * @returns {{x: number, y: number, w: number, h: number}}
         */
        private denormalizeRect(x: number, y: number, w: number, h: number): {x: number; y: number; w: number; h: number} {
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
         * @returns {number}
         */
        private normalizeRadius(r: number): number {
            return r / Math.min(this.imgW, this.imgH);
        }

        /**
         * Converts radius from % of the smallest dimension to px
         * @param r
         * @returns {number}
         */
        private denormalizeRadius(r: number): number {
            return r * Math.min(this.imgW, this.imgH);
        }

        private restrainWidth(x: number) {
            return Math.max(0, Math.min(this.imgW, x));
        }

        private restrainHeight(y: number) {
            return Math.max(0, Math.min(this.imgH, y));
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

            var image = <Element> this.clip.getHTMLElement().getElementsByTagName('image')[0];
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
            var shader = api.liveedit.Shader.get();
            if (visible) {
                shader.shade(this);
            } else {
                shader.hide();
            }
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

        private setFocusPositionPx(position: {x: number; y: number}) {
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
         * @returns {{x, y}|{x: number, y: number}}
         */
        getFocusPosition(): {x: number; y: number} {
            return this.normalizePoint(this.getFocusPositionPx());
        }

        private getFocusPositionPx(): {x: number; y: number} {
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
            var lastPos: {x: number; y: number};

            this.clip.onMouseDown((event: MouseEvent) => {
                mouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
            });

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
            var focusGroup = <Element> this.clip.getHTMLElement().getElementsByClassName('focus-group')[0],
                circles = focusGroup.getElementsByTagName('circle');

            for (var i = 0; i < circles.length; i++) {
                var circle = <Element> circles[i];
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
                    this.setCropPositionPx(this.revertCropData);
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
         * Sets the center of the focal point
         * @param x horizontal value in 0-1 interval
         * @param y vertical value in 0-1 interval
         * @returns {undefined}
         */
        setCropPosition(x: number, y: number, w: number, h: number) {
            if (this.isImageLoaded()) {
                this.setCropPositionPx(this.denormalizeRect(x, y, w, h));
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

        private setCropPositionPx(crop: {x: number; y: number; w: number; h: number}) {
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
         * Returns the center of the focal point as 0-1 values
         * @returns {{x, y}|{x: number, y: number}}
         */
        getCropPosition(): {x: number; y: number; w: number; h: number} {
            return this.normalizeRect(this.getCropPositionPx());
        }

        private getCropPositionPx(): {x: number; y: number; w: number; h: number} {
            return {
                x: this.cropData.x,
                y: this.cropData.y,
                w: this.cropData.w,
                h: this.cropData.h
            }
        }

        resetCropPosition() {
            this.setCropPosition(0, 0, 1, 1);
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
            var cropGroup = <Element> this.clip.getHTMLElement().getElementsByClassName('crop-group')[0],
                rect = <Element> cropGroup.getElementsByTagName('rect')[0],
                drag = <Element> cropGroup.getElementsByTagName('svg')[0];

            rect.setAttribute('x', this.cropData.x.toString());
            rect.setAttribute('y', this.cropData.y.toString());
            rect.setAttribute('width', this.cropData.w.toString());
            rect.setAttribute('height', this.cropData.h.toString());

            // 16 is the half-size of drag
            drag.setAttribute('x', (this.cropData.x + this.cropData.w / 2 - 16).toString());
            drag.setAttribute('y', (this.cropData.y + this.cropData.h - 16).toString());
        }

        private bindCropMouseListeners() {
            var mouseDown: boolean = false;
            var lastPos: {x: number; y: number};

            var dragHandle = this.clip.findChildById('drag-handle', true);

            dragHandle.onMouseDown((event: MouseEvent) => {
                mouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
                dragHandle.addClass('active');
            });

            this.mouseMoveListener = (event: MouseEvent) => {
                if (mouseDown) {
                    var deltaY = this.getOffsetY(event) - lastPos.y,
                        deltaX = this.imgW * deltaY / this.imgH,        // scale x proportionally
                        newW = this.cropData.w + deltaX * 2,
                        newH = this.cropData.h + deltaY * 2,
                        newX = this.cropData.x - deltaX,
                        newY = this.cropData.y - deltaY;

                    if (newW > 0 && newH > 0) {

                        this.setCropPositionPx({
                            x: this.restrainWidth(newX),
                            y: this.restrainHeight(newY),
                            w: this.restrainWidth(newW),
                            h: this.restrainHeight(newH)
                        });

                        lastPos = {
                            x: this.getOffsetX(event),
                            y: this.getOffsetY(event)
                        };
                    }
                }
            };
            api.dom.Body.get().onMouseMove(this.mouseMoveListener);

            this.mouseUpListener = (event: MouseEvent) => {
                if (mouseDown) {
                    mouseDown = false;
                    dragHandle.removeClass('active');
                }
            };
            api.dom.Body.get().onMouseUp(this.mouseUpListener);
        }

        private unbindCropMouseListeners() {
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
        onFocusModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focusEditModeListeners.push(listener);
        }

        /**
         * Unbind listener from focus edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        unFocusModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focusEditModeListeners = this.focusEditModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyFocusModeChanged(edit: boolean, position: {x: number; y: number}) {
            var normalizedPosition;
            if (position) {
                // position can be undefined when auto positioned
                normalizedPosition = this.normalizePoint(position);
            }
            this.focusEditModeListeners.forEach((listener) => {
                listener(edit, normalizedPosition);
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

        onFocusPositionChanged(listener: (position: {x: number; y: number}) => void) {
            this.focusPositionChangedListeners.push(listener);
        }

        unFocusPositionChanged(listener: (position: {x: number; y: number}) => void) {
            this.focusPositionChangedListeners = this.focusPositionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusPositionChanged(position: {x: number; y: number}) {
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
        onCropModeChanged(listener: (edit: boolean, position: {x: number; y: number; w: number; h: number}) => void) {
            this.cropEditModeListeners.push(listener);
        }

        /**
         * Unbind listener from crop edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        unCropModeChanged(listener: (edit: boolean, position: {x: number; y: number; w: number; h: number}) => void) {
            this.cropEditModeListeners = this.cropEditModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyCropModeChanged(edit: boolean, position: {x: number; y: number; w: number; h: number}) {
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

        onCropPositionChanged(listener: (position: {x: number; y: number; w: number; h: number}) => void) {
            this.cropPositionChangedListeners.push(listener);
        }

        unCropPositionChanged(listener: (position: {x: number; y: number; w: number; h: number}) => void) {
            this.cropPositionChangedListeners = this.cropPositionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyCropPositionChanged(position: {x: number; y: number; w: number; h: number}) {
            var normalizedPosition = this.normalizeRect(position);
            this.cropPositionChangedListeners.forEach((listener) => {
                listener(normalizedPosition);
            })
        }

    }

}