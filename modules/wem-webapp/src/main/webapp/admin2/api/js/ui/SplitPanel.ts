module api_ui {
    export class SplitPanel extends api_ui.Panel {
        private panelA:api_ui.Panel;

        private panelB:api_ui.Panel;

        private panelASpace:number;

        private panelBSpace:number;

        private splitter:SplitPanelSplitter;

        private alignment;

        constructor(panelA:api_ui.Panel, panelB:api_ui.Panel) {
            super();
            this.getEl().addClass("split-panel");
            this.splitter = new SplitPanelSplitter(this);
            this.splitter.setHeight(5);
            this.panelA = panelA;
            this.panelB = panelB;
            this.panelASpace = 50;
            this.panelBSpace = 50;

            this.panelA.setDoOffset(false);
            this.panelB.setDoOffset(false);

            this.appendChild(this.panelA);
            this.appendChild(this.splitter);
            this.appendChild(this.panelB);

            this.splitter.init();
        }

        setDistribution(aSpace:number, bSpace:number) {
            this.panelASpace = aSpace;
            this.panelBSpace = bSpace;
        }

        getSplitter():SplitPanelSplitter {
            return this.splitter;
        }

        distribute() {
            this.panelA.getHTMLElement().style.height = "calc(" + this.panelASpace + "% - " + this.splitter.getHeight() / 2 + "px)";
            this.panelB.getHTMLElement().style.height = "calc(" + this.panelBSpace + "% - " + this.splitter.getHeight() / 2 + "px)";
        }

        render() {
            this.distribute();
        }
    }

    export class SplitPanelSplitter extends api_dom.DivEl {

        private draggable:bool;

        private visible:bool;

        private splitPanel:SplitPanel;

        private ghostDragger:api_dom.DivEl;

        private dragListener:(e:MouseEvent) => void;

        private lastY:number;


        constructor(splitPanel:SplitPanel) {
            super("splitter");
            this.splitPanel = splitPanel;
            this.getEl().addClass("splitter");
            this.createGhostDragger();
            this.dragListener = (e:MouseEvent) => {
                this.lastY = e.clientY;
                this.ghostDragger.getEl().setTopPx(this.lastY);
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

        private stopDrag() {
            this.splitPanel.getEl().removeClass("dragging");
            var aSize = this.lastY / this.splitPanel.getHTMLElement().offsetHeight * 100;
            var bSize = 100 - aSize;
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

        getHeight():number {
            return this.getHTMLElement().offsetHeight;
        }

        setHeight(height:number) {
            this.getHTMLElement().style.height = height + "px";
        }

        setDraggable(value:bool) {
            this.draggable = value;
        }

        setVisible(value:bool) {
            this.visible = value;
            this.setHeight(0);
        }
    }
}