module api_ui {

    export enum SplitPanelAlignment {
        HORIZONTAL,
        VERTICAL
    }

    export class SplitPanel extends api_ui.Panel {
        private panelA:api_ui.Panel;

        private panelB:api_ui.Panel;

        private panelASpace:number;

        private panelBSpace:number;

        private splitter:SplitPanelSplitter;

        private alignment:SplitPanelAlignment;

        constructor(panelA:api_ui.Panel, panelB:api_ui.Panel) {
            super();
            this.getEl().addClass("split-panel");
            this.splitter = new SplitPanelSplitter(this);
            this.splitter.setThickness(5);
            this.panelA = panelA;
            this.panelB = panelB;
            this.panelASpace = 50;
            this.panelBSpace = 50;
            this.alignment = SplitPanelAlignment.HORIZONTAL;

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

        setDistribution(aSpace:number, bSpace:number) {
            this.panelASpace = aSpace;
            this.panelBSpace = bSpace;
        }

        getSplitter():SplitPanelSplitter {
            return this.splitter;
        }

        distribute() {
            if (this.isHorizontal()) {
                this.panelA.getHTMLElement().style.height = "calc(" + this.panelASpace + "% - " + this.splitter.getThickness() / 2 + "px)";
                this.panelB.getHTMLElement().style.height = "calc(" + this.panelBSpace + "% - " + this.splitter.getThickness() / 2 + "px)";
            }
            else {
                this.panelA.getHTMLElement().style.width = "calc(" + this.panelASpace + "% - " + this.splitter.getThickness() / 2 + "px)";
                this.panelB.getHTMLElement().style.width = "calc(" + this.panelBSpace + "% - " + this.splitter.getThickness() / 2 + "px)";
            }
        }

        render() {
            this.distribute();
        }

        getPanelA():api_ui.Panel {
            return this.panelA;
        }

        getPanelASpace():number {
            return this.panelASpace;
        }

        private updateAlignment() {
            if (this.isHorizontal()) {
                this.panelA.getEl().removeClass("vertical");
                this.panelB.getEl().removeClass("vertical");
            } else {
                this.panelA.getEl().addClass("vertical");
                this.panelB.getEl().addClass("vertical");
                this.panelB.getEl().setRight("0");
                this.splitter.setVertical();
            }
        }

        private isHorizontal() {
            return this.alignment == SplitPanelAlignment.HORIZONTAL;
        }
    }

    export class SplitPanelSplitter extends api_dom.DivEl {

        private draggable:bool;

        private visible:bool;

        private splitPanel:SplitPanel;

        private ghostDragger:api_dom.DivEl;

        private dragListener:(e:MouseEvent) => void;

        private lastY:number;

        private lastX:number;

        private splitPanelOffset:number;

        private thickness:number;

        private alignment:SplitPanelAlignment;


        constructor(splitPanel:SplitPanel) {
            super("splitter");
            this.splitPanel = splitPanel;
            this.alignment = SplitPanelAlignment.HORIZONTAL;
            this.getEl().addClass("splitter");
            this.createGhostDragger();

            this.dragListener = (e:MouseEvent) => {
                if (this.isHorizontal()) {
                    this.lastY = e.clientY;
                    this.ghostDragger.getEl().setTopPx(this.lastY - this.getSplitPanelOffset());
                } else {
                    this.lastX = e.clientX;
                    this.ghostDragger.getEl().setLeft(this.lastX + "px");
                }

            };
        }

        init() {
            this.getHTMLElement().addEventListener("mousedown", (e:MouseEvent) => {
                e.preventDefault();
                this.ghostDragger.insertBeforeEl(this);
                this.startDrag();
            });

            this.getHTMLElement().parentElement.addEventListener("mouseup", (e:MouseEvent) => {
                if (this.ghostDragger.getHTMLElement().parentNode) {
                    this.stopDrag();
                    this.splitPanel.removeChild(this.ghostDragger);
                }
            })
        }

        private getSplitPanelOffset() {
            if (!this.splitPanelOffset || this.splitPanelOffset == 0) {
                this.splitPanelOffset = this.splitPanel.getCumulativeOffsetTop();
            }
            return this.splitPanelOffset;
        }

        private stopDrag() {
            this.splitPanel.getEl().removeClass("dragging");
            var aSize;
            var bSize;
            if (this.isHorizontal()) {
                aSize = (this.lastY - this.getSplitPanelOffset()) / this.splitPanel.getHTMLElement().offsetHeight * 100;
                bSize = 100 - aSize;
                this.getEl().setLeft(null);
            } else {
                aSize = (this.lastX / window.innerWidth) * 100;
                bSize = 100 - aSize;
                //this.getEl().setLeft(this.lastX + "px");
                this.updatePosition(aSize, bSize);
            }

            if (aSize != 0 && bSize != 0) {
                this.splitPanel.setDistribution(aSize, bSize);
                this.splitPanel.distribute();
            }
            this.splitPanel.getHTMLElement().removeEventListener("mousemove", this.dragListener);
        }

        private startDrag() {
            this.splitPanel.getEl().addClass("dragging");
            this.splitPanel.getHTMLElement().addEventListener("mousemove", this.dragListener);
        }

        private createGhostDragger() {
            this.ghostDragger = new api_dom.DivEl();
            this.ghostDragger.getEl().addClass("ghost-dragger");
        }

        updatePosition(a:number, b:number) {
            if (!this.isHorizontal()) {
                this.getEl().setLeft("calc(" + a + "% - " + this.getThickness() + "px)");
            }
        }

        getThickness():number {
            return this.thickness;
        }

        setThickness(thickness:number) {
            this.thickness = thickness;
            if (this.isHorizontal()) {
                this.getHTMLElement().style.height = thickness + "px";
            } else {
                this.getHTMLElement().style.width = thickness + "px";
            }
        }

        private updateThickness() {
            this.getHTMLElement().style.height = null;
            this.getHTMLElement().style.width = null;
            this.setThickness(this.thickness);
        }

        setDraggable(value:bool) {
            this.draggable = value;
        }

        setVisible(value:bool) {
            this.visible = value;
            this.setThickness(0);
        }

        setVertical() {
            this.alignment = SplitPanelAlignment.VERTICAL;
            this.getEl().addClass("vertical");
            this.ghostDragger.getEl().addClass("vertical");
            this.getEl().setLeft("calc(" + this.splitPanel.getPanelASpace() + "% - " + (this.thickness/2) + "px)");
            this.updateThickness();
        }

        setHorizontal() {
            this.alignment = SplitPanelAlignment.HORIZONTAL;
            this.getEl().removeClass("vertical");
            this.ghostDragger.getEl().removeClass("vertical");
            this.getEl().setLeft(null);
            this.updateThickness();
        }

        private isHorizontal() {
            return this.alignment == SplitPanelAlignment.HORIZONTAL;
        }
    }
}