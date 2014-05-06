module app {

    export class ModuleAppPanel extends api.app.BrowseAndWizardBasedAppPanel<api.module.ModuleSummary> {

        constructor(appBar:api.app.AppBar) {
            var browsePanel = new app.browse.ModuleBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            api.app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

        }

    }
}