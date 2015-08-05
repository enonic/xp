module app.view.widget {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;

    export class WidgetsPanel extends api.ui.panel.Panel {

        private widgets: Widget[] = [];
        private labelEl: api.dom.SpanEl;
        private widgetsContainer: api.dom.DivEl = new api.dom.DivEl("widgets-container");
        private animationTimer;

        private splitter: api.dom.DivEl;
        private ghostDragger: api.dom.DivEl;
        private mask: api.ui.mask.DragMask;

        private actualWidth: number;
        private minWidth: number = 280;
        private parentMinWidth: number = 15;

        private sizeChangedListeners: {() : void}[] = [];

        constructor(name?: string) {
            super("widgets-panel");
            this.setDoOffset(false);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {

            });

            this.onShown((event) => {

            });

            this.ghostDragger = new api.dom.DivEl("ghost-dragger");
            this.splitter = new api.dom.DivEl("splitter");
            this.appendChild(this.splitter);

            this.onRendered(() => this.onRenderedHandler());

            this.labelEl = new api.dom.SpanEl("widgets-panel-label");
            if (name) {
                this.labelEl.setHtml(name);
            }
            this.appendChild(this.labelEl);
            this.appendChild(this.widgetsContainer)
        }

        private onRenderedHandler() {
            var initialPos = 0;
            var splitterPosition = 0;
            var parent = this.getParentElement();
            this.actualWidth = this.getEl().getWidth();
            this.mask = new api.ui.mask.DragMask(parent);

            var dragListener = (e: MouseEvent) => {
                if (this.splitterWithinBoundaries(initialPos - e.clientX)) {
                    splitterPosition = e.clientX;
                    this.ghostDragger.getEl().setLeftPx(e.clientX - this.getEl().getOffsetLeft());
                }
            };

            this.splitter.onMouseDown((e: MouseEvent) => {
                e.preventDefault();
                initialPos = e.clientX;
                splitterPosition = e.clientX;
                this.startDrag(dragListener);
            });

            this.mask.onMouseUp((e: MouseEvent) => {
                this.actualWidth = this.getEl().getWidth() + initialPos - splitterPosition;
                this.stopDrag(dragListener);
                ResponsiveManager.fireResizeEvent();
            });
        }

        private splitterWithinBoundaries(offset: number) {
            var newWidth = this.actualWidth + offset;
            return (newWidth >= this.minWidth) && (newWidth <= this.getParentElement().getEl().getWidth() - this.parentMinWidth);
        }

        private startDrag(dragListener: {(e: MouseEvent):void}) {
            this.mask.show();
            this.mask.onMouseMove(dragListener);
            this.ghostDragger.insertBeforeEl(this.splitter);
            this.ghostDragger.getEl().setLeftPx(this.splitter.getEl().getOffsetLeftRelativeToParent()).setTop(null);
        }

        private stopDrag(dragListener: {(e: MouseEvent):void}) {
            this.getEl().setWidthPx(this.actualWidth);

            this.callWithTimeout(() => {
                this.notifyPanelSizeChanged();
            }, 800); //delay is required due to animation time

            this.mask.unMouseMove(dragListener);
            this.mask.hide();
            this.removeChild(this.ghostDragger);
        }

        removeWidgets() {
            this.widgetsContainer.removeChildren();
        }

        addWidget(widget: Widget) {
            this.widgets.push(widget);
            this.widgetsContainer.appendChild(widget);
        }

        setName(name: string) {
            this.labelEl.setHtml(name);
        }

        slideOut() {
            this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
        }

        slideIn() {
            this.getEl().setRightPx(0);
        }

        notifyPanelSizeChanged() {
            this.sizeChangedListeners.forEach((listener: ()=> void) => listener());
        }

        onPanelSizeChanged(listener: () => void) {
            this.sizeChangedListeners.push(listener);
        }

        unPanelSizeChanged(listener: () => void) {
            this.sizeChangedListeners.filter((currentListener: () => void) => {
                return listener == currentListener;
            });
        }

        callWithTimeout(callback: () => void, timer: number) {

            if (this.animationTimer) {
                clearTimeout(this.animationTimer);
            }
            this.animationTimer = setTimeout(() => {
                callback();
                this.animationTimer = null
            }, timer);
        }
    }
}
