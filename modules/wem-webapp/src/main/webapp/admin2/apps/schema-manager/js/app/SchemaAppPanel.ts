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

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.appBarTabMenu.deselectNavigationItem();
            });

            api_ui_tab.TabMenuItemSelectEvent.on((event) => {
                this.appBarTabMenu.hideMenu();
                this.selectPanel(event.getTab());
            });

            api_ui_tab.TabMenuItemCloseEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                var panel = this.getPanel(tabIndex);
                new app_browse.CloseSchemaEvent(panel, true).fire();
            });
            
            app_browse.NewSchemaEvent.on(() => {
                console.log('TODO: implement handler for NewSchemaEvent');
            });

            app_browse.EditSchemaEvent.on((event) => {

                console.log('TODO: implement handler for EditSchemaEvent');
                console.log('Selected schema model: %O', event.getModels());

                //TODO: uncomment when wizard is ready

                /*var schemes:api_model.SchemaModel[] = event.getModels();
                 for (var i = 0; i < schemes.length; i++) {
                 var schemaModel:api_model.SchemaModel = schemes[i];

                 var schemaGetParams:api_remote.RemoteCallContentTypeGetParams = {
                 "contentType": schemaModel.data.name,
                 "format": 'JSON'
                 };
                 api_remote.RemoteService.contentType_get(schemaGetParams, (result:api_remote.RemoteCallContentTypeGetResult) => {

                 if (result && result.success) {

                 var tabMenuItem = new SchemaAppBarTabMenuItem(result.contentType.displayName);

                 var id = this.generateTabId(result.contentType.name, true);
                 var schemaWizardPanel = new app_wizard.SchemaWizardPanel(id);
                 schemaWizardPanel.setPersistedItem(result.contentType);

                 this.addWizardPanel(tabMenuItem, schemaWizardPanel);
                 this.selectPanel(tabMenuItem);
                 } else {
                 console.error("Error", result ? result.error : "Unable to retrieve space.");
                 }
                 });
                 }*/
            });

            app_browse.OpenSchemaEvent.on((event) => {
                var schemes:api_model.SchemaModel[] = event.getModels();
                for (var i = 0; i < schemes.length; i++) {
                    var schemaModel:api_model.SchemaModel = schemes[i];

                    var tabMenuItem = new SchemaAppBarTabMenuItem(schemaModel.data.displayName);

                    var id = this.generateTabId(schemaModel.data.name, false);
                    var schemaItemViewPanel = new app_browse.SchemaItemViewPanel(id);

                    var spaceItem = new api_app_browse.BrowseItem(schemaModel)
                        .setDisplayName(schemaModel.data.displayName)
                        .setPath(schemaModel.data.name)
                        .setIconUrl(schemaModel.data.iconUrl);

                    schemaItemViewPanel.setItem(spaceItem);

                    this.addNavigationItem(tabMenuItem, schemaItemViewPanel);
                    this.selectPanel(tabMenuItem);
                }
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

            app_browse.CloseSchemaEvent.on((event) => {
                this.removePanel(event.getPanel(), event.isCheckCanRemovePanel());
            });

        }

        private generateTabId(shemaName, isEdit) {
            return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + shemaName;
        }        
        
    }
}