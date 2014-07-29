module api.ui.panel {

    /**
     * A panel having multiple child panels, but showing only one at a time - like a deck of cards.
     */
    export class DeckPanel extends Panel {

        private panels: Panel[] = [];

        private panelShown: Panel = null;

        private panelShownListeners: {(event: PanelShownEvent):void}[] = [];

        constructor(className?: string) {
            super("deck-panel" + (className ? " " + className : ""));
        }

        isEmpty(): boolean {
            return this.panels.length == 0;
        }

        getSize(): number {
            return this.panels.length;
        }

        /*
         * Add new Panel to the deck.
         * @param panel
         * @returns {number} The index for the added Panel.
         */
        addPanel<T extends Panel>(panel: T): number {
            panel.hide();
            panel.setDoOffset(false);
            this.appendChild(panel);
            return this.panels.push(panel) - 1;
        }

        getPanel(index: number) {
            return this.panels[index];
        }

        getLastPanel(): Panel {
            return this.getPanel(this.getSize() - 1) || null;
        }

        getPanelShown(): Panel {
            return this.panelShown;
        }

        getPanelShownIndex(): number {
            return this.getPanelIndex(this.panelShown);
        }

        getPanelIndex<T extends Panel>(panel: T): number {
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
        removePanelByIndex(index: number, checkCanRemovePanel: boolean = true): Panel {
            var panelToRemove = this.getPanel(index);
            return this.removePanel(panelToRemove, checkCanRemovePanel) ? panelToRemove : null;
        }

        /*
         * Removes given panel. Method canRemovePanel will be called to know if specified panel is allowed to be removed.
         * @returns {number} the index of the removed panel. -1 if it was not removable.
         */
        removePanel(panelToRemove: Panel, checkCanRemovePanel: boolean = true): number {

            var index: number = this.getPanelIndex(panelToRemove);

            if (index < 0 || checkCanRemovePanel && !this.canRemovePanel(panelToRemove)) {
                return -1;
            }

            panelToRemove.remove();
            this.panels.splice(index, 1);

            if (this.isEmpty()) {
                this.panelShown = null;
            }
            else if (panelToRemove == this.getPanelShown()) {
                // show either panel that has the same index now or the last panel
                this.showPanelByIndex(Math.min(index, this.getSize() - 1));
            }

            return index;
        }

        /*
         * Override this method to decide whether given panel at given index can be removed or not. Default is true.
         */
        canRemovePanel(panel: Panel): boolean {
            return true;
        }

        showPanel(panel: Panel) {
            var index = this.getPanelIndex(panel);
            if (index > -1) {
                this.showPanelByIndex(index);
            }
        }

        showPanelByIndex(index: number) {
            var previousPanel = this.getPanelShown();
            var panelToShow = this.getPanel(index);

            if (panelToShow == null) {
                return;
            }

            if (this.panelShown != null) {
                this.panelShown.hide();
            }

            panelToShow.show();
            this.panelShown = panelToShow;
            this.notifyPanelShown(panelToShow, index, previousPanel);
        }

        onPanelShown(listener: (event: PanelShownEvent)=>void) {
            this.panelShownListeners.push(listener);
        }

        unPanelShown(listener: (event: PanelShownEvent)=>void) {
            this.panelShownListeners = this.panelShownListeners.filter((currentListener: (event: PanelShownEvent) => void) => {
                return  listener != currentListener;
            });
        }

        private notifyPanelShown(panel: Panel, panelIndex: number, previousPanel: Panel) {
            this.panelShownListeners.forEach((listener: (event: PanelShownEvent) => void) => {
                listener.call(this, new PanelShownEvent(panel, panelIndex, previousPanel));
            });
        }
    }
}