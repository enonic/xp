module api.ui.image {

    import ImgEl = api.dom.ImgEl;
    import DivEl = api.dom.DivEl;

    export class FocalEditor extends api.dom.DivEl {

        private canvas: DivEl;
        private image: ImgEl;
        private clip: api.dom.Element;

        private position: {x: number; y: number} = {x: 0, y: 0};
        private revertToPosition: {x: number; y: number};
        private autoPositioned: boolean = true;

        private imgW: number;
        private imgH: number;
        private imgR: number;

        private mouseDownListener;
        private mouseMoveListener;

        private focalPointButton: api.ui.button.Button;
        private setFocusButton: api.ui.button.Button;
        private cancelButton: api.ui.button.Button;

        private positionChangedListeners: {(x: number, y: number): void}[] = [];
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

            this.bindDragListeners();

            if (src) {
                this.setSrc(src);
            }
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

        onEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.editModeListeners.push(listener);
        }

        unEditModeChanged(listener: (edit: boolean, position: {x: number; y: number}) => void) {
            this.editModeListeners = this.editModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyEditModeChanged(edit: boolean, position: {x: number; y: number}) {
            this.editModeListeners.forEach((listener) => {
                listener(edit, position);
            })
        }

        setEditMode(edit: boolean, applyChanges: boolean = true) {
            this.toggleClass('edit', edit);

            this.focalPointButton.setVisible(!edit);
            this.setFocusButton.setVisible(edit);
            this.cancelButton.setVisible(edit);

            if (edit) {
                this.revertToPosition = this.getPosition();
                this.updateMaskPosition();
            } else {
                if (!applyChanges) {
                    var auto = this.autoPositioned;
                    this.setPosition(this.revertToPosition.x, this.revertToPosition.y);
                    this.autoPositioned = auto;
                }
                this.revertToPosition = undefined;
            }

            this.notifyEditModeChanged(edit, this.position);
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

        setPosition(x: number, y: number) {
            var oldX = this.position.x,
                oldY = this.position.y;

            this.position.x = this.restrainWidth(x);
            this.position.y = this.restrainHeight(y);
            this.autoPositioned = false;

            if (oldX != this.position.x || oldY != this.position.y) {
                this.notifyPositionChanged(this.position);

                if (this.image.isLoaded()) {
                    this.updateMaskPosition();
                }
            }
        }

        getPosition(): {x: number; y: number} {
            // return new object to prevent modification
            return {
                x: this.position.x,
                y: this.position.y
            }
        }

        resetPosition() {
            this.setPosition(this.imgW / 2, this.imgH / 2);
            this.autoPositioned = true;
        }

        setRadius(r: number) {
            var oldR = this.imgR;
            this.imgR = r;
            this.autoPositioned = false;

            if (oldR != this.imgR) {
                this.notifyRadiusChanged(this.imgR);

                if (this.image.isLoaded()) {
                    this.updateMaskPosition();
                }
            }
        }

        getRadius(): number {
            return this.imgR;
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

            var min = Math.min(this.imgW, this.imgH);
            this.imgR = min / 4;

            this.notifyRadiusChanged(this.imgR);

            var image = <Element> this.clip.getHTMLElement().getElementsByTagName('image')[0];
            image.setAttribute('width', this.imgW.toString());
            image.setAttribute('height', this.imgH.toString());

            if (this.autoPositioned) {
                this.resetPosition();
            }

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

        private bindDragListeners() {
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
                    this.setPosition(restrainedPos.x, restrainedPos.y);
                    lastPos = restrainedPos;
                }
            };
            api.dom.Body.get().onMouseMove(this.mouseMoveListener);

            this.mouseDownListener = (event: MouseEvent) => {
                mouseDown = false;
            };
            api.dom.Body.get().onMouseUp(this.mouseDownListener);
        }

        private unbindDragListeners() {
            api.dom.Body.get().unMouseMove(this.mouseMoveListener);
            api.dom.Body.get().unMouseUp(this.mouseDownListener);
        }

        public onPositionChanged(listener: (x: number, y: number) => void) {
            this.positionChangedListeners.push(listener);
        }

        public unPositionChanged(listener: (x: number, y: number) => void) {
            this.positionChangedListeners = this.positionChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyPositionChanged(position: {x: number; y: number}) {
            this.positionChangedListeners.forEach((listener) => {
                listener(position.x, position.y);
            })
        }

        public onRadiusChanged(listener: (r: number) => void) {
            this.radiusChangedListeners.push(listener);
        }

        public unRadiusChanged(listener: (r: number) => void) {
            this.radiusChangedListeners = this.radiusChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyRadiusChanged(r: number) {
            this.radiusChangedListeners.forEach((listener) => {
                listener(r);
            })
        }
    }

}