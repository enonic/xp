module api_ui {

    /**
     * TODO: Implement remaining functionality.
     */
    export class DeckPanel extends Panel {

        private panels:Panel[] = [];

        private panelShown:number = -1;

        constructor(idPrefix?:string) {
            super(idPrefix || "DeckPanel");
        }

        getSize():number {
            return this.panels.length;
        }

        /**
         * Add new Panel to the deck.
         * @param panel
         * @returns {number} The index for the added Panel.
         */
            addPanel(panel:Panel):number {
            panel.hide();
            this.appendChild(panel);
            return this.panels.push(panel) - 1;
        }

        getPanel(index:number) {
            return this.panels[index];
        }

        removePanel(index:number):Panel {

            var panel = this.panels[index];
            panel.getEl().remove();
            var lastPanel = this.panels.length == index + 1;
            this.panels.splice(index, 1);

            if (this.panels.length == 0) {
                return panel;
            }

            if (this.isShownPanel(index)) {

                if (!lastPanel) {
                    this.panels[this.panels.length - 1].show();
                    this.panelShown = this.panels.length - 1;
                }
                else {
                    this.panels[index-1].show();
                }
            }
            return panel;
        }

        private isShownPanel(panelIndex:number):bool {
            return this.panelShown === panelIndex;
        }

        showPanel(index:number) {
            for (var i:number = 0; i < this.panels.length; i++) {
                var panel = this.panels[i];
                if (i === index) {
                    panel.show();
                    this.panelShown = index;
                }
                else {
                    panel.hide();
                }
            }
        }

        getPanels():Panel[] {
            return this.panels;
        }
    }
}