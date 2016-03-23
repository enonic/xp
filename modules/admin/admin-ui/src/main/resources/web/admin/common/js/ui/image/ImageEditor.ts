module api.ui.image {

    import ImgEl = api.dom.ImgEl;
    import DivEl = api.dom.DivEl;
    import Button = api.ui.button.Button;
    import Element = api.dom.Element;
    import TabMenu = api.ui.tab.TabMenu;
    import TabMenuItem = api.ui.tab.TabMenuItem;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import NavigatorEvent = api.ui.NavigatorEvent;

    export interface Point {
        x: number;
        y: number;
    }

    export interface Rect extends Point {
        x2: number;
        y2: number;
    }

    interface SVGRect extends Point {
        w: number;
        h: number;
    }

    interface FocusData extends Point {
        r: number;
        auto: boolean;
    }

    interface CropData extends SVGRect {
        auto: boolean;
    }

    interface ZoomData extends SVGRect {}

    export class ImageEditor extends api.dom.DivEl {

        private SCROLLABLE_SELECTOR: string = '.form-panel';
        private WIZARD_TOOLBAR_SELECTOR: string = '.wizard-step-navigator-and-toolbar';

        private frame: DivEl;
        private canvas: DivEl;
        private image: ImgEl;
        private clip: Element;
        private dragHandle: Element;
        private zoomContainer: Element;
        private zoomLine: Element;
        private zoomKnob: Element;
        private focusClipPath: Element;
        private cropClipPath: Element;

        private focusData: FocusData = {x: 0, y: 0, r: 0.25, auto: undefined};
        private revertFocusData: FocusData;

        private cropData: CropData = {x: 0, y: 0, w: 0, h: 0, auto: undefined};
        private revertCropData: CropData;

        private zoomData: ZoomData = {x: 0, y: 0, w: 0, h: 0};
        private revertZoomData: ZoomData;

        private imgW: number = 1;
        private imgH: number = 1;
        private frameW: number = 1;
        private frameH: number = 1;
        private maxZoom = 5;

        private mouseUpListener;
        private mouseMoveListener;
        private mouseDownListener;
        private dragMouseDownListener;
        private knobMouseDownListener;

        private stickyToolbar: DivEl;

        private editCropButton: Button;
        private editFocusButton: Button;
        private editResetButton: Button;
        private uploadButton: api.dom.ButtonEl;

        private editModeListeners: {(edit: boolean, position: Rect, zoom: Rect, focus: Point): void}[] = [];

        private focusPositionChangedListeners: {(position: Point): void}[] = [];
        private autoFocusChangedListeners: {(auto: boolean): void}[] = [];
        private focusRadiusChangedListeners: {(r: number): void}[] = [];

        private cropPositionChangedListeners: {(position: Rect): void}[] = [];
        private autoCropChangedListeners: {(auto: boolean): void}[] = [];
        private shaderVisibilityChangedListeners: {(visible: boolean): void}[] = [];

        private maskWheelListener: (event: WheelEvent) => void;
        private maskClickListener: (event: MouseEvent) => void;
        private maskHideListener: (event: api.dom.ElementHiddenEvent) => void;

        private imageErrorListeners: {(event: UIEvent): void}[] = [];

        private skipNextOutsideClick: boolean;

        public static debug = false;

        constructor(src?: string) {
            super('image-editor');

            this.frame = new DivEl('image-frame');
            this.canvas = new DivEl('image-canvas');

            this.image = new ImgEl(null, 'image-bg');

            let resizeListener = (item) => {
                if (this.isVisible()) {
                    this.updateImageDimensions(false, true);
                    this.updateStickyToolbar();
                }
            };

            let isFirstLoad = true;

            let updateImageOnShown: () => void = () => {
                this.updateImageDimensions(true);
                this.updateStickyToolbar();
                this.unShown(updateImageOnShown);
                if (isFirstLoad) {
                    api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, resizeListener);
                    isFirstLoad = false;
                }
            };

            let onLoaded = (event: UIEvent) => {
                // check that real image has been loaded
                if (this.isImageLoaded()) {
                    if (this.isVisible()) {
                        updateImageOnShown();
                    } else {
                        this.onShown(updateImageOnShown);
                    }
                }
            };

            this.image.onLoaded(onLoaded);

            let imageErrorHandler = (event: UIEvent) => {
                this.notifyImageError(event);
                this.remove();
            };

            this.image.onError(imageErrorHandler);

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
                           '    </g>' +
                           '</svg>';

            this.clip = Element.fromString(clipHtml);

            this.dragHandle = this.clip.findChildById(myId + '-dragHandle', true);
            this.focusClipPath = this.clip.findChildById(myId + '-focusClipPath', true);
            this.cropClipPath = this.clip.findChildById(myId + '-cropClipPath', true);

            // prevent FF image dragging
            this.clip.getHTMLElement().querySelector('image')['ondragstart'] = function () {
                return false
            };

            var imageMask = new api.dom.DivEl("image-bg-mask");

            this.canvas.appendChildren(imageMask, this.image, this.clip);

            this.frame.appendChild(this.canvas);

            this.stickyToolbar = this.createStickyToolbar();
            this.appendChildren(this.stickyToolbar, this.frame);

            var scrollListener = (event) => this.updateStickyToolbar();

            this.onAdded((event: api.dom.ElementAddedEvent) => {
                // sticky toolbar needs to have access to parent elements
                wemjq(this.getHTMLElement()).closest(this.SCROLLABLE_SELECTOR).bind("scroll", scrollListener);
            });
            this.onRemoved((event: api.dom.ElementRemovedEvent) => {
                // element has already been removed so use parent
                if (!!event.getParent()) {
                    wemjq(event.getParent().getHTMLElement()).closest(this.SCROLLABLE_SELECTOR).unbind("scroll", scrollListener);
                }
                api.ui.responsive.ResponsiveManager.unAvailableSizeChanged(this);
                this.unImageError(imageErrorHandler);
            });

            if (src) {
                this.setSrc(src);
            }

            this.setFocusAutoPositioned(true);
            this.setCropAutoPositioned(true);
        }

        isElementInsideButtonsContainer(el: HTMLElement): boolean {
            return this.stickyToolbar.getHTMLElement().contains(el);
        }

        getLastButtonInContainer(): Element {
            return this.uploadButton;
        }

        remove(): ImageEditor {
            if (this.isFocusEditMode()) {
                this.disableFocusEditMode(false);
            } else if (this.isCropEditMode()) {
                this.disableCropEditMode(false);
            }
            super.remove();
            return this;
        }

        setSrc(src: string) {
            this.image.setSrc(src);
            var image: HTMLElement = <HTMLElement> this.clip.getHTMLElement().querySelector('image');
            image.setAttribute('xlink:href', src);
        }

        getSrc(): string {
            return this.image.getSrc();
        }

        getImage(): ImgEl {
            return this.image;
        }

        getUploadButton(): api.dom.ButtonEl {
            return this.uploadButton;
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
            // focus point is calculated relative to crop area
            return {
                x: point.x / this.cropData.w,
                y: point.y / this.cropData.h
            }
        }

        /**
         * Converts point from % to px
         * @param x
         * @param y
         * @returns {Point} denormalized point
         */
        private denormalizePoint(x: number, y: number): Point {
            // focus point is calculated relative to crop area
            return {
                x: x * this.cropData.w,
                y: y * this.cropData.h
            }
        }

        /**
         * Converts rectangle from px to %
         * @param rect rectangle object to normalize
         * @returns {SVGRect} normalized to 0-1 rectangle
         */
        private normalizeRect(rect: SVGRect): SVGRect {
            var minW = this.frameW;
            var minH = this.frameH;
            return {
                x: rect.x / minW,
                y: rect.y / minH,
                w: rect.w / minW,
                h: rect.h / minH
            }
        }

        /**
         * Converts rectangle from % to px
         * @param rect
         * @returns {SVGRect} denormalized rectangle
         */
        private denormalizeRect(rect: SVGRect): SVGRect {
            var minW = this.frameW;
            var minH = this.frameH;
            return {
                x: rect.x * minW,
                y: rect.y * minH,
                w: rect.w * minW,
                h: rect.h * minH
            }
        }

        /**
         * Converts radius from px to % of the smallest dimension
         * @param r
         * @returns {number} normalized to 0-1 radius
         */
        private normalizeRadius(r: number): number {
            // focus radius is calculated relative to crop area
            return r / Math.min(this.cropData.w, this.cropData.h);
        }

        /**
         * Converts radius from % of the smallest dimension to px
         * @param r
         * @returns {number} denormalized radius
         */
        private denormalizeRadius(r: number): number {
            // focus radius is calculated relative to crop area
            return r * Math.min(this.cropData.w, this.cropData.h);
        }

        private getOffsetX(e: MouseEvent): number {
            return e.clientX - this.frame.getEl().getOffset().left;
        }

        private getOffsetY(e: MouseEvent): number {
            return e.clientY - this.frame.getEl().getOffset().top;
        }

        private isImageLoaded(): boolean {
            return this.image.isLoaded() && !this.image.isPlaceholder();
        }

        private updateImageDimensions(reset: boolean = false, scale: boolean = false) {
            var imgEl = this.image.getEl(),
                frameEl = this.frame.getEl();

            var zoomPct: SVGRect, cropPct: SVGRect, focusPosPct: Point, focusRadPct,
                revZoomPct: SVGRect, revCropPct: SVGRect, revFocusPosPct: Point, revFocusRadPct;

            if (scale) {
                // save all positions in percents before updating dimensions to scale them accordingly
                zoomPct = this.normalizeRect(this.getZoomPositionPx());
                cropPct = this.normalizeRect(this.getCropPositionPx());
                focusPosPct = this.normalizePoint(this.getFocusPositionPx());
                focusRadPct = this.normalizeRadius(this.getFocusRadiusPx());
                if (this.revertZoomData) {
                    revZoomPct = this.normalizeRect(this.revertZoomData);
                }
                if (this.revertCropData) {
                    revCropPct = this.normalizeRect(this.revertCropData);
                }
                if (this.revertFocusData) {
                    revFocusPosPct = this.normalizePoint(this.revertFocusData);
                    revFocusRadPct = this.normalizeRadius(this.revertFocusData.r);
                }
            }

            this.imgW = imgEl.getNaturalWidth();
            this.imgH = imgEl.getNaturalHeight();

            this.frameW = this.getEl().getWidth();
            this.frameH = (this.frameW * this.imgH) / this.imgW;

            frameEl.setWidthPx(this.frameW).setHeightPx(this.frameH);

            if (ImageEditor.debug) {
                console.group('ImageEditor.updateImageDimensions');
                console.log('Image loaded: ' + this.imgW + ' x ' + this.imgH + ', frame: ' + this.frameW + ' x ' + this.frameH);
            }

            if (reset) {
                if (this.revertZoomData) {
                    // zoom was set while images was not yet loaded (saved in px);
                    this.setZoomPositionPx(this.denormalizeRect(this.revertZoomData));

                    this.revertZoomData = undefined;
                } else if (this.cropData.auto) {
                    // use cropData.auto flag for zoom as well
                    this.resetZoomPosition();
                }

                // crop depends on zoom so init it after
                if (this.revertCropData) {
                    // crop was set while images was not yet loaded (saved in px);
                    this.setCropPositionPx(this.denormalizeRect(this.revertCropData));

                    this.revertCropData = undefined;
                } else if (this.cropData.auto) {
                    this.resetCropPosition();
                }

                // focus depends on zoom so init it after
                if (this.revertFocusData) {
                    // position was set while image was not yet loaded
                    this.setFocusPositionPx(this.denormalizePoint(
                        this.revertFocusData.x,
                        this.revertFocusData.y));

                    this.setFocusRadiusPx(this.denormalizeRadius(this.revertFocusData.r));

                    this.revertFocusData = undefined;
                } else if (this.focusData.auto) {
                    // set position to center by default
                    this.resetFocusPosition();
                    this.resetFocusRadius();
                }

                this.updateFocusMaskPosition();
                this.updateCropMaskPosition();
                this.updateZoomPosition();

            }

            if (scale) {
                // scale all positions accordingly to dimensions change in edit mode
                this.setZoomPositionPx(this.denormalizeRect(zoomPct), false);
                this.setCropPositionPx(this.denormalizeRect(cropPct), false);
                this.setFocusPositionPx(this.denormalizePoint(focusPosPct.x, focusPosPct.y), false);
                this.setFocusRadiusPx(this.denormalizeRadius(focusRadPct), false);

                //also update revert positions in case user will cancel changes
                if (revZoomPct) {
                    this.revertZoomData = this.denormalizeRect(revZoomPct);
                }
                if (revCropPct) {
                    var revCrop = this.denormalizeRect(revCropPct);
                    this.revertCropData = {
                        x: revCrop.x,
                        y: revCrop.y,
                        w: revCrop.w,
                        h: revCrop.h,
                        auto: this.revertCropData.auto
                    };
                }
                if (revFocusPosPct && revFocusRadPct) {
                    var revFocusPos = this.denormalizePoint(revFocusPosPct.x, revFocusPosPct.y);
                    var revFocusRad = this.denormalizeRadius(revFocusRadPct);
                    this.revertFocusData = {
                        x: revFocusPos.x,
                        y: revFocusPos.y,
                        r: revFocusRad,
                        auto: this.revertFocusData.auto
                    };
                }
            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        private updateFrameHeight() {
            this.frame.getEl().setHeightPx(this.cropData.h);
        }

        private isOutside(event: MouseEvent) {
            var el = this.getEl(),
                offset = el.getOffset(),
                bottom = offset.top + el.getHeightWithBorder(),
                right = offset.left + el.getWidthWithBorder(),
                scrollEl = wemjq(this.getHTMLElement()).closest(this.SCROLLABLE_SELECTOR),
                scrollOffset = scrollEl.length == 1 ? scrollEl.offset() : {
                    left: 0,
                    top: 0
                };

            return event.clientX < Math.max(scrollOffset.left, offset.left) ||
                   event.clientX > right ||
                   event.clientY < Math.max(scrollOffset.top, offset.top) ||
                   event.clientY > bottom;
        }

        private setShaderVisible(visible: boolean) {
            if (ImageEditor.debug) {
                console.log('setShaderVisible', visible);
            }

            var bodyMask = api.ui.mask.BodyMask.get();
            if (visible) {
                if (!this.maskClickListener) {
                    this.maskClickListener = (event: MouseEvent) => {

                        if (ImageEditor.debug) {
                            console.log('maskClickListener', event);
                        }

                        if (this.isOutside(event)) {
                            event.stopPropagation();
                            event.preventDefault();

                            if (this.skipNextOutsideClick) {
                                if (ImageEditor.debug) {
                                    console.log('maskClickListener, skipping mask click as requested earlier');
                                }
                                this.skipNextOutsideClick = false;
                                return;
                            }

                            if (this.isCropEditMode()) {
                                this.disableCropEditMode();
                            } else if (this.isFocusEditMode()) {
                                this.disableFocusEditMode();
                            }
                            bodyMask.hide();
                        }
                    };
                }
                api.dom.Body.get().onClicked(this.maskClickListener);

                if (!this.maskWheelListener) {
                    this.maskWheelListener = (event: WheelEvent) => {
                        var el = this.getEl(),
                            win = api.dom.WindowDOM.get(),
                            myHeight = el.getHeight(),
                            myTop = el.getTopPx(),
                            winHeight = win.getHeight();

                        var newTop = myTop - this.normalizeWheel(event).pixelY;

                        var newTopLimited;
                        var heightLimit = this.stickyToolbar.getEl().getHeight() + 100;
                        newTopLimited = Math.min(winHeight - heightLimit, Math.max(heightLimit - myHeight, newTop));
                        var isInsideLimit = newTop == newTopLimited;
                        if (!isInsideLimit && (Math.abs(newTop - newTopLimited) > Math.abs(myTop - newTopLimited))) {
                            // we are outside limit and trying to move away from it
                            // so keep my current position to prevent it
                            newTop = myTop
                        } else if (isInsideLimit) {
                            // we are inside limit where limits apply
                            newTop = newTopLimited
                        } else {
                            // we are outside the limit but moving towards the limit
                            // leave newTop untouched to allow it
                        }

                        if (newTop != myTop) {
                            el.setTopPx(newTop);
                            this.updateStickyToolbar();
                        }
                    };
                }
                api.dom.Body.get().onMouseWheel(this.maskWheelListener);

                if (!this.maskHideListener) {
                    this.maskHideListener = (event: api.dom.ElementHiddenEvent) => {
                        api.dom.Body.get().unClicked(this.maskClickListener);
                        api.dom.Body.get().unMouseWheel(this.maskWheelListener);
                        bodyMask.unHidden(this.maskHideListener);
                    }
                }
                bodyMask.onHidden(this.maskHideListener);

                bodyMask.addClass('opaque').show();
            } else {
                bodyMask.removeClass('opaque').hide();
            }

            this.notifyShaderVisibilityChanged(visible);
        }

        // Reasonable defaults
        private WHEEL_PIXEL_STEP = 10;
        private WHEEL_LINE_HEIGHT = 20;
        private WHEEL_PAGE_HEIGHT = 800;

        // https://github.com/facebook/fixed-data-table/blob/master/dist/fixed-data-table.js#L2052
        private normalizeWheel(event) {
            var sX = 0, sY = 0,       // spinX, spinY
                pX = 0, pY = 0;       // pixelX, pixelY

            // Legacy
            if ('detail'      in event) { sY = event.detail; }
            if ('wheelDelta'  in event) { sY = -event.wheelDelta / 120; }
            if ('wheelDeltaY' in event) { sY = -event.wheelDeltaY / 120; }
            if ('wheelDeltaX' in event) { sX = -event.wheelDeltaX / 120; }

            // side scrolling on FF with DOMMouseScroll
            if ('axis' in event && event.axis === event.HORIZONTAL_AXIS) {
                sX = sY;
                sY = 0;
            }

            pX = sX * this.WHEEL_PIXEL_STEP;
            pY = sY * this.WHEEL_PIXEL_STEP;

            if ('deltaY' in event) { pY = event.deltaY; }
            if ('deltaX' in event) { pX = event.deltaX; }

            if ((pX || pY) && event.deltaMode) {
                if (event.deltaMode == 1) {          // delta in LINE units
                    pX *= this.WHEEL_LINE_HEIGHT;
                    pY *= this.WHEEL_LINE_HEIGHT;
                } else {                             // delta in PAGE units
                    pX *= this.WHEEL_PAGE_HEIGHT;
                    pY *= this.WHEEL_PAGE_HEIGHT;
                }
            }

            // Fall-back if spin cannot be determined
            if (pX && !sX) { sX = (pX < 1) ? -1 : 1; }
            if (pY && !sY) { sY = (pY < 1) ? -1 : 1; }

            return {
                spinX: sX,
                spinY: sY,
                pixelX: pX,
                pixelY: pY
            };
        }

        private createStickyToolbar(): DivEl {
            var toolbar = new DivEl('sticky-toolbar');

            var editContainer = new DivEl('edit-container');

            this.editResetButton = new Button('Reset');
            this.editResetButton.setVisible(false).addClass('transparent').onClicked((event: MouseEvent) => {
                event.stopPropagation();

                if (this.isFocusEditMode()) {
                    this.resetFocusPosition();
                    this.resetFocusRadius();
                } else if (this.isCropEditMode()) {
                    this.resetZoomPosition();
                    this.resetCropPosition();
                }
            });

            var applyButton = new Button('Apply');
            applyButton.addClass('blue').onClicked((event: MouseEvent) => {
                event.stopPropagation();

                if (this.isCropEditMode()) {
                    this.disableCropEditMode();
                } else if (this.isFocusEditMode()) {
                    this.disableFocusEditMode();
                }
            });

            var cancelButton = new api.ui.button.CloseButton();
            cancelButton.onClicked((event: MouseEvent) => {
                event.stopPropagation();

                if (this.isCropEditMode()) {
                    this.disableCropEditMode(false);
                } else if (this.isFocusEditMode()) {
                    this.disableFocusEditMode(false);
                }
            });

            editContainer.appendChildren(this.editResetButton, applyButton, cancelButton);

            var standbyContainer = new DivEl('standby-container');
            var resetButton = new Button('Reset');
            resetButton.addClass('button-reset red').setVisible(false).onClicked((event: MouseEvent) => {
                event.stopPropagation();

                this.resetCropPosition();
                this.resetZoomPosition();
                this.resetFocusPosition();
                this.resetFocusRadius();
            });

            this.onFocusAutoPositionedChanged((auto) => {
                this.editResetButton.setVisible(!auto);
                this.toggleClass('autofocused', auto);
                resetButton.setVisible(!auto || !this.cropData.auto);
            });
            this.onCropAutoPositionedChanged((auto) => {
                this.editResetButton.setVisible(!auto);
                resetButton.setVisible(!auto || !this.focusData.auto);
            });

            this.uploadButton = new Button();
            this.uploadButton.addClass('button-upload');
            standbyContainer.appendChildren(resetButton, this.uploadButton);

            this.editCropButton = new Button();
            this.editCropButton.addClass('button-crop transparent icon-crop').onClicked((event: MouseEvent) => {
                event.stopPropagation();

                if (this.isCropEditMode()) {
                    this.disableCropEditMode();
                } else {
                    if (this.isFocusEditMode()) {
                        this.disableFocusEditMode(true, false);
                        this.enableCropEditMode(true, false);
                    } else {
                        this.enableCropEditMode();
                    }
                }
            });

            this.editFocusButton = new Button();
            this.editFocusButton.addClass('button-focus transparent icon-center_focus_strong').onClicked((event: MouseEvent) => {
                event.stopPropagation();

                if (this.isFocusEditMode()) {
                    this.disableFocusEditMode();
                } else {
                    if (this.isCropEditMode()) {
                        this.disableCropEditMode(true, false);
                        this.enableFocusEditMode(true, false);
                    } else {
                        this.enableFocusEditMode();
                    }
                }
            });

            var rightContainer = new DivEl('right-container');
            rightContainer.appendChildren(standbyContainer, editContainer);

            var zoomContainer = this.createZoomContainer();

            var topContainer = new DivEl('top-container');
            topContainer.appendChildren(this.editCropButton, this.editFocusButton, rightContainer);

            toolbar.appendChildren(topContainer, zoomContainer);

            return toolbar;
        }

        private updateStickyToolbar() {
            var relativeScrollTop = this.getRelativeScrollTop();
            if (!this.isTopEdgeVisible(relativeScrollTop) && this.isBottomEdgeVisible(relativeScrollTop)) {
                this.addClass("sticky-mode");
                this.stickyToolbar.getEl().setTopPx(-relativeScrollTop);
            } else {
                this.stickyToolbar.getEl().setTopPx(0);
                this.removeClass("sticky-mode");
            }
        }

        private createZoomContainer(): DivEl {
            this.zoomContainer = new DivEl('zoom-container');

            this.zoomLine = new DivEl('zoom-line');
            this.zoomKnob = new api.dom.SpanEl('zoom-knob');
            this.zoomLine.appendChild(this.zoomKnob);

            var zoomTitle = new api.dom.SpanEl('zoom-title');
            zoomTitle.setHtml('Zoom');

            this.zoomContainer.appendChildren(zoomTitle, this.zoomLine);

            return this.zoomContainer;
        }

        private isTopEdgeVisible(relativeScrollTop: number): boolean {
            return relativeScrollTop > 0;
        }

        private isBottomEdgeVisible(relativeScrollTop: number): boolean {
            // use crop area bottom edge
            var stickyToolbarHeight = this.stickyToolbar.getEl().getHeight(),
                frameHeight = this.frame.getEl().getHeight(),
                totalHeight = this.getEl().getHeight();

            // in crop edit mode toolbar grows bigger because of zoom control, so calc difference
            var toolbarDelta = stickyToolbarHeight - (totalHeight - frameHeight);

            return (this.getCropPositionPx().h + relativeScrollTop - toolbarDelta ) > 0;
        }

        private getRelativeScrollTop(): number {
            var scrollEl = wemjq(this.getHTMLElement()).closest(this.SCROLLABLE_SELECTOR),
                scrollElOffsetTop = scrollEl.length == 1
                    ? scrollEl.offset().top
                    : 0,
                wizardToolbarHeight = !this.isEditMode() && scrollEl.length == 1
                    ? scrollEl.find(this.WIZARD_TOOLBAR_SELECTOR).innerHeight()
                    : 0;

            return this.getEl().getOffsetTop() - scrollElOffsetTop - wizardToolbarHeight;
        }

        private setEditMode(edit: boolean, applyChanges: boolean = true) {
            if (ImageEditor.debug) {
                console.group('setEditMode');
                console.log('edit=' + edit + ', applyChanges=' + applyChanges);
            }

            this.setShaderVisible(edit);
            this.toggleClass('edit-mode', edit);

            var crop, zoom, focus, radius;

            if (edit) {
                this.updateRevertCropData();
                this.updateRevertZoomData();
                this.updateRevertFocusData();

                if (ImageEditor.debug) {
                    console.log('updated revert data');
                }
            } else {
                if (applyChanges) {
                    crop = this.getCropPosition();
                    zoom = this.getZoomPosition();
                    focus = this.getFocusPosition();
                    radius = this.getFocusRadius();

                    if (ImageEditor.debug) {
                        console.log('Applying changes: \nCrop', crop, '\nZoom', zoom, '\nFocus', focus, '\nRadius', radius);
                    }
                } else {
                    if (ImageEditor.debug) {
                        console.log('reverting focus to', this.revertFocusData);
                        console.log('reverting crop to', this.revertCropData);
                        console.log('reverting zoom to', this.revertZoomData);
                    }
                    this.setFocusPositionPx({x: this.revertFocusData.x, y: this.revertFocusData.y}, false);
                    this.setFocusRadiusPx(this.revertFocusData.r, false);
                    this.setFocusAutoPositioned(this.revertFocusData.auto);


                    this.setZoomPositionPx(this.revertZoomData, false);
                    this.setCropPositionPx(this.revertCropData, false);
                    this.setCropAutoPositioned(this.revertCropData.auto);
                }

                this.revertFocusData = undefined;
                this.revertCropData = undefined;
                this.revertZoomData = undefined;
            }

            this.notifyEditModeChanged(edit, crop, zoom, focus);

            // update it after listeners in case they modified anything
            this.updateStickyToolbar();

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        isEditMode(): boolean {
            return this.hasClass('edit-mode');
        }


        /*
         *  Focus related methods
         */

        private enableFocusEditMode(applyChanges: boolean = true, enterEditMode: boolean = true) {

            if (ImageEditor.debug) {
                console.log('enableFocusEditMode, applyChanges=' + applyChanges + ', enterEditMode=' + enterEditMode);
            }
            this.editResetButton.setLabel('Reset Autofocus').setVisible(!this.focusData.auto);

            this.setFocusEditMode(true);

            if (enterEditMode) {
                this.setEditMode(true, applyChanges);
            }

            this.bindFocusMouseListeners();
            this.updateFocusMaskPosition();
        }

        private disableFocusEditMode(applyChanges: boolean = true, exitEditMode: boolean = true) {

            if (ImageEditor.debug) {
                console.log('disableFocusEditMode, applyChanges=' + applyChanges + ', exitEditMode=' + exitEditMode);
            }
            this.unbindFocusMouseListeners();

            this.setFocusEditMode(false);

            if (exitEditMode) {
                this.setEditMode(false, applyChanges);
            }
        }

        private setFocusEditMode(edit: boolean) {
            if (ImageEditor.debug) {
                console.log('setFocusEditMode', edit);
            }
            this.toggleClass('edit-focus', edit);
            if (edit) {
                this.setImageClipPath(this.focusClipPath);
            }
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
                this.setFocusAutoPositioned(this.isFocusNotModified(this.focusData));
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
            if (ImageEditor.debug) {
                console.log('resetFocusPosition');
            }

            var denormalizedPoint = this.denormalizePoint(0.5, 0.5);
            // make sure it resets to the center of the crop area
            this.setFocusPositionPx({
                x: denormalizedPoint.x,
                y: denormalizedPoint.y
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
                this.setFocusAutoPositioned(this.isFocusNotModified(this.focusData));
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
            if (ImageEditor.debug) {
                console.log('setFocusAutoPositioned', auto);
            }
            var autoChanged = this.focusData.auto != auto;
            this.focusData.auto = auto;

            this.toggleClass('focused', !auto);

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

            var mouseDownOriginalTarget;
            this.mouseDownListener = (event: MouseEvent) => {

                if (ImageEditor.debug) {
                    console.log('ImageEditor.mouseDownListener');
                }

                mouseDownOriginalTarget = event['originalTarget'];
                mouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
            };
            this.clip.onMouseDown(this.mouseDownListener);

            this.mouseMoveListener = (event: MouseEvent) => {
                if (mouseDown) {
                    if (ImageEditor.debug) {
                        console.log('ImageEditor.mouseMoveListener');
                    }

                    var restrainedPos = {
                        x: this.restrainFocusX(this.focusData.x + this.getOffsetX(event) - lastPos.x),
                        y: this.restrainFocusY(this.focusData.y + this.getOffsetY(event) - lastPos.y)
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

                    if (this.isOutside(event) && mouseDownOriginalTarget == event['originalTarget']) {
                        if (ImageEditor.debug) {
                            console.log('mouseUpListener, set to skip next click');
                        }

                        // mouse up will trigger click event that should not be processed
                        this.skipNextOutsideClick = true;
                    }

                    // allow focus positioning by clicking
                    var restrainedPos = {
                        x: this.restrainFocusX(this.getOffsetX(event)),
                        y: this.restrainFocusY(this.getOffsetY(event))
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
                // focus position is calculated relative to crop area
                circle.setAttribute('cx', (this.cropData.x + this.focusData.x).toString());
                circle.setAttribute('cy', (this.cropData.y + this.focusData.y).toString());
            }
        }

        private restrainFocusX(x: number) {
            return Math.max(0, Math.min(this.cropData.w, x));
        }

        private restrainFocusY(y: number) {
            return Math.max(0, Math.min(this.cropData.h, y));
        }

        private restrainFocusRadius(r: number) {
            return Math.max(0, Math.min(this.cropData.w / 4, this.cropData.h / 4, r));
        }

        private isFocusNotModified(focus: FocusData): boolean {
            return this.isFocusPositionNotModified(focus) && this.isFocusRadiusNotModified(focus.r);
        }

        private isFocusPositionNotModified(focus: FocusData): boolean {
            return focus.x == Math.min(this.frameW, this.cropData.w) / 2 &&
                   focus.y == Math.min(this.frameH, this.cropData.h) / 2;
        }

        private isFocusRadiusNotModified(r: number): boolean {
            return r == Math.min(this.cropData.w, this.cropData.h) / 4;
        }


        /*
         *  Crop related methods
         */

        private enableCropEditMode(applyChanges: boolean = true, enterEditMode: boolean = true) {

            if (ImageEditor.debug) {
                console.log('enableCropEditMode, applyChanges=' + applyChanges + ', enterEditMode=' + enterEditMode);
            }
            this.editResetButton.setLabel('Reset Mask').setVisible(!this.cropData.auto);

            this.setCropEditMode(true);

            if (enterEditMode) {
                this.setEditMode(true, applyChanges);
            }

            this.bindCropMouseListeners();
            this.updateCropMaskPosition();
            this.updateZoomPosition();
        }

        private disableCropEditMode(applyChanges: boolean = true, exitEditMode: boolean = true) {

            if (ImageEditor.debug) {
                console.log('disableCropEditMode, applyChanges=' + applyChanges + ', exitEditMode=' + exitEditMode);
            }
            this.unbindCropMouseListeners();

            this.setCropEditMode(false);

            if (exitEditMode) {
                this.setEditMode(false, applyChanges);
            }
        }

        private setCropEditMode(edit: boolean) {
            if (ImageEditor.debug) {
                console.log('setCropEditMode, edit=' + edit);
            }
            this.toggleClass('edit-crop', edit);

            if (edit) {
                this.setImageClipPath(this.cropClipPath);

            } else {

                if (this.focusData.auto) {
                    // reset focus position to calc new value
                    this.resetFocusPosition();
                } else {
                    // set current position to restrain it with new crop data
                    this.setFocusPositionPx(this.focusData, false);
                }
            }
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
        setCropPosition(x: number, y: number, x2: number, y2: number) {
            var svg = this.rectToSVG(x, y, x2, y2);
            if (this.isImageLoaded()) {
                this.setCropPositionPx(this.denormalizeRect(svg));
            } else {
                // use revert position to temporary save values until the image is loaded
                // can't denormalize until image is loaded
                this.revertCropData = {
                    x: svg.x,
                    y: svg.y,
                    w: svg.w,
                    h: svg.h,
                    auto: this.cropData.auto
                }
            }
        }

        private setCropPositionPx(crop: SVGRect, updateAuto: boolean = true) {

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
                this.setCropAutoPositioned(this.isCropNotModified(this.cropData));
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

                // reset radius for it to be a quarter of the smallest side
                this.resetFocusRadius();

                // also restring focus position to be inside cropped area
                this.focusData.x = this.restrainFocusX(this.focusData.x);
                this.focusData.y = this.restrainFocusY(this.focusData.y);

                // update focus position for it to stay in place
                this.updateFocusMaskPosition();
            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        getCropPosition(): Rect {
            return this.rectFromSVG(this.normalizeRect(this.getCropPositionPx()));
        }

        private getCropPositionPx(): SVGRect {
            return {
                x: this.cropData.x,
                y: this.cropData.y,
                w: this.cropData.w,
                h: this.cropData.h
            }
        }

        resetCropPosition() {
            if (ImageEditor.debug) {
                console.log('resetCropPosition');
            }
            var crop = {x: 0, y: 0, w: 1, h: 1};
            this.setCropPositionPx(this.denormalizeRect(crop), false);
            this.setCropAutoPositioned(true);
        }

        private setCropAutoPositioned(auto: boolean) {
            if (ImageEditor.debug) {
                console.log('setCropAutoPositioned', auto);
            }
            var autoChanged = this.cropData.auto != auto;
            this.cropData.auto = auto;

            if (autoChanged) {
                this.notifyCropAutoPositionedChanged(auto);
            }
        }

        private updateCropMaskPosition() {
            var rect = this.cropClipPath.getHTMLElement().querySelector('rect'),
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

            // FF doesn't generate click after mouse down - up events if their originalTarget properties don't match
            // Chrome doesn't have such property
            var mouseDownOriginalTarget;

            this.dragMouseDownListener = (event: MouseEvent) => {
                event.stopPropagation();
                event.preventDefault();

                if (ImageEditor.debug) {
                    console.group('ImageEditor.dragMouseListener');
                    console.log('mouse down', event);
                }
                mouseDownOriginalTarget = event['originalTarget'];
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
                    console.log('ImageEditor.knobMouseListener', event);
                }
                mouseDownOriginalTarget = event['originalTarget'];
                zoomMouseDown = true;
                lastPos = {
                    x: this.getOffsetX(event),
                    y: this.getOffsetY(event)
                };
                this.zoomContainer.addClass('active');
            };
            this.zoomKnob.onMouseDown(this.knobMouseDownListener);

            this.mouseDownListener = (event: MouseEvent) => {
                event.stopPropagation();
                event.preventDefault();

                var x = this.getOffsetX(event),
                    y = this.getOffsetY(event);

                if (ImageEditor.debug) {
                    console.group('ImageEditor.mouseDownListener');
                    console.log('mouse down', event);
                }

                if (this.isInsideCrop(x, y)) {
                    if (ImageEditor.debug) {
                        console.log('click inside crop area');
                    }
                    mouseDownOriginalTarget = event['originalTarget'];
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

                    this.moveZoomKnobByPx(currPos.x - lastPos.x);

                } else if (dragMouseDown) {

                    var deltaY = this.getOffsetY(event) - lastPos.y,
                        distBetweenCropAndZoomBottoms = this.zoomData.h - this.cropData.h - this.cropData.y,
                        newH = this.cropData.h +
                               (deltaY > distBetweenCropAndZoomBottoms ? distBetweenCropAndZoomBottoms : deltaY);

                    if (newH > 0 && newH != this.cropData.h) {

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
                    console.log('mouse up', event);
                }

                if (this.isOutside(event) && mouseDownOriginalTarget == event['originalTarget'] &&
                    (dragMouseDown || zoomMouseDown || panMouseDown)) {
                    if (ImageEditor.debug) {
                        console.log('mouseUpListener, set to skip next click');
                    }

                    // mouse up will trigger click event that should not be processed
                    this.skipNextOutsideClick = true;
                }

                if (dragMouseDown) {
                    dragMouseDown = false;
                    this.dragHandle.removeClass('active');
                } else if (zoomMouseDown) {
                    zoomMouseDown = false;
                    this.zoomContainer.removeClass('active');
                } else if (panMouseDown) {
                    panMouseDown = false;
                }

                if (ImageEditor.debug) {
                    console.groupEnd();
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

        private rectFromSVG(svg: SVGRect): Rect {
            return svg ? {
                x: svg.x,
                y: svg.y,
                x2: svg.x + svg.w,
                y2: svg.y + svg.h
            } : undefined;
        }

        private rectToSVG(x: number, y: number, x2: number, y2: number): SVGRect {
            return {
                x: x,
                y: y,
                w: x2 - x,
                h: y2 - y
            }
        }

        private isCropNotModified(rect: SVGRect): boolean {
            return rect.x == 0 && rect.y == 0 && rect.w == this.frameW && rect.h == this.frameH;
        }


        /*
         *  Zoom related methods
         */

        setZoomPosition(x: number, y: number, x2: number, y2: number) {
            var zoom = this.rectToSVG(x, y, x2, y2);
            if (this.isImageLoaded()) {
                this.setZoomPositionPx(this.denormalizeRect(zoom));
            } else {
                // use revert position to temporary save values until the image is loaded
                // can't denormalize until image is loaded
                this.revertZoomData = {
                    x: zoom.x,
                    y: zoom.y,
                    w: zoom.w,
                    h: zoom.h
                }
            }
        }

        private setZoomPositionPx(zoom: SVGRect, updateAuto: boolean = true) {
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

                // update crop position for it to stay in place as zoom changes parent svg size
                this.setCropPositionPx({
                    x: this.cropData.x - dx,
                    y: this.cropData.y - dy,
                    w: this.cropData.w,
                    h: this.cropData.h
                }, updateAuto);

            }

            if (ImageEditor.debug) {
                console.groupEnd();
            }
        }

        getZoomPosition(): Rect {
            return this.rectFromSVG(this.normalizeRect(this.getZoomPositionPx()));
        }

        private getZoomPositionPx(): SVGRect {
            return {
                x: this.zoomData.x,
                y: this.zoomData.y,
                w: this.zoomData.w,
                h: this.zoomData.h
            }
        }

        resetZoomPosition() {
            if (ImageEditor.debug) {
                console.log('resetZoomPosition');
            }
            var zoom = {x: 0, y: 0, w: 1, h: 1};
            this.setZoomPositionPx(this.denormalizeRect(zoom), false);
            this.setCropAutoPositioned(true);
        }

        private isInsideZoom(x: number, y: number) {
            return x >= this.zoomData.x && x <= (this.zoomData.x + this.zoomData.w) &&
                   y >= this.zoomData.y && y <= (this.zoomData.y + this.zoomData.h);
        }

        private moveZoomKnobByPx(delta: number) {

            var zoomLineEl = this.zoomLine.getEl(),
                zoomKnobEl = this.zoomKnob.getEl();

            var sliderLength = zoomLineEl.getWidth(),
                knobX = zoomKnobEl.getLeftPx() || 0,
                knobNewX = Math.max(0, Math.min(sliderLength, knobX + delta));

            if (knobNewX != knobX) {
                zoomKnobEl.setLeftPx(knobNewX);

                var knobPct = knobNewX / sliderLength,
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

            var zoomKnobEl = this.zoomKnob.getEl(),
                zoomLineEl = this.zoomLine.getEl();

            var sliderLength = zoomLineEl.getWidth(),
                knobPct = (this.zoomData.w / this.frameW - 1 ) / (this.maxZoom - 1),
                knobNewX = Math.max(0, Math.min(sliderLength, knobPct * sliderLength));

            zoomKnobEl.setLeftPx(knobNewX);
        }

        private updateRevertCropData() {
            var cropPosition = this.getCropPositionPx();
            this.revertCropData = {
                x: cropPosition.x,
                y: cropPosition.y,
                w: cropPosition.w,
                h: cropPosition.h,
                auto: this.cropData.auto
            };
        }

        private updateRevertZoomData() {
            var zoomPosition = this.getZoomPositionPx();
            this.revertZoomData = {
                x: zoomPosition.x,
                y: zoomPosition.y,
                w: zoomPosition.w,
                h: zoomPosition.h
            };
        }

        private updateRevertFocusData() {
            var focusPosition = this.getFocusPositionPx();
            this.revertFocusData = {
                x: focusPosition.x,
                y: focusPosition.y,
                r: this.getFocusRadiusPx(),
                auto: this.focusData.auto
            };
        }

        /**
         * Zoom coordinates system starts in the top left corner of the original image
         * @param x
         * @returns {number}
         */
        private restrainZoomX(x: number) {
            return Math.max(Math.min(this.frameW, this.cropData.w) - this.zoomData.w, Math.min(0, x));
        }

        private restrainZoomY(y: number) {
            return Math.max(Math.min(this.frameH, this.cropData.h) - this.zoomData.h, Math.min(0, y));
        }

        private restrainZoomW(x: number) {
            return Math.max(Math.min(this.frameW, this.cropData.w), Math.min(this.maxZoom * this.frameW, x));
        }

        private restrainZoomH(y: number) {
            return Math.max(Math.min(this.frameH, this.cropData.h), Math.min(this.maxZoom * this.frameH, y));
        }


        /*
         *      Common listeners
         */

        onEditModeChanged(listener: (edit: boolean, position: Rect, zoom: Rect, focus: Point) => void) {
            this.editModeListeners.push(listener);
        }

        unEditModeChanged(listener: (edit: boolean, position: Rect, zoom: Rect, focus: Point) => void) {
            this.editModeListeners = this.editModeListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyEditModeChanged(edit: boolean, position: Rect, zoom: Rect, focus: Point) {
            this.editModeListeners.forEach((listener) => {
                listener(edit, position, zoom, focus);
            })
        }


        /*
         *   Focus related listeners
         */

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

        private notifyCropPositionChanged(position: SVGRect) {
            var normalizedPosition = this.rectFromSVG(this.normalizeRect(position));
            this.cropPositionChangedListeners.forEach((listener) => {
                listener(normalizedPosition);
            })
        }

        onShaderVisibilityChanged(listener: (auto: boolean) => void) {
            this.shaderVisibilityChangedListeners.push(listener);
        }

        unShaderVisibilityChanged(listener: (auto: boolean) => void) {
            this.shaderVisibilityChangedListeners = this.shaderVisibilityChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyShaderVisibilityChanged(auto: boolean) {
            this.shaderVisibilityChangedListeners.forEach((listener) => {
                listener(auto);
            })
        }

        onImageError(listener: (event: UIEvent) => void) {
            this.imageErrorListeners.push(listener);
        }

        unImageError(listener: (event: UIEvent) => void) {
            this.imageErrorListeners = this.imageErrorListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifyImageError(event: UIEvent) {
            this.imageErrorListeners.forEach(listener => listener(event));
        }
    }

}