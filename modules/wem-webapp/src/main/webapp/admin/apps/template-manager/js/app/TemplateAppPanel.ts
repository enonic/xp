module app {

    export class TemplateAppPanel extends api_app.BrowseAndWizardBasedAppPanel<app_browse.TemplateBrowseItem> {

        constructor(appBar:api_app.AppBar) {

            var browsePanel = new app_browse.TemplateBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });
        }

    }
}