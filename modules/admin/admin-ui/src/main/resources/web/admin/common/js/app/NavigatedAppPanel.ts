module api.app {

    import NavigationItem = api.ui.NavigationItem;
    import Panel = api.ui.panel.Panel;
    import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
    import NavigatorEvent = api.ui.NavigatorEvent;

    export class NavigatedAppPanel<M extends api.Equitable> extends AppPanel<M> {

        private appBarTabMenu: api.app.bar.AppBarTabMenu;

        private appBar: api.app.bar.AppBar;

        constructor(appBar: api.app.bar.TabbedAppBar) {
            super('navigated-panel');

            this.appBar = appBar;

            this.appBarTabMenu = appBar.getTabMenu();

            this.appBarTabMenu.onNavigationItemSelected((event: NavigatorEvent) => {
                this.showPanelByIndex(event.getItem().getIndex());
            });
        }

        selectPanel(item: NavigationItem) {
            this.selectPanelByIndex(item.getIndex());
        }

        selectPanelByIndex(index: number) {
            this.appBarTabMenu.selectNavigationItem(index);
            // panel will be shown because of the selected navigator listener in constructor
        }

        addNavigablePanel(item: AppBarTabMenuItem, panel: Panel, select?: boolean) {
            this.appBarTabMenu.addNavigationItem(item);
            let index = this.addPanel(panel);
            if (select) {
                this.selectPanelByIndex(index);
            }
            return index;
        }

        removeNavigablePanel(panel: Panel, checkCanRemovePanel: boolean = true): number {
            let index = this.removePanel(panel, checkCanRemovePanel);
            if (index > -1) {
                let navigationItem: AppBarTabMenuItem = <AppBarTabMenuItem>this.appBarTabMenu.getNavigationItem(index);
                this.appBarTabMenu.removeNavigationItem(navigationItem);
            }

            this.checkBrowsePanelNeedsToBeShown(index, panel);

            return index;
        }

        getAppBarTabMenu(): api.app.bar.AppBarTabMenu {
            return this.appBarTabMenu;
        }

        addViewPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, viewPanel: api.app.view.ItemViewPanel<M>) {
            this.addNavigablePanel(tabMenuItem, viewPanel, true);

            viewPanel.onClosed((event: api.app.view.ItemViewClosedEvent<M>) => {
                this.removeNavigablePanel(event.getView(), false);
            });
        }

        addWizardPanel(tabMenuItem: api.app.bar.AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<any>) {
            this.addNavigablePanel(tabMenuItem, wizardPanel, true);

            wizardPanel.onClosed((event: api.app.wizard.WizardClosedEvent) => {
                this.removeNavigablePanel(event.getWizard(), false);
            });
        }

        canRemovePanel(panel: api.ui.panel.Panel): boolean {
            if (api.ObjectHelper.iFrameSafeInstanceOf(panel, api.app.wizard.WizardPanel)) {
                let wizardPanel: api.app.wizard.WizardPanel<any> = <api.app.wizard.WizardPanel<any>>panel;
                return wizardPanel.canClose();
            }
            return true;
        }

        protected addBrowsePanel(browsePanel: api.app.browse.BrowsePanel<M>) {
            if (!this.browsePanel) {
                this.browsePanel = browsePanel;

                let browseMenuItem = new api.app.bar.AppBarTabMenuItemBuilder().setLabel('<Select>').setTabId(
                    new api.app.bar.AppBarTabId('hidden', '____home')).build();
                browseMenuItem.setVisibleInMenu(false);
                this.addNavigablePanel(browseMenuItem, browsePanel);

                this.currentKeyBindings = api.ui.Action.getKeyBindings(this.resolveActions(browsePanel));
                this.activateCurrentKeyBindings();
            }
        }

        protected resolveActions(panel: api.ui.panel.Panel): api.ui.Action[] {
            let actions = super.resolveActions(panel);
            return actions.concat(this.appBar.getActions());
        }

        private checkBrowsePanelNeedsToBeShown(index: number, panel: api.ui.panel.Panel) {
            if (panel == this.browsePanel && index > -1) {
                this.browsePanel = undefined;
            } else if (this.getSize() == 0) {
                // show browse panel if all others were removed
                new ShowBrowsePanelEvent().fire();
            }
        }
    }
}
