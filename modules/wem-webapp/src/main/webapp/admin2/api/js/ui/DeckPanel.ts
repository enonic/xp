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
            this.panels.splice(index, 1);

            if (this.panelShown === index) {
                if (this.panels.length < index + 1) {
                    this.panels[this.panels.length].show();
                    this.panelShown = this.panels.length - 1;
                }
                else {
                    this.panels[index].show();
                }
            }
            return panel;
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
    }
}