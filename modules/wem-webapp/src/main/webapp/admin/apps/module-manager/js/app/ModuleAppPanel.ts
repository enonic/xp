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

            var moduleUploader = new app.imp.ModuleUploader();
            var dialog = new api.ui.dialog.UploadDialog(
                "Module Importer", "Modules will be imported in application", moduleUploader
            );
            moduleUploader.onFinishUpload((resp:api.module.InstallModuleResponse)=> {
                var modules = resp.getModules();
                if (modules.length > 0) {
                    api.notify.showFeedback('Module \'' + modules.map((modl:api.module.Module) => {console.log(modl); return modl.getDisplayName()} ).join(', ') + '\' was installed');
                }
                var errors = resp.getErrors();
                if (errors.length > 0) {
                    api.notify.showError('Import errors: [' + errors.join('], [') + ']');
                }
                new api.module.ModuleImportedEvent().fire();
                dialog.close();
            });
            moduleUploader.onError((resp:api.rest.JsonResponse<api.module.ModuleSummary>)=> {
                api.notify.showError('Invalid Module file');
                dialog.close();
            });

            app.browse.ImportModuleEvent.on(() => {
                dialog.open();
            });

            app.browse.ExportModuleEvent.on(() => {
                var selection = components.gridPanel.getSelection()[0];
                var moduleSelected = api.module.ModuleSummary.fromExtModel(selection);
                var moduleKey: api.module.ModuleKey = moduleSelected.getModuleKey();
                var exportModule = new api.module.ExportModuleRequest(moduleKey);
                var moduleExportUrl = exportModule.getRequestPath().toString() + '?moduleKey=' + moduleKey.toString();
                console.log('Download module file from: ' + moduleExportUrl);
                window.location.href = moduleExportUrl;
            });

        }

    }
}