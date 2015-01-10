module api.ui.panel {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;

    export enum SplitPanelAlignment {
        HORIZONTAL,
        VERTICAL
    }

    export enum SplitPanelUnit {
        PIXEL,
        PERCENT
    }

    export class SplitPanelBuilder {

        private firstPanel: Panel;

        private secondPanel: Panel;

        private firstPanelSize: number = 50;

        private firstPanelMinSize: number = 0;

        private firstPanelUnit: SplitPanelUnit = SplitPanelUnit.PERCENT;

        private secondPanelSize: number = 50;

        private secondPanelMinSize: number = 0;

        private secondPanelUnit: SplitPanelUnit = SplitPanelUnit.PERCENT;

        private alignment: SplitPanelAlignment = SplitPanelAlignment.HORIZONTAL;

        private alignmentTreshold: number;

        private splitterThickness: number = 5;

        private firstPanelIsDecidingPanel: boolean = true;

        constructor(firstPanel: Panel, secondPanel: Panel) {
            this.firstPanel = firstPanel;
            this.secondPanel = secondPanel;
        }

        build(): SplitPanel {
            return new SplitPanel(this);
        }

        setFirstPanelSize(size: number, unit: SplitPanelUnit): SplitPanelBuilder {
            this.firstPanelSize = size;
            this.firstPanelUnit = unit;
            this.firstPanelIsDecidingPanel = true;
            return this;
        }

        setFirstPanelMinSize(size: number, unit: SplitPanelUnit): SplitPanelBuilder {
            this.firstPanelMinSize = size;
            this.firstPanelUnit = unit;
            this.firstPanelIsDecidingPanel = false;
            return this;
        }

        setSecondPanelSize(size: number, unit: SplitPanelUnit): SplitPanelBuilder {
            this.secondPanelSize = size;
            this.secondPanelUnit = unit;
            return this;
        }

        setSecondPanelMinSize(size: number, unit: SplitPanelUnit): SplitPanelBuilder {
            this.secondPanelMinSize = size;
            this.secondPanelUnit = unit;
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

        getFirstPanel(): Panel {
            return this.firstPanel;
        }

        getFirstPanelMinSize(): number {
            return this.firstPanelMinSize;
        }

        getSecondPanel(): Panel {
            return this.secondPanel;
        }

        getSecondPanelMinSize(): number {
            return this.secondPanelMinSize;
        }

        getFirstPanelSize(): number {
            return this.firstPanelSize;
        }

        getSecondPanelSize(): number {
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

        getFirstPanelUnit(): SplitPanelUnit {
            return this.firstPanelUnit
        }

        getSecondPanelUnit(): SplitPanelUnit {
            return this.secondPanelUnit;
        }

        isFirstPanelDecidingPanel(): boolean {
            return this.firstPanelIsDecidingPanel;
        }
    }

    export class SplitPanel extends Panel {

        private firstPanel: Panel;

        private secondPanel: Panel;

        private firstPanelSize: number = -1; // -1 means the rest of the page

        private firstPanelMinSize: number;

        private firstPanelUnit: SplitPanelUnit;

        private secondPanelSize: number = -1; // -1 means the rest of the page

        private secondPanelMinSize: number;

        private secondPanelUnit: SplitPanelUnit;

        private splitterThickness: number;

        private previousSplitterThickness: number;

        private splitter: api.dom.DivEl;

        private alignment: SplitPanelAlignment;

        private alignmentTreshold: number;

        private ghostDragger: api.dom.DivEl;

        private dragListener: (e: MouseEvent) => void;

        private mask: api.ui.mask.DragMask;

        private splitterPosition: number;

        private hiddenPanel: number;

        private hiddenPanelPreviousSize: number;

        constructor(builder: SplitPanelBuilder) {
            super("split-panel");
            this.firstPanel = builder.getFirstPanel();
            this.firstPanelMinSize = builder.getFirstPanelMinSize();
            this.firstPanelUnit = builder.getFirstPanelUnit();
            this.secondPanel = builder.getSecondPanel();
            this.secondPanelMinSize = builder.getSecondPanelMinSize();
            this.secondPanelUnit = builder.getSecondPanelUnit();

            if (builder.isFirstPanelDecidingPanel()) {
                this.setFirstPanelSize(builder.getFirstPanelSize(), this.firstPanelUnit);
            } else {
                this.setSecondPanelSize(builder.getSecondPanelSize(), this.secondPanelUnit);
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
            this.mask = new api.ui.mask.DragMask(this);
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
                api.dom.WindowDOM.get().onResized((event: UIEvent) => this.updateAlignment(), this);
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

            this.onRemoved((event) => {
                ResponsiveManager.unAvailableSizeChanged(this);
                ResponsiveManager.unAvailableSizeChanged(this.firstPanel);
                ResponsiveManager.unAvailableSizeChanged(this.secondPanel);
            });
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

            var splitPanelEl = this.getEl();
            var dragOffset = this.isHorizontal() ? this.splitterPosition - splitPanelEl.getOffsetTop() : this.splitterPosition -
                                                                                                         splitPanelEl.getOffsetLeft();
            var splitPanelSize = this.isHorizontal() ? splitPanelEl.getHeightWithBorder() : splitPanelEl.getWidthWithBorder();

            if (this.firstPanelUnit == SplitPanelUnit.PERCENT) {
                this.firstPanelSize = (dragOffset / splitPanelSize) * 100;
            } else {
                this.firstPanelSize = dragOffset;
            }

            this.distribute();
        }

        private splitterWithinBoundaries(offset: number) {
            var firstPanelSize = this.isHorizontal() ? this.firstPanel.getEl().getHeight() : this.firstPanel.getEl().getWidth();
            var secondPanelSize = this.isHorizontal() ? this.secondPanel.getEl().getHeight() : this.secondPanel.getEl().getWidth();

            var newFirstPanelWidth = firstPanelSize - offset;
            var newSecondPanelWidth = secondPanelSize + offset;
            return (newFirstPanelWidth >= this.firstPanelMinSize) && (newSecondPanelWidth >= this.secondPanelMinSize);
        }

        private updateAlignment() {
            var splitPanelWidth = this.getEl().getWidthWithMargin();
            if (splitPanelWidth > this.alignmentTreshold && this.isHorizontal()) {
                this.alignment = SplitPanelAlignment.VERTICAL;
            } else if (splitPanelWidth < this.alignmentTreshold && !this.isHorizontal()) {
                this.alignment = SplitPanelAlignment.HORIZONTAL;
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

        setFirstPanelSize(size: number, unit?: SplitPanelUnit) {
            this.firstPanelSize = size;
            this.secondPanelSize = -1;
            if (unit) {
                this.firstPanelUnit = unit;
            }
        }

        setSecondPanelSize(size: number, unit?: SplitPanelUnit) {
            this.secondPanelSize = size;
            this.firstPanelSize = -1;
            if (unit) {
                this.secondPanelUnit = unit;
            }
        }

        distribute() {
            if (this.isHorizontal()) {
                this.firstPanel.getEl().setHeight(this.getPanelSizeString(1)).setWidth(null);
                this.secondPanel.getEl().setHeight(this.getPanelSizeString(2)).setWidth(null);
                ResponsiveManager.fireResizeEvent();
            } else {
                this.firstPanel.getEl().setWidth(this.getPanelSizeString(1)).setHeight(null);
                this.secondPanel.getEl().setWidth(this.getPanelSizeString(2)).setHeight(null);
                ResponsiveManager.fireResizeEvent();
                if (this.firstPanelUnit == SplitPanelUnit.PERCENT) {
                    var positionInPercentage = (this.firstPanelSize != -1) ? this.firstPanelSize : 100 - this.secondPanelSize;
                    this.splitter.getEl().setLeft("calc(" + positionInPercentage + "% - " + (this.splitterThickness / 2) + "px)");
                } else {
                    this.splitter.getEl().setLeft(this.getPanelSizeString(1));
                }
            }
        }

        isHorizontal() {
            return this.alignment == SplitPanelAlignment.HORIZONTAL;
        }

        getPanelSizeString(panelNumber: number): string {
            api.util.assert((panelNumber == 1 || panelNumber == 2), "Panel number must be 1 or 2");

            var size = (panelNumber == 1) ? this.firstPanelSize : this.secondPanelSize;
            var otherPanelSize = (panelNumber == 1) ? this.secondPanelSize : this.firstPanelSize;
            var unit = (panelNumber == 1) ? this.firstPanelUnit : this.secondPanelUnit;
            var otherPanelUnit = (panelNumber == 1) ? this.secondPanelUnit : this.firstPanelUnit;

            var result;
            if (size != -1) { // This panel is the deciding panel
                if (unit == SplitPanelUnit.PIXEL) {
                    result = size - (this.splitterThickness / 2) + "px";
                } else {
                    result = "calc(" + size + "%" + " - " + (this.splitterThickness / 2) + "px)";
                }
            } else { // Other panel is the deciding panel
                if (otherPanelUnit == SplitPanelUnit.PIXEL) {
                    result = "calc(100% - " + (otherPanelSize + (this.splitterThickness / 2)) + "px)";
                } else {
                    result = "calc(" + (100 - otherPanelSize) + "%" + " - " + (this.splitterThickness / 2) + "px)";
                }
            }
            return result;
        }

        showPanel(panelNumber: number) {
            api.util.assert((panelNumber == 1 || panelNumber == 2), "Panel number must be 1 or 2");
            if (!this.isPanelHidden(panelNumber)) {
                return;
            }
            this.splitterThickness = this.previousSplitterThickness;
            this.splitter.show();

            if (panelNumber == 1) {
                this.firstPanelSize = this.hiddenPanelPreviousSize;
                this.firstPanel.show();
            } else {
                this.secondPanelSize = this.hiddenPanelPreviousSize;
                this.secondPanel.show();
            }

            this.hiddenPanel = -1;
            this.distribute();
        }

        hidePanel(panelNumber: number) {
            api.util.assert((panelNumber == 1 || panelNumber == 2), "Panel number must be 1 or 2");
            if (this.isPanelHidden(panelNumber)) {
                return;
            }

            this.previousSplitterThickness = this.splitterThickness;
            this.splitterThickness = 0;
            this.splitter.hide();
            this.hiddenPanel = panelNumber;

            if (panelNumber == 1) {
                this.hiddenPanelPreviousSize = this.firstPanelSize;
                this.firstPanelSize = 0;
                this.firstPanel.hide();
            } else {
                this.hiddenPanelPreviousSize = this.secondPanelSize;
                this.secondPanelSize = 0;
                this.secondPanel.hide();
            }
            this.distribute();
        }

        isPanelHidden(panelNumber: number) {
            return this.hiddenPanel == panelNumber;
        }

        private getUnitString(panelNumber: number): string {
            api.util.assert((panelNumber == 1 || panelNumber == 2), "Panel number must be 1 or 2");

            var unit = (panelNumber == 1) ? this.firstPanelUnit : this.secondPanelUnit;
            //console.log("UNIT", unit);
            if (unit == SplitPanelUnit.PIXEL) {
                return "px";
            } else {
                return "%";
            }
        }
    }
}