module api_ui {
    export class SplitPanel extends api_ui.Panel {
        private panelA:api_ui.Panel;

        private panelB:api_ui.Panel;

        private panelASpace:number;

        private panelBSpace:number;

        private splitter:SplitPanelSplitter;

        private alignment;

        constructor() {
            super();
            this.getEl().addClass("split-panel");
            this.splitter = new SplitPanelSplitter();
            this.appendChild(this.splitter);
        }

         setPanelA(panel:api_ui.Panel):void {
            this.panelA = panel;
            this.panelA.insertBeforeEl(this.splitter);
        }

        setPanelB(panel:api_ui.Panel):void {
            this.panelB = panel;
            this.panelB.insertAfterEl(this.splitter);
        }

        setDistribution(aSpace:number, bSpace:number) {
            this.panelASpace = aSpace;
            this.panelBSpace = bSpace;
        }

        getSplitter():SplitPanelSplitter {
            return this.splitter;
        }
    }

    export class SplitPanelSplitter extends api_dom.DivEl {
        private draggable:bool;
        private visible:bool;

        constructor() {
            super("splitter");
        }

        setDraggable(value:bool) {
            this.draggable = value;
        }

        setVisible(value:bool) {
            this.visible = value;

        }
    }
}