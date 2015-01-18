module api.app {

    export class AppPanel<M extends api.Equitable> extends api.ui.panel.NavigatedDeckPanel {

        private browsePanel: api.app.browse.BrowsePanel<M>;

        constructor(tabNavigator: api.app.bar.AppBarTabMenu) {
            super(tabNavigator);
        }

        addBrowsePanel(browsePanel: api.app.browse.BrowsePanel<M>) {
            // limit to 1 browse panel
            if (!this.browsePanel) {
                var browseMenuItem = new api.app.bar.AppBarTabMenuItemBuilder().setLabel("[Select]").
                    setTabId(new api.app.bar.AppBarTabId("hidden", "____home")).
                    build();
                browseMenuItem.setVisibleInMenu(false);
                browseMenuItem.setRemovable(false);
                this.addNavigablePanel(browseMenuItem, browsePanel, true);
                this.browsePanel = browsePanel;
            }
        }

        getBrowsePanel(): api.app.browse.BrowsePanel<M> {
            return this.browsePanel;
        }

        removeNavigablePanel(panel: api.ui.panel.Panel, checkCanRemovePanel: boolean = true): number {
            var index = super.removeNavigablePanel(panel, checkCanRemovePanel);
            this.checkBrowsePanelNeedsToBeShown(index, panel);
            return index;
        }

        private checkBrowsePanelNeedsToBeShown(index: number, panel: api.ui.panel.Panel) {
            if (panel == this.browsePanel && index > -1) {
                this.browsePanel = undefined;
            } else if (this.getSize() == 0) {
                // show browse panel if all others were removed
                new api.app.bar.event.ShowBrowsePanelEvent().fire();
            }
        }
    }
}
