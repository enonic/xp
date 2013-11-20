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
                new api_module.InstallModuleRequest().send().done((resp:api_rest.JsonResponse)=> {
                    api_notify.showFeedback('Module \'' + resp.getJson().result.displayName + '\' was installed');
                } ).fail((resp:api_rest.JsonResponse)=> {
                    api_notify.showError('Invalid Module file');
                } );
            });


        }

    }
}