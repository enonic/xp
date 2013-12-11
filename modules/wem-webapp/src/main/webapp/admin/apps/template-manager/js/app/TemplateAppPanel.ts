module app {

    export class TemplateAppPanel extends api_app.BrowseAndWizardBasedAppPanel<app_browse.TemplateBrowseItem> {

        constructor(appBar:api_app.AppBar) {

            var browsePanel = new app_browse.TemplateBrowsePanel();

            super({
                appBar: appBar,
                browsePanel: browsePanel
            });

            this.handleGlobalEvents();
        }

        private handleGlobalEvents() {

            var templateUploader = new app_import.TemplateUploader();
            var dialog = new api_ui_dialog.UploadDialog(
                "Template Importer", "Templates will be imported in application", templateUploader
            );
            templateUploader.onFinishUpload((response:api_content_site_template.InstallSiteTemplateResponse) => {
                var templates = response.getSiteTemplates();
                if (templates.length > 0) {
                    api_notify.showFeedback('Template \'' + templates.map((template:api_content_site_template.SiteTemplateSummary) => {console.log(template); return template.getDisplayName()} ).join(', ') + '\' was installed');
                }
                var errors = response.getErrors();
                if (errors.length > 0) {
                    api_notify.showError('Import errors: [' + errors.join('], [') + ']');
                }
                new api_content_site_template.SiteTemplateImportedEvent().fire();
                dialog.close();
            });
            templateUploader.onError((resp:api_rest.JsonResponse) => {
                api_notify.showError("Invalid Template file");
                dialog.close();
            });

            app_browse.ImportTemplateEvent.on(() => {
                dialog.open();
            });

        }
    }
}