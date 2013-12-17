module app {

    export class ModuleAppPanel extends api_app.BrowseAndWizardBasedAppPanel<api_module.ModuleSummary> {

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

            var moduleUploader = new app_import.ModuleUploader();
            var dialog = new api_ui_dialog.UploadDialog(
                "Module Importer", "Modules will be imported in application", moduleUploader
            );
            moduleUploader.onFinishUpload((resp:api_module.InstallModuleResponse)=> {
                var modules = resp.getModules();
                if (modules.length > 0) {
                    api_notify.showFeedback('Module \'' + modules.map((modl:api_module.Module) => {console.log(modl); return modl.getDisplayName()} ).join(', ') + '\' was installed');
                }
                var errors = resp.getErrors();
                if (errors.length > 0) {
                    api_notify.showError('Import errors: [' + errors.join('], [') + ']');
                }
                new api_module.ModuleImportedEvent().fire();
                dialog.close();
            });
            moduleUploader.onError((resp:api_rest.JsonResponse<api_module.ModuleSummary>)=> {
                api_notify.showError('Invalid Module file');
                dialog.close();
            });

            app_browse.ImportModuleEvent.on(() => {
                dialog.open();
            });

            app_browse.ExportModuleEvent.on(() => {
                var selection = components.gridPanel.getSelection()[0];
                var moduleSelected = api_module.ModuleSummary.fromExtModel(selection);
                var moduleKey: api_module.ModuleKey = moduleSelected.getModuleKey();
                var exportModule = new api_module.ExportModuleRequest(moduleKey);
                var moduleExportUrl = exportModule.getRequestPath().toString() + '?moduleKey=' + moduleKey.toString();
                console.log('Download module file from: ' + moduleExportUrl);
                window.location.href = moduleExportUrl;
            });

        }

    }
}