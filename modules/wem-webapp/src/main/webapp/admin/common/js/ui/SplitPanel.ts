module api.ui {

    export enum SplitPanelAlignment {
        HORIZONTAL,
        VERTICAL
    }

    export class SplitPanelBuilder {

        private firstPanel:api.ui.Panel;

        private secondPanel:api.ui.Panel;

        private firstPanelFixed: boolean = true;

        private firstPanelSize: string = '50%';

        private secondPanelSize: string;

        private alignment: SplitPanelAlignment = SplitPanelAlignment.HORIZONTAL;

        private alignmentTreshold: number;

        private splitterThickness: number = 5;

        constructor(firstPanel: api.ui.Panel, secondPanel: api.ui.Panel) {
            this.firstPanel = firstPanel;
            this.secondPanel = secondPanel;
        }

        build():SplitPanel {
            return new SplitPanel(this);
        }

        fixFirstPanelSize(size: string): SplitPanelBuilder {
            this.firstPanelSize = size;
            this.firstPanelFixed = true;
            return this;
        }

        fixSecondPanelSize(size: string): SplitPanelBuilder {
            this.secondPanelSize = size;
            this.firstPanelFixed = false;
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

        getFirstPanel():api.ui.Panel {
            return this.firstPanel;
        }

        getSecondPanel():api.ui.Panel {
            return this.secondPanel;
        }

        isFirstPanelFixed():boolean {
            return this.firstPanelFixed;
        }

        getFirstPanelSize():string {
            return this.firstPanelSize;
        }

        getSecondPanelSize():string {
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

        private firstPanel:api.ui.Panel;

        private secondPanel:api.ui.Panel;

        private firstPanelSize:string;

        private secondPanelSize:string;

        private fixedPanel: api.ui.Panel;

        private pixelSize: boolean;

        private splitterThickness: number;

        private splitter:api.dom.DivEl;

        private alignment:SplitPanelAlignment;

        private alignmentTreshold: number;

        private ghostDragger: api.dom.DivEl;

        private dragListener: (e:MouseEvent) => void;

        private mask:api.ui.DragMask;

        constructor(builder:SplitPanelBuilder) {
            super("split-panel");
            this.firstPanel = builder.getFirstPanel();
            this.secondPanel = builder.getSecondPanel();
            if (builder.isFirstPanelFixed()) {
                this.setFirstPanelSize(builder.getFirstPanelSize());
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

            this.dragListener = (e:MouseEvent) => {
                if (this.isHorizontal()) {
                    this.ghostDragger.getEl().setTopPx(e.clientY - this.getEl().getOffsetTop());
                } else {
                    this.ghostDragger.getEl().setLeftPx(e.clientX - this.getEl().getOffsetLeft());
                }
            };

            this.splitter.onMouseDown((e:MouseEvent) => {
                e.preventDefault();
                this.ghostDragger.insertBeforeEl(this.splitter);
                this.startDrag();
            });

            this.onMouseUp((e:MouseEvent) => {
                if (this.ghostDragger.getHTMLElement().parentNode) {
                    this.stopDrag(e);
                    this.removeChild(this.ghostDragger);
                }
            });

            if (this.alignmentTreshold) {
                api.dom.Window.get().onResized((event: UIEvent) => this.updateAlignment(), this);
            }

            this.onShown((event: api.dom.ElementShownEvent) => this.updateAlignment());
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

        private stopDrag(e:MouseEvent) {
            this.mask.hide();
            this.removeClass("dragging");
            this.unMouseMove(this.dragListener);

            var splitPanelEl = this.getEl(),
                dragOffset = this.isHorizontal() ? e.clientY - splitPanelEl.getOffsetTop() : e.clientX - splitPanelEl.getOffsetLeft(),
                splitPanelSize = this.isHorizontal() ? splitPanelEl.getHeightWithBorder() : splitPanelEl.getWidthWithBorder(),
                fixedPanelSizePx = this.isFirstPanelFixed() ? dragOffset : splitPanelSize - dragOffset,
                fixedPanelSize = this.isPixelSize() ? fixedPanelSizePx + 'px' : fixedPanelSizePx / splitPanelSize * 100 + '%';

            this.isFirstPanelFixed() ? this.setFirstPanelSize(fixedPanelSize) : this.setSecondPanelSize(fixedPanelSize);
            this.distribute();
            this.fireResizeEvent();
        }

        setAlignment(alignment:SplitPanelAlignment) {
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
                this.firstPanel.getEl().setWidth(null);
                this.secondPanel.getEl().setWidth(null);
                this.splitter.getEl().setHeightPx(this.splitterThickness).setWidth(null).setLeft(null);
            } else {
                this.addClass("vertical");
                this.firstPanel.getEl().setHeight(null);
                this.secondPanel.getEl().setHeight(null);
                this.splitter.getEl().setWidthPx(this.splitterThickness).setHeight(null);
            }
            this.distribute();
        }

        setFirstPanelSize(size: string) {
            this.firstPanelSize = size;
            this.secondPanelSize = "100% - " + size;
            this.fixedPanel = this.firstPanel;
            this.pixelSize = size.indexOf('%') == -1;
        }

        setSecondPanelSize(size: string) {
            this.firstPanelSize = "100% - " + size;
            this.secondPanelSize = size;
            this.fixedPanel = this.secondPanel;
            this.pixelSize = size.indexOf('%') == -1;
        }

        distribute() {
            if (this.isHorizontal()) {
                this.firstPanel.getEl().setHeight("calc(" + this.firstPanelSize + " - " + this.splitterThickness / 2 + "px)").setWidth(null);
                this.secondPanel.getEl().setHeight("calc(" + this.secondPanelSize + " - " + this.splitterThickness / 2 + "px)").setWidth(null);
            }
            else {
                this.firstPanel.getEl().setWidth("calc(" + this.firstPanelSize + " - " + this.splitterThickness / 2 + "px)").setHeight(null);
                this.secondPanel.getEl().setWidth("calc(" + this.secondPanelSize + " - " + this.splitterThickness / 2 + "px)").setHeight(null);
                this.splitter.getEl().setLeft("calc(" + this.firstPanelSize + " - " + this.splitterThickness / 2 + "px)");
            }
        }

        private fireResizeEvent() {
            var event = document.createEvent('Event');
            event.initEvent('resize', true, true);
            this.getHTMLElement().dispatchEvent(event);
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