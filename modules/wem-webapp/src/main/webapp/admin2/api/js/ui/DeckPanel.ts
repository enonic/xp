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

        isEmpty():bool {
            return this.panels.length == 0;
        }

        getSize():number {
            return this.panels.length;
        }

        /*
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

        getLastPanel():Panel {
            return this.isEmpty() ? null : this.panels[this.panels.length - 1];
        }

        getPanelShown():Panel {
            return this.panels[this.panelShown];
        }

        getPanelShownIndex():number {
            return this.panelShown;
        }

        removePanel(index:number):Panel {

            var panelToRemove = this.panels[index];
            panelToRemove.getEl().remove();
            var removingLastPanel:bool = this.panels.length == index + 1;
            var panelToRemoveIsShown:bool = this.isShownPanel(index);

            this.panels.splice(index, 1);

            if (this.isEmpty()) {
                this.panelShown = -1;
            }
            else if (panelToRemoveIsShown) {

                if (removingLastPanel) {
                    this.getLastPanel().show();
                    this.panelShown = this.panels.length - 1;
                }
                else {
                    this.panels[index].show();
                }
            }

            return panelToRemove;
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