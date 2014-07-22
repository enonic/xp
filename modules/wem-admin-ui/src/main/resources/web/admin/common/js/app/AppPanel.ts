module api.app {

    export class AppPanel<M> extends api.ui.NavigatedDeckPanel {

        private browsePanel: api.app.browse.BrowsePanel<M>;

        constructor(tabNavigator: AppBarTabMenu) {
            super(tabNavigator);
        }

        addBrowsePanel(browsePanel: api.app.browse.BrowsePanel<M>) {
            // limit to 1 browse panel
            if (!this.browsePanel) {
                var browseMenuItem = new AppBarTabMenuItem("home", new AppBarTabId("hidden", "____home"));
                browseMenuItem.setVisibleInMenu(false);
                browseMenuItem.setRemovable(false);
                this.addNavigablePanel(browseMenuItem, browsePanel, true);
                this.browsePanel = browsePanel;
            }
        }

        getBrowsePanel(): api.app.browse.BrowsePanel<M> {
            return this.browsePanel;
        }

        removePanelByIndex(index: number): api.ui.Panel {
            var panel = super.removePanelByIndex(index);
            this.checkBrowsePanelNeedsToBeShown(index, panel);
            return panel;
        }

        removePanel(panel: api.ui.Panel): number {
            var index = super.removePanel(panel);
            this.checkBrowsePanelNeedsToBeShown(index, panel);
            return index;
        }

        private checkBrowsePanelNeedsToBeShown(index: number, panel: api.ui.Panel) {
            if (panel == this.browsePanel && index > -1) {
                this.browsePanel = undefined;
            } else if (this.getSize() == 0) {
                // show browse panel if all others were removed
                new api.app.ShowBrowsePanelEvent().fire();
            }
        }
    }
}
