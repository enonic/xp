module api_ui {

    /**
     * A panel having multiple child panels, but showing only one at a time - like a deck of cards.
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

        getPanelIndex(panel:Panel):number {
            var foundAtIndex:number = -1;
            this.panels.forEach((currPanel, index) => {
                if (panel === currPanel && foundAtIndex === -1) {
                    foundAtIndex = index;
                }
            });
            return foundAtIndex;
        }

        /*
         * Removes given panel. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {number} the index of the removed panel. -1 if it was not removable.
         */
        removePanel(panelToRemove:Panel):number {

            var panelIndex:number = this.getPanelIndex(panelToRemove);
            if (panelIndex > -1) {
                if( this.doRemovePanel(panelToRemove, panelIndex) ){
                    return panelIndex;
                }
                else {
                    return -1;
                }
            }
            return panelIndex;
        }

        /*
         * Removes panel specified by given index. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {Panel} the removed panel. Null if not was not removable.
         */
        removePanelByIndex(index:number):Panel {

            var panelToRemove = this.panels[index];

            if (this.doRemovePanel(panelToRemove, index)) {
                return panelToRemove;
            }
            else {
                return null;
            }
        }

        /*
         * Override this method to decide whether given panel at given index can be removed or not. Default is true.
         */
        canRemovePanel(panel:Panel, index:number):bool {
            return true;
        }

        private doRemovePanel(panelToRemove:Panel, index):bool {
            if (!this.canRemovePanel(panelToRemove, index)) {
                return false;
            }
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
            return true;
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
    }
}