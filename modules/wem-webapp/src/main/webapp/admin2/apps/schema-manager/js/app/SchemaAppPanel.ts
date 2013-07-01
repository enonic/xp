module app {

    export class SchemaAppPanel extends api_app.AppPanel {

        private appBrowsePanel:app_browse.SchemaAppBrowsePanel;

        private appBarTabMenu:api_app.AppBarTabMenu;

        constructor(appBar:api_app.AppBar) {
            this.appBrowsePanel = new app_browse.SchemaAppBrowsePanel();
            this.appBarTabMenu = appBar.getTabMenu();

            super(this.appBarTabMenu, this.appBrowsePanel, app_browse.SchemaBrowseActions.ACTIONS);

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            // TODO: setup event hanlers

        }
    }
}