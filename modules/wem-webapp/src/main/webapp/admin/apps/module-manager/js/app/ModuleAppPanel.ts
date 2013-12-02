module app {

    export class ModuleAppPanel extends api_app.BrowseAndWizardBasedAppPanel<api_module.Module> {

        constructor(appBar:api_app.AppBar) {
            var browsePanel = new app_browse.ModuleBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            api_app.ShowAppBrowsePanelEvent.on((event) => {
                this.showHomePanel();
                this.getAppBarTabMenu().deselectNavigationItem();
            });

            app_browse.ImportModuleEvent.on(() => {
                var dialog = new app_import.UploadDialog();

                dialog.onFinishUpload((resp:api_module.InstallModuleResponse)=> {

                    api_notify.showFeedback('Module \'' + resp.getModules().map((modl:api_module.Module) => {console.log(modl); return modl.getDisplayName()} ).join(', ') + '\' was installed');
                    new api_module.ModuleImportedEvent().fire();
                    dialog.close();
                } ).
                onError((resp:api_rest.JsonResponse)=> {
                    api_notify.showError('Invalid Module file');
                    dialog.close();
                } );
                dialog.open();
            });

            app_browse.ExportModuleEvent.on(() => {
                var selection = components.gridPanel.getSelection()[0];
                var moduleSelected = api_module.Module.fromExtModel(selection);
                var moduleKey: api_module.ModuleKey = moduleSelected.getModuleKey();
                var exportModule = new api_module.ExportModuleRequest(moduleKey);
                var moduleExportUrl = exportModule.getRequestPath().toString() + '?moduleKey=' + moduleKey.toString();
                console.log('Download module file from: ' + moduleExportUrl);
                window.location.href = moduleExportUrl;
            });

        }

    }
}