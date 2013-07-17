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

            app_browse.NewSchemaEvent.on(() => {
                console.log('TODO: implement handler for NewSchemaEvent');
            });

            app_browse.EditSchemaEvent.on((event) => {
                console.log('TODO: implement handler for EditSchemaEvent');
                console.log('Selected schema model: %O', event.getModels());
            });

            app_browse.OpenSchemaEvent.on((event) => {
                console.log('TODO: implement handler for OpenSchemaEvent');
                console.log('Selected schema model: %O', event.getModels());
            });

            app_browse.DeleteSchemaEvent.on((event) => {
                console.log('TODO: implement handler for DeleteSchemaEvent');
                console.log('Selected schema model: %O', event.getModels());
            });

            app_browse.ReindexSchemaEvent.on(() => {
                console.log('TODO: implement handler for ReindexSchemaEvent');
            });

            app_browse.ExportSchemaEvent.on(() => {
                console.log('TODO: implement handler for ExportSchemaEvent');
            });

        }
    }
}