module api.ui {

    export enum SplitPanelAlignment {
        HORIZONTAL,
        VERTICAL
    }

    export class SplitPanelBuilder {

        private firstPanel: api.ui.Panel;

        private secondPanel: api.ui.Panel;

        private firstPanelFixed: boolean = true;

        private firstPanelSize: string = '50%';

        private firstPanelMinSize: number = 0;

        private secondPanelSize: string;

        private secondPanelMinSize: number = 0;

        private alignment: SplitPanelAlignment = SplitPanelAlignment.HORIZONTAL;

        private alignmentTreshold: number;

        private splitterThickness: number = 5;

        constructor(firstPanel: api.ui.Panel, secondPanel: api.ui.Panel) {
            this.firstPanel = firstPanel;
            this.secondPanel = secondPanel;
        }

        build(): SplitPanel {
            return new SplitPanel(this);
        }

        fixFirstPanelSize(size: string): SplitPanelBuilder {
            this.firstPanelSize = size;
            this.firstPanelFixed = true;
            return this;
        }

        setFirstPanelMinSize(size: number): SplitPanelBuilder {
            this.firstPanelMinSize = size;
            return this;
        }

        fixSecondPanelSize(size: string): SplitPanelBuilder {
            this.secondPanelSize = size;
            this.firstPanelFixed = false;
            return this;
        }

        setSecondPanelMinSize(size: number): SplitPanelBuilder {
            this.secondPanelMinSize = size;
            return this;
        }

        setAlignment(alignment: SplitPanelAlignment): SplitPanelBuilder {
            this.alignment = alignment;
            return this;
        }

        setAlignmentTreshold(treshold: number): SplitPanelBuilder {
            this.alignmentTreshold = treshold;
            return this;
        }

        setSplitterThickness(thickness: number): SplitPanelBuilder {
            this.splitterThickness = thickness;
            return this;
        }

        getFirstPanel(): api.ui.Panel {
            return this.firstPanel;
        }

        getFirstPanelMinSize(): number {
            return this.firstPanelMinSize;
        }

        getSecondPanel(): api.ui.Panel {
            return this.secondPanel;
        }

        getSecondPanelMinSize(): number {
            return this.secondPanelMinSize;
        }

        isFirstPanelFixed(): boolean {
            return this.firstPanelFixed;
        }

        getFirstPanelSize(): string {
            return this.firstPanelSize;
        }

        getSecondPanelSize(): string {
            return this.secondPanelSize;
        }

        getAlignment(): SplitPanelAlignment {
            return this.alignment;
        }

        getAlignmentTreshold(): number {
            return this.alignmentTreshold;
        }

        getSplitterThickness(): number {
            return this.splitterThickness;
        }
    }

    export class SplitPanel extends api.ui.Panel {

        private firstPanel: api.ui.Panel;

        private secondPanel: api.ui.Panel;

        private firstPanelSize: string;

        private firstPanelMinSize: number;

        private secondPanelSize: string;

        private secondPanelMinSize: number;

        private fixedPanel: api.ui.Panel;

        private pixelSize: boolean;

        private splitterThickness: number;

        private splitter: api.dom.DivEl;

        private alignment: SplitPanelAlignment;

        private alignmentTreshold: number;

        private ghostDragger: api.dom.DivEl;

        private dragListener: (e: MouseEvent) => void;

        private mask: api.ui.DragMask;

        private splitterPosition: number;

        constructor(builder: SplitPanelBuilder) {
            super("split-panel");
            this.firstPanel = builder.getFirstPanel();
            this.firstPanelMinSize = builder.getFirstPanelMinSize();
            this.secondPanel = builder.getSecondPanel();
            this.secondPanelMinSize = builder.getSecondPanelMinSize();
            if (builder.isFirstPanelFixed()) {
                this.setFirstPanelSize(builder.getFirstPanelSize(), true);
            } else {
                this.setSecondPanelSize(builder.getSecondPanelSize());
            }
            this.alignment = builder.getAlignment();
            this.alignmentTreshold = builder.getAlignmentTreshold();
            this.splitterThickness = builder.getSplitterThickness();
            this.splitter = new api.dom.DivEl("splitter");

            this.firstPanel.setDoOffset(false);
            this.secondPanel.setDoOffset(false);

            this.appendChild(this.firstPanel);
            this.appendChild(this.splitter);
            this.appendChild(this.secondPanel);

            this.ghostDragger = new api.dom.DivEl("ghost-dragger");
            this.mask = new api.ui.DragMask(this);
            this.appendChild(this.mask);

            var initialPos = 0;
            this.dragListener = (e: MouseEvent) => {
                if (this.isHorizontal()) {
                    if (this.splitterWithinBoundaries(initialPos - e.clientY)) {
                        this.splitterPosition = e.clientY;
                        this.ghostDragger.getEl().setTopPx(e.clientY - this.getEl().getOffsetTop());
                    }
                } else {
                    if (this.splitterWithinBoundaries(initialPos - e.clientX)) {
                        this.splitterPosition = e.clientX;
                        this.ghostDragger.getEl().setLeftPx(e.clientX - this.getEl().getOffsetLeft());
                    }
                }
            };

            this.splitter.onMouseDown((e: MouseEvent) => {
                e.preventDefault();
                if (this.isHorizontal()) {
                    initialPos = e.clientY;
                } else {
                    initialPos = e.clientX;
                }
                this.ghostDragger.insertBeforeEl(this.splitter);
                this.startDrag();
            });

            this.onMouseUp((e: MouseEvent) => {
                if (this.ghostDragger.getHTMLElement().parentNode) {
                    this.stopDrag(e);
                    this.removeChild(this.ghostDragger);
                }
            });

            if (this.alignmentTreshold) {
                api.dom.Window.get().onResized((event: UIEvent) => this.updateAlignment(), this);
            }

            this.onShown((event: api.dom.ElementShownEvent) => {
                var splitPanelSize = this.isHorizontal() ? this.getEl().getHeight() : this.getEl().getWidth();
                api.util.assert(this.firstPanelMinSize + this.secondPanelMinSize <= splitPanelSize,
                    "warning: total sum of first and second panel minimum sizes exceed total split panel size");
                this.updateAlignment();
            });

            // Add all elements, needed to be tracked
            ResponsiveManager.onAvailableSizeChanged(this);
            ResponsiveManager.onAvailableSizeChanged(this.firstPanel);
            ResponsiveManager.onAvailableSizeChanged(this.secondPanel);
        }

        private startDrag() {
            this.mask.show();
            this.addClass("dragging");
            this.onMouseMove(this.dragListener);

            if (this.isHorizontal()) {
                this.ghostDragger.getEl().setTopPx(this.splitter.getEl().getOffsetTopRelativeToParent()).setLeft(null);
            } else {
                this.ghostDragger.getEl().setLeftPx(this.splitter.getEl().getOffsetLeftRelativeToParent()).setTop(null);
            }
        }

        private stopDrag(e: MouseEvent) {
            this.mask.hide();
            this.removeClass("dragging");
            this.unMouseMove(this.dragListener);

            var splitPanelEl = this.getEl(),
                dragOffset = this.isHorizontal() ? this.splitterPosition - splitPanelEl.getOffsetTop() : this.splitterPosition -
                                                                                                         splitPanelEl.getOffsetLeft(),
                splitPanelSize = this.isHorizontal() ? splitPanelEl.getHeightWithBorder() : splitPanelEl.getWidthWithBorder(),
                fixedPanelSizePx = this.isFirstPanelFixed() ? dragOffset : splitPanelSize - dragOffset,
                fixedPanelSize = this.isPixelSize() ? fixedPanelSizePx + 'px' : fixedPanelSizePx / splitPanelSize * 100 + '%';

            this.isFirstPanelFixed() ? this.setFirstPanelSize(fixedPanelSize) : this.setSecondPanelSize(fixedPanelSize);
            this.distribute();
            ResponsiveManager.fireResizeEvent();
        }

        private splitterWithinBoundaries(offset: number) {
            var firstPanelSize = this.isHorizontal() ? this.firstPanel.getEl().getHeight() : this.firstPanel.getEl().getWidth();
            var secondPanelSize = this.isHorizontal() ? this.secondPanel.getEl().getHeight() : this.secondPanel.getEl().getWidth();

            var newFirstPanelWidth = firstPanelSize - offset;
            var newSecondPanelWidth = secondPanelSize + offset;
            return (newFirstPanelWidth >= this.firstPanelMinSize) && (newSecondPanelWidth >= this.secondPanelMinSize);
        }

        setAlignment(alignment: SplitPanelAlignment) {
            this.alignment = alignment;
            this.updateAlignment();
        }

        private updateAlignment() {
            var splitPanelWidth = this.getEl().getWidthWithMargin();
            if (splitPanelWidth > this.alignmentTreshold && this.isHorizontal()) {
                this.alignment = api.ui.SplitPanelAlignment.VERTICAL;
            } else if (splitPanelWidth < this.alignmentTreshold && !this.isHorizontal()) {
                this.alignment = api.ui.SplitPanelAlignment.HORIZONTAL;
            }

            if (this.isHorizontal()) {
                this.removeClass("vertical");
                this.addClass("horizontal");
                this.firstPanel.getEl().setWidth(null);
                this.secondPanel.getEl().setWidth(null);
                this.splitter.getEl().setHeightPx(this.splitterThickness).setWidth(null).setLeft(null);
            } else {
                this.addClass("vertical");
                this.removeClass("horizontal");
                this.firstPanel.getEl().setHeight(null);
                this.secondPanel.getEl().setHeight(null);
                this.splitter.getEl().setWidthPx(this.splitterThickness).setHeight(null);
            }
            this.distribute();
        }

        setFirstPanelSize(size: string, fixed?: boolean) {
            this.firstPanelSize = size;
            this.secondPanelSize = "100% - " + size;
            if (fixed) {
                this.fixedPanel = this.firstPanel;
            }
            this.pixelSize = size.indexOf('%') == -1;
        }

        setSecondPanelSize(size: string, fixed?: boolean) {
            this.firstPanelSize = "100% - " + size;
            this.secondPanelSize = size;
            if (fixed) {
                this.fixedPanel = this.secondPanel;
            }
            this.pixelSize = size.indexOf('%') == -1;
        }

        distribute() {
            if (this.isHorizontal()) {
                this.firstPanel.getEl().setHeight("calc(" + this.firstPanelSize + " - " + this.splitterThickness / 2 +
                                                  "px)").setWidth(null);
                this.secondPanel.getEl().setHeight("calc(" + this.secondPanelSize + " - " + this.splitterThickness / 2 +
                                                   "px)").setWidth(null);
            }
            else {
                this.firstPanel.getEl().setWidth("calc(" + this.firstPanelSize + " - " + this.splitterThickness / 2 +
                                                 "px)").setHeight(null);
                this.secondPanel.getEl().setWidth("calc(" + this.secondPanelSize + " - " + this.splitterThickness / 2 +
                                                  "px)").setHeight(null);
                this.splitter.getEl().setLeft("calc(" + this.firstPanelSize + " - " + this.splitterThickness / 2 + "px)");
            }

            ResponsiveManager.fireResizeEvent();
        }

        isHorizontal() {
            return this.alignment == SplitPanelAlignment.HORIZONTAL;
        }

        isFirstPanelFixed(): boolean {
            return this.fixedPanel == this.firstPanel;
        }

        isPixelSize(): boolean {
            return this.pixelSize;
        }
    }
}