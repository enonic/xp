module api.ui.image {

    import ImgEl = api.dom.ImgEl;
    import DivEl = api.dom.DivEl;
    import Button = api.ui.button.Button;

    export class ImageEditor extends api.dom.DivEl {

        private canvas: DivEl;
        private image: ImgEl;
        private clip: api.dom.Element;

        private position: {x: number; y: number} = {x: 0, y: 0};
        private revertToPosition: {x: number; y: number};
        private autoPositioned: boolean;
        private revertAutoPositioned: boolean;

        private imgW: number;
        private imgH: number;
        private imgR: number;

        private mouseUpListener;
        private mouseMoveListener;

        private buttonsContainer: DivEl;
        private focalButtonsContainer: DivEl;
        private cropButtonsContainer: DivEl;

        private focalPointButton: Button;
        private cropButton: Button;

        private positionChangedListeners: {(position: {x: number; y: number}): void}[] = [];
        private autoPositionedChangedListeners: {(autoPositioned: boolean): void}[] = [];
        private radiusChangedListeners: {(r: number): void}[] = [];
        private focusEditModeListeners: {(edit: boolean, position: {x: number; y:number}): void}[] = [];

        constructor(src?: string) {
            super('image-editor');

            this.canvas = new DivEl('image-canvas');

            this.image = new ImgEl(null, 'image-image');
            this.image.onLoaded((event: UIEvent) => this.updateImageDimensions());

            var clipHtml = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="image-clip">' +
                           '    <clipPath id="clipPath">' +
                           '        <circle cx="50" cy="50" r="50" class="clip-circle"/>' +
                           '    </clipPath>' +
                           '    <image width="100" height="100" xlink:href="' + ImgEl.PLACEHOLDER + '" clip-path="url(#clipPath)"/>' +
                           '    <circle cx="50" cy="50" r="50" stroke="red" fill-opacity="0" stroke-width="4" class="stroke-circle"/>' +
                           '</svg>';

            this.clip = api.dom.Element.fromString(clipHtml, false);
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

        /**
         * Converts position from px to %
         * @param position
         * @returns {{x: number, y: number}}
         */
        private normalizePosition(position: {x: number; y: number}): {x: number; y: number} {
            return {
                x: position.x / this.imgW,
                y: position.y / this.imgH
            }
        }

        /**
         * Converts position from % to px
         * @param x
         * @param y
         * @returns {{x: number, y: number}}
         */
        private denormalizePosition(x: number, y: number): {x: number; y: number} {
            return {
                x: x * this.imgW,
                y: y * this.imgH
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
            var autoPositioned = this.autoPositioned;
            this.setFocusRadius(0.25);
            this.setFocusAutoPositioned(autoPositioned);

            if (this.isImageLoaded() && this.revertToPosition) {
                // position was set while image was not yet loaded
                this.setFocusPosition(this.revertToPosition.x, this.revertToPosition.y);
                this.revertToPosition = undefined;
            } else if (this.autoPositioned) {
                // set position to center by default
                this.resetFocusPosition();
            }

            var image = <Element> this.clip.getHTMLElement().getElementsByTagName('image')[0];
            image.setAttribute('width', this.imgW.toString());
            image.setAttribute('height', this.imgH.toString());

            if (this.image.isLoaded()) {
                this.updateMaskPosition();
            }
        }

        private updateMaskPosition() {
            var clipEl = this.clip.getHTMLElement(),
                circles = clipEl.getElementsByTagName('circle');

            for (var i = 0; i < circles.length; i++) {
                var circle = <Element> circles[i];
                circle.setAttribute('r', this.imgR.toString());
                circle.setAttribute('cx', this.position.x.toString());
                circle.setAttribute('cy', this.position.y.toString());
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
            this.toggleClass('edit-focus', edit);
            this.setShaderVisible(edit);

            this.buttonsContainer.setVisible(!edit);
            this.focalButtonsContainer.setVisible(edit);

            if (edit) {
                this.bindFocusMouseListeners();
                this.revertToPosition = this.getFocusPositionPx();
                this.revertAutoPositioned = this.autoPositioned;
                // update mask position in case it was updated during stand by
                this.updateMaskPosition();
            } else {
                this.unbindFocusMouseListeners();
                if (!applyChanges) {
                    this.setFocusPositionPx(this.revertToPosition);
                    this.setFocusAutoPositioned(this.revertAutoPositioned);
                }
                this.revertToPosition = undefined;
                this.revertAutoPositioned = undefined;
            }
            // notify position updated in case we exit edit mode and apply changes
            this.notifyFocusEditModeChanged(edit, !edit && applyChanges ? this.position : undefined);
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
                // placeholder image is 1x1 so don't count it
                this.setFocusPositionPx(this.denormalizePosition(x, y));
            } else {
                // use revert position to temporary save values until the image is loaded
                this.revertToPosition = {
                    x: x,
                    y: y
                }
            }
        }

        private setFocusPositionPx(position: {x: number; y: number}) {
            var oldX = this.position.x,
                oldY = this.position.y;

            this.position.x = this.restrainWidth(position.x);
            this.position.y = this.restrainHeight(position.y);
            this.setFocusAutoPositioned(false);

            if (oldX != this.position.x || oldY != this.position.y) {
                this.notifyFocusPositionChanged(this.position);

                if (this.image.isLoaded()) {
                    this.updateMaskPosition();
                }
            }
        }

        /**
         * Returns the center of the focal point as 0-1 values
         * @returns {{x, y}|{x: number, y: number}}
         */
        getFocusPosition(): {x: number; y: number} {
            return this.normalizePosition(this.getFocusPositionPx());
        }

        private getFocusPositionPx(): {x: number; y: number} {
            return {
                x: this.position.x,
                y: this.position.y
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
            var oldR = this.imgR;
            this.imgR = r;
            this.setFocusAutoPositioned(false);

            if (oldR != this.imgR) {
                this.notifyFocusRadiusChanged(this.imgR);

                if (this.image.isLoaded()) {
                    this.updateMaskPosition();
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
            return this.imgR;
        }

        private setFocusAutoPositioned(auto: boolean) {
            var autoPositionedChanged = this.autoPositioned != auto;
            this.autoPositioned = auto;
            this.focalPointButton.toggleClass('manual', !auto);
            if (autoPositionedChanged) {
                this.notifyFocusAutoPositionedChanged(auto);
            }
        }

        private bindFocusMouseListeners() {
            var mouseDown: boolean = false;
            var lastPos: {x: number; y: number};

            this.clip.onMouseDown((event: MouseEvent) => {
                mouseDown = true;
                lastPos = {x: this.getOffsetX(event), y: this.getOffsetY(event)};
            });

            this.mouseMoveListener = (event: MouseEvent) => {
                if (mouseDown) {
                    var restrainedPos = {
                        x: this.restrainWidth(this.position.x + this.getOffsetX(event) - lastPos.x),
                        y: this.restrainHeight(this.position.y + this.getOffsetY(event) - lastPos.y)
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

            this.onFocusAutoPositionedChanged((autoPositioned) => {
                resetButton.setEnabled(!autoPositioned);
                setFocusButton.setEnabled(!autoPositioned);
            });

            var focalButtonsContainer = new DivEl('edit-container');
            focalButtonsContainer.setVisible(false).appendChildren(setFocusButton, resetButton, cancelButton);

            return focalButtonsContainer;
        }

        /*
         *  Crop related methods
         */

        setCropEditMode(edit: boolean, applyChanges: boolean = true) {
            this.toggleClass('edit-crop', edit);
            this.setShaderVisible(edit);

            this.buttonsContainer.setVisible(!edit);
            this.cropButtonsContainer.setVisible(edit);
        }

        isCropEditMode(): boolean {
            return this.hasClass('edit-crop');
        }

        private createCropButtonsContainer(): DivEl {
            var cropButton = new Button('Crop');
            cropButton.setEnabled(false).addClass('blue').onClicked((event: MouseEvent) => this.setCropEditMode(false));

            var resetButton = new Button('Reset');
            resetButton.setEnabled(false).addClass('red').onClicked((event: MouseEvent) => {
                console.log('crop reset');
            });

            var cancelButton = new Button('Cancel');
            cancelButton.onClicked((event: MouseEvent) => this.setCropEditMode(false, false));

            this.onFocusAutoPositionedChanged((autoPositioned) => {
                resetButton.setEnabled(!autoPositioned);
                cropButton.setEnabled(!autoPositioned);
            });

            var cropButtonsContainer = new DivEl('edit-container');
            cropButtonsContainer.setVisible(false).appendChildren(cropButton, resetButton, cancelButton);
            return cropButtonsContainer;

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
        onFocusEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focusEditModeListeners.push(listener);
        }

        /**
         * Unbind listener from focus edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        unFocusEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.focusEditModeListeners = this.focusEditModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyFocusEditModeChanged(edit: boolean, position: {x: number; y: number}) {
            var normalizedPosition;
            if (position) {
                // position can be undefined when auto positioned
                normalizedPosition = this.normalizePosition(position);
            }
            this.focusEditModeListeners.forEach((listener) => {
                listener(edit, normalizedPosition);
            })
        }

        onFocusAutoPositionedChanged(listener: (autoPositioned: boolean) => void) {
            this.autoPositionedChangedListeners.push(listener);
        }

        unFocusAutoPositionedChanged(listener: (autoPositioned: boolean) => void) {
            this.autoPositionedChangedListeners = this.autoPositionedChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusAutoPositionedChanged(autoPositioned: boolean) {
            this.autoPositionedChangedListeners.forEach((listener) => {
                listener(autoPositioned);
            })
        }

        onFocusPositionChanged(listener: (position: {x: number; y: number}) => void) {
            this.positionChangedListeners.push(listener);
        }

        unFocusPositionChanged(listener: (position: {x: number; y: number}) => void) {
            this.positionChangedListeners = this.positionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusPositionChanged(position: {x: number; y: number}) {
            var normalizedPosition = this.normalizePosition(position);
            this.positionChangedListeners.forEach((listener) => {
                listener(normalizedPosition);
            })
        }

        onFocusRadiusChanged(listener: (r: number) => void) {
            this.radiusChangedListeners.push(listener);
        }

        unFocusRadiusChanged(listener: (r: number) => void) {
            this.radiusChangedListeners = this.radiusChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocusRadiusChanged(r: number) {
            var normalizedRadius = this.normalizeRadius(r);
            this.radiusChangedListeners.forEach((listener) => {
                listener(normalizedRadius);
            })
        }
    }

}