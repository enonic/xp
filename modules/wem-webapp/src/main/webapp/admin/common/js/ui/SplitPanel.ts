module api.ui {

    export enum SplitPanelAlignment {
        HORIZONTAL,
        VERTICAL
    }

    export class SplitPanelBuilder {

        private startPanel:api.ui.Panel;

        private endPanel:api.ui.Panel;

        private startPanelFixed: boolean = true;

        private startPanelSize: string = '50%';

        private endPanelSize: string;

        private alignment: SplitPanelAlignment = SplitPanelAlignment.HORIZONTAL;

        constructor(startPanel: api.ui.Panel, endPanel: api.ui.Panel) {
            this.startPanel = startPanel;
            this.endPanel = endPanel;
        }

        build():SplitPanel {
            return new SplitPanel(this);
        }

        fixStartPanelSize(size: string): SplitPanelBuilder {
            this.startPanelSize = size;
            this.startPanelFixed = true;
            return this;
        }

        fixEndPanelSize(size: string): SplitPanelBuilder {
            this.endPanelSize = size;
            this.startPanelFixed = false;
            return this;
        }

        setAlignment(alignment: SplitPanelAlignment): SplitPanelBuilder {
            this.alignment = alignment;
            return this;
        }

        getStartPanel():api.ui.Panel {
            return this.startPanel;
        }

        getEndPanel():api.ui.Panel {
            return this.endPanel;
        }

        isStartPanelFixed():boolean {
            return this.startPanelFixed;
        }

        getStartPanelSize():string {
            return this.startPanelSize;
        }

        getEndPanelSize():string {
            return this.endPanelSize;
        }

        getAlignment(): SplitPanelAlignment {
            return this.alignment;
        }
    }

    export class SplitPanel extends api.ui.Panel {
        private panelA:api.ui.Panel;

        private panelB:api.ui.Panel;

        private panelASpace:string;

        private panelBSpace:string;

        private fixedPanel: api.ui.Panel;

        private pixelSize: boolean;

        private splitter:SplitPanelSplitter;

        private alignment:SplitPanelAlignment;

        constructor(builder:SplitPanelBuilder) {
            super("split-panel");
            this.panelA = builder.getStartPanel();
            this.panelB = builder.getEndPanel();
            if (builder.isStartPanelFixed()) {
                this.setStartDistribution(builder.getStartPanelSize());
            } else {
                this.setEndDistribution(builder.getEndPanelSize());
            }
            this.alignment = builder.getAlignment();
            this.splitter = new SplitPanelSplitter(this);
            this.updateAlignment();

            this.panelA.setDoOffset(false);
            this.panelB.setDoOffset(false);

            this.appendChild(this.panelA);
            this.appendChild(this.splitter);
            this.appendChild(this.panelB);

            this.splitter.init();
        }

        setAlignment(alignment:SplitPanelAlignment) {
            this.alignment = alignment;
            this.updateAlignment();
        }

        setStartDistribution(size: string) {
            this.panelASpace = size;
            this.panelBSpace = "100% - " + size;
            this.fixedPanel = this.panelA;
            this.pixelSize = size.indexOf('%') == -1;
        }

        setEndDistribution(size: string) {
            this.panelASpace = "100% - " + size;
            this.panelBSpace = size;
            this.fixedPanel = this.panelB;
            this.pixelSize = size.indexOf('%') == -1;
        }

        distribute() {
            if (this.isHorizontal()) {
                this.panelA.getEl().setHeight("calc(" + this.panelASpace + " - " + this.splitter.getThickness() / 2 + "px)");
                this.panelB.getEl().setHeight("calc(" + this.panelBSpace + " - " + this.splitter.getThickness() / 2 + "px)");
            }
            else {
                this.panelA.getEl().setWidth("calc(" + this.panelASpace + " - " + this.splitter.getThickness() / 2 + "px)");
                this.panelB.getEl().setWidth("calc(" + this.panelBSpace + " - " + this.splitter.getThickness() / 2 + "px)");
            }
        }

        render() {
            this.distribute();
        }

        getPanelA():api.ui.Panel {
            return this.panelA;
        }

        getPanelB():api.ui.Panel {
            return this.panelB;
        }

        getPanelASpace():string {
            return this.panelASpace;
        }

        private updateAlignment() {
            if (this.isHorizontal()) {
                this.panelA.removeClass("vertical");
                this.panelB.removeClass("vertical");
            } else {
                this.panelA.addClass("vertical");
                this.panelB.addClass("vertical");
                this.panelB.getEl().setRight("0");
                this.splitter.setVertical();
            }
        }

        isHorizontal() {
            return this.alignment == SplitPanelAlignment.HORIZONTAL;
        }

        isStartFixed(): boolean {
            return this.fixedPanel == this.panelA;
        }

        isPixelSize(): boolean {
            return this.pixelSize;
        }
    }

    export class SplitPanelSplitter extends api.dom.DivEl {

        private splitPanel:SplitPanel;

        private ghostDragger:api.dom.DivEl;

        private dragListener:(e:MouseEvent) => void;

        private lastY:number;

        private lastX:number;

        private thickness:number = 5;

        private maskA:api.ui.DragMask;

        private maskB:api.ui.DragMask;


        constructor(splitPanel:SplitPanel) {
            super("splitter");
            this.splitPanel = splitPanel;
            this.ghostDragger = new api.dom.DivEl("ghost-dragger");
            if (!this.splitPanel.isHorizontal()) {
                this.setVertical();
            }
            this.updateThickness();

            this.dragListener = (e:MouseEvent) => {
                if (this.splitPanel.isHorizontal()) {
                    this.lastY = e.clientY;
                    this.ghostDragger.getEl().setTopPx(this.lastY - this.splitPanel.getEl().getOffsetTop());
                } else {
                    this.lastX = e.clientX;
                    this.ghostDragger.getEl().setLeftPx(this.lastX - this.splitPanel.getEl().getOffsetLeft());
                }
            };
        }

        init() {
            this.getEl().addEventListener("mousedown", (e:MouseEvent) => {
                e.preventDefault();
                this.ghostDragger.insertBeforeEl(this);
                this.startDrag();
            });

            this.getParentElement().getEl().addEventListener("mouseup", (e:MouseEvent) => {
                if (this.ghostDragger.getHTMLElement().parentNode) {
                    this.stopDrag();
                    this.splitPanel.removeChild(this.ghostDragger);
                }
            });
        }

        private stopDrag() {
            this.removePanelMask();
            var splitPanelEl = this.splitPanel.getEl();
            splitPanelEl.removeClass("dragging");

            var dragOffset = this.splitPanel.isHorizontal() ? this.lastY - splitPanelEl.getOffsetTop() : this.lastX - splitPanelEl.getOffsetLeft(),
                splitPanelSize = this.splitPanel.isHorizontal() ? splitPanelEl.getHeightWithBorder() : splitPanelEl.getWidthWithBorder(),
                startPanelSize = this.splitPanel.isPixelSize() ? dragOffset + 'px' : dragOffset / splitPanelSize * 100 + '%',
                endPanelSize = this.splitPanel.isPixelSize() ? splitPanelSize - dragOffset + 'px' : (splitPanelSize - dragOffset) / splitPanelSize * 100 + '%';
            if (!this.splitPanel.isHorizontal()) {
                this.getEl().setLeft("calc(" + startPanelSize + " - " + this.getThickness()/2 + "px)");
            }
            if (this.splitPanel.isStartFixed()) {
                this.splitPanel.setStartDistribution(startPanelSize);
            } else {
                this.splitPanel.setEndDistribution(endPanelSize);
            }
            this.splitPanel.distribute();

            this.splitPanel.getHTMLElement().removeEventListener("mousemove", this.dragListener);
        }

        private startDrag() {
            this.addPanelMask();
            this.splitPanel.getEl().addClass("dragging");
            this.splitPanel.getHTMLElement().addEventListener("mousemove", this.dragListener);
        }

        private addPanelMask() {
            if (!this.maskA) {
                this.maskA = new api.ui.DragMask(this.splitPanel.getPanelA());
            }
            if (!this.maskB) {
                this.maskB = new api.ui.DragMask(this.splitPanel.getPanelB());
            }

            this.maskA.show();
            this.maskB.show();

            this.splitPanel.getPanelA().getParentElement().appendChild(this.maskA);
            this.splitPanel.getPanelA().getParentElement().appendChild(this.maskB);
        }

        private removePanelMask() {
            this.maskA.hide();
            this.maskB.hide();
            this.maskA.remove();
            this.maskB.remove();
        }

        getThickness():number {
            return this.thickness;
        }

        private updateThickness() {
            if (this.splitPanel.isHorizontal()) {
                this.getEl().setHeightPx(this.thickness).setWidth(null);
            } else {
                this.getEl().setWidthPx(this.thickness).setHeight(null);
            }
        }

        setVertical() {
            this.getEl().addClass("vertical");
            this.ghostDragger.getEl().addClass("vertical");
            this.getEl().setLeft("calc(" + this.splitPanel.getPanelASpace() + " - " + (this.thickness/2) + "px)");
            this.updateThickness();
        }

        setHorizontal() {
            this.getEl().removeClass("vertical");
            this.ghostDragger.getEl().removeClass("vertical");
            this.getEl().setLeft(null);
            this.updateThickness();
        }
    }
}