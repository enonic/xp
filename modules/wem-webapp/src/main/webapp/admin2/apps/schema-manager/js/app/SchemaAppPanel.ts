module app {

    export class SchemaAppPanel extends api_app.AppPanel {

        private browsePanel:app_browse.SchemaBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:api_app.AppBar) {
            this.browsePanel = new app_browse.SchemaBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(this.appBarTabMenu, this.browsePanel, app_browse.SchemaBrowseActions.ACTIONS);

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            // TODO: setup event hanlers

        }
    }
}