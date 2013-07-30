module api_ui {

    /**
     * A panel having multiple child panels, but showing only one at a time - like a deck of cards.
     */
    export class DeckPanel extends Panel {

        private panels:Panel[] = [];

        private panelShown:Panel = null;

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
            panel.setDoOffset(false);
            this.appendChild(panel);
            return this.panels.push(panel) - 1;
        }

        getPanel(index:number) {
            return this.panels[index];
        }

        getLastPanel():Panel {
            return this.getPanel(this.getSize() - 1) || null;
        }

        getPanelShown():Panel {
            return this.panelShown;
        }

        private setPanelShown(panel:Panel) {
            this.panelShown = panel;
            new DeckPanelShownPanelChangedEvent(panel, this.getPanelIndex(panel)).fire();
        }

        getPanelShownIndex():number {
            return this.getPanelIndex(this.panelShown);
        }

        getPanelIndex(panel:Panel):number {
            var size = this.getSize();
            for (var i = 0; i < size; i++) {
                if (this.panels[i] === panel) {
                    return i;
                }
            }
            return -1;
        }

        /*
         * Removes panel specified by given index. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {Panel} the removed panel. Null if not was not removable.
         */
        removePanelByIndex(index:number, checkCanRemovePanel?:bool = true):Panel {
            var panelToRemove = this.getPanel(index);
            return this.removePanel(panelToRemove, checkCanRemovePanel) ? panelToRemove : null;
        }

        /*
         * Removes given panel. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {number} the index of the removed panel. -1 if it was not removable.
         */
        removePanel(panelToRemove:Panel, checkCanRemovePanel?:bool = true):number {

            var index:number = this.getPanelIndex(panelToRemove);

            if (index < 0) {
                return -1;
            }

            if (checkCanRemovePanel && !this.canRemovePanel(panelToRemove)) {
                return -1;
            }

            panelToRemove.getEl().remove();
            this.panels.splice(index, 1);

            if (this.isEmpty()) {
                this.setPanelShown(null);
            }
            else if (panelToRemove == this.getPanelShown()) {
                // show either panel that has the same index now or the last panel
                this.showPanel(Math.min(index, this.getSize() - 1));
            }

            return index;
        }

        /*
         * Override this method to decide whether given panel at given index can be removed or not. Default is true.
         */
        canRemovePanel(panel:Panel):bool {
            return true;
        }

        /*
         * Hides shown panel and makes visible a panel with given index.
         * Does nothing if there is no panel with given index.
         * Fires DeckPanelShownPanelChangedEvent if new panel was shown.
         */
        showPanel(index:number) {
            var selectedPanel = this.getPanel(index);

            if (selectedPanel == null) {
                return;
            }

            if (this.panelShown != null) {
                this.panelShown.hide();
            }

            selectedPanel.show();
            this.setPanelShown(selectedPanel);
        }
    }

    export class DeckPanelShownPanelChangedEvent extends api_event.Event {

        panel:api_ui.Panel;
        index:number;

        constructor(panel:api_ui.Panel, index:number) {
            super("deckPanelShownPanelChangedEvent");
            this.panel = panel;
            this.index = index;
        }

        static on(handler:(event:DeckPanelShownPanelChangedEvent) => void) {
            api_event.onEvent('deckPanelShownPanelChangedEvent', handler);
        }

    }
}