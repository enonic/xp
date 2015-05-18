module api.ui.image {

    import ImgEl = api.dom.ImgEl;
    import DivEl = api.dom.DivEl;

    export class FocalEditor extends api.dom.DivEl {

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

        private focalPointButton: api.ui.button.Button;
        private setFocusButton: api.ui.button.Button;
        private cancelButton: api.ui.button.Button;

        private positionChangedListeners: {(position: {x: number; y: number}): void}[] = [];
        private radiusChangedListeners: {(r: number): void}[] = [];
        private editModeListeners: {(edit: boolean, position: {x: number; y:number}): void}[] = [];

        constructor(src?: string) {
            super('focal-editor');

            this.canvas = new DivEl('focal-canvas');

            this.image = new ImgEl(null, 'focal-image');
            this.image.onLoaded((event: UIEvent) => this.updateImageDimensions());

            var clipHtml = '<svg xmlns="http://www.w3.org/2000/svg" version="1.1" class="focal-clip">' +
                           '    <clipPath id="clipPath">' +
                           '        <circle cx="50" cy="50" r="50" class="clip-circle"/>' +
                           '    </clipPath>' +
                           '    <image width="100" height="100" xlink:href="' + ImgEl.PLACEHOLDER + '" clip-path="url(#clipPath)"/>' +
                           '    <circle cx="50" cy="50" r="50" stroke="red" fill-opacity="0" stroke-width="4" class="stroke-circle"/>' +
                           '</svg>';

            this.clip = api.dom.Element.fromString(clipHtml, false);
            this.canvas.appendChildren(this.image, this.clip);

            this.appendChildren(this.canvas, this.createToolbar());

            this.bindMouseListeners();

            if (src) {
                this.setSrc(src);
            }

            this.setAutoPositioned(true);
        }

        setEditMode(edit: boolean, applyChanges: boolean = true) {
            this.toggleClass('edit', edit);

            this.focalPointButton.setVisible(!edit);
            this.setFocusButton.setVisible(edit);
            this.cancelButton.setVisible(edit);

            if (edit) {
                this.revertToPosition = this.getPositionPx();
                this.revertAutoPositioned = this.autoPositioned;
                // update mask position in case it was updated during stand by
                this.updateMaskPosition();
            } else {
                if (!applyChanges) {
                    this.setPositionPx(this.revertToPosition);
                    this.setAutoPositioned(this.revertAutoPositioned);
                }
                this.revertToPosition = undefined;
                this.revertAutoPositioned = undefined;
            }
            // notify position updated in case we exit edit mode and apply changes
            this.notifyEditModeChanged(edit, !edit && applyChanges ? this.position : undefined);
        }

        isEditMode(): boolean {
            return this.hasClass('edit');
        }

        remove(): FocalEditor {
            this.unbindDragListeners();
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
         * Sets the center of the focal point
         * @param x horizontal value in 0-1 interval
         * @param y vertical value in 0-1 interval
         * @returns {undefined}
         */
        setPosition(x: number, y: number) {
            if (this.isImageLoaded()) {
                // placeholder image is 1x1 so don't count it
                this.setPositionPx(this.denormalizePosition(x, y));
            } else {
                // use revert position to temporary save values until the image is loaded
                this.revertToPosition = {
                    x: x,
                    y: y
                }
            }
        }

        private isImageLoaded(): boolean {
            return this.image.isLoaded() && !this.image.isPlaceholder();
        }

        private setPositionPx(position: {x: number; y: number}) {
            var oldX = this.position.x,
                oldY = this.position.y;

            this.position.x = this.restrainWidth(position.x);
            this.position.y = this.restrainHeight(position.y);
            this.setAutoPositioned(false);

            if (oldX != this.position.x || oldY != this.position.y) {
                this.notifyPositionChanged(this.position);

                if (this.image.isLoaded()) {
                    this.updateMaskPosition();
                }
            }
        }

        /**
         * Returns the center of the focal point as 0-1 values
         * @returns {{x, y}|{x: number, y: number}}
         */
        getPosition(): {x: number; y: number} {
            return this.normalizePosition(this.getPositionPx());
        }

        private getPositionPx(): {x: number; y: number} {
            return {
                x: this.position.x,
                y: this.position.y
            }
        }

        resetPosition() {
            this.setPosition(0.5, 0.5);
            this.setAutoPositioned(true);
        }

        setRadius(r: number) {
            return this.setRadiusPx(this.denormalizeRadius(r));
        }

        private setRadiusPx(r: number) {
            var oldR = this.imgR;
            this.imgR = r;
            this.setAutoPositioned(false);

            if (oldR != this.imgR) {
                this.notifyRadiusChanged(this.imgR);

                if (this.image.isLoaded()) {
                    this.updateMaskPosition();
                }
            }
        }

        /**
         * Returns the radius normalized by smallest dimension
         * @returns {number}
         */
        getRadius(): number {
            return this.normalizeRadius(this.getRadiusPx());
        }

        private getRadiusPx(): number {
            return this.imgR;
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

        private setAutoPositioned(auto: boolean) {
            this.autoPositioned = auto;
            this.toggleClass('auto', auto);
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

        private updateImageDimensions() {
            var imgEl = this.image.getEl();
            this.imgW = imgEl.getNaturalWidth() + imgEl.getBorderLeftWidth() + imgEl.getBorderRightWidth() +
                        imgEl.getPaddingLeft() + imgEl.getPaddingRight();
            this.imgH = imgEl.getNaturalHeight() + imgEl.getBorderTopWidth() + imgEl.getBorderBottomWidth() +
                        imgEl.getPaddingTop() + imgEl.getPaddingBottom();

            // calculate radius first as it will be needed when setting position
            var autoPositioned = this.autoPositioned;
            this.setRadius(0.25);
            this.setAutoPositioned(autoPositioned);

            if (this.isImageLoaded() && this.revertToPosition) {
                // position was set while image was not yet loaded
                this.setPosition(this.revertToPosition.x, this.revertToPosition.y);
                this.revertToPosition = undefined;
            } else if (this.autoPositioned) {
                // set position to center by default
                this.resetPosition();
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

        private getPositionFromEvent(event: MouseEvent): {x: number; y: number} {
            return
        }

        private bindMouseListeners() {
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
                    this.setPositionPx(restrainedPos);

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
                    this.setPositionPx(restrainedPos);

                    mouseDown = false;
                }
            };
            api.dom.Body.get().onMouseUp(this.mouseUpListener);
        }

        private unbindDragListeners() {
            api.dom.Body.get().unMouseMove(this.mouseMoveListener);
            api.dom.Body.get().unMouseUp(this.mouseUpListener);
        }

        private createToolbar(): api.dom.DivEl {
            var toolbar = new api.dom.DivEl('focal-toolbar');

            this.focalPointButton = new api.ui.button.Button();
            this.focalPointButton.addClass('no-bg icon-center-focus-strong');
            this.focalPointButton.onClicked((event: MouseEvent) => {
                this.setEditMode(true);
            });
            toolbar.appendChild(this.focalPointButton);

            this.setFocusButton = new api.ui.button.Button('Set Focus');
            this.setFocusButton.addClass('blue');
            this.setFocusButton.setVisible(false);
            this.setFocusButton.onClicked((event: MouseEvent) => {
                this.setEditMode(false);
            });
            this.cancelButton = new api.ui.button.Button('Cancel');
            this.cancelButton.setVisible(false);
            this.cancelButton.onClicked((event: MouseEvent) => {
                this.setEditMode(false, false);
            });

            var pullRight = new api.dom.DivEl('pull-right');
            pullRight.appendChild(this.setFocusButton);
            pullRight.appendChild(this.cancelButton);
            toolbar.appendChild(pullRight);

            return toolbar;
        }

        /**
         * Bind listener to edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        onEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.editModeListeners.push(listener);
        }

        /**
         * Unbind listener from edit mode change
         * @param listener has following params:
         *  - edit - tells if we enter or exit edit mode
         *  - position - will be supplied if we exit edit mode and apply changes
         */
        unEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.editModeListeners = this.editModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyEditModeChanged(edit: boolean, position: {x: number; y: number}) {
            var normalizedPosition;
            if (position) {
                // position can be undefined when auto positioned
                normalizedPosition = this.normalizePosition(position);
            }
            this.editModeListeners.forEach((listener) => {
                listener(edit, normalizedPosition);
            })
        }

        onPositionChanged(listener: (position: {x: number; y: number}) => void) {
            this.positionChangedListeners.push(listener);
        }

        unPositionChanged(listener: (position: {x: number; y: number}) => void) {
            this.positionChangedListeners = this.positionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyPositionChanged(position: {x: number; y: number}) {
            var normalizedPosition = this.normalizePosition(position);
            this.positionChangedListeners.forEach((listener) => {
                listener(normalizedPosition);
            })
        }

        onRadiusChanged(listener: (r: number) => void) {
            this.radiusChangedListeners.push(listener);
        }

        unRadiusChanged(listener: (r: number) => void) {
            this.radiusChangedListeners = this.radiusChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyRadiusChanged(r: number) {
            var normalizedRadius = this.normalizeRadius(r);
            this.radiusChangedListeners.forEach((listener) => {
                listener(normalizedRadius);
            })
        }
    }

}