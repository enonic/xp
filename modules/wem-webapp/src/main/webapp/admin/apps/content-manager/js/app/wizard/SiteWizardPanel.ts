module app_wizard {

    export class SiteWizardPanel extends ContentWizardPanel {

        private site:api_content_site.Site;

        private siteModule:api_module.Module;

        private siteTemplate:api_content_site_template.SiteTemplateSummary;

        private siteWizardStepForm:SiteWizardStepForm;

        constructor(tabId:api_app.AppBarTabId, contentType:api_schema_content.ContentType, parentContent:api_content.Content, persistedContent:api_content.Content, siteContent:api_content.Content) {

            this.site = siteContent.getSite();
            this.siteWizardStepForm = new SiteWizardStepForm();

            super(tabId, contentType, parentContent, persistedContent, siteContent);
        }

        createSteps(): api_app_wizard.WizardStep[] {
            var steps: api_app_wizard.WizardStep[] = super.createSteps();
            steps.unshift(new api_app_wizard.WizardStep("Site", this.siteWizardStepForm));
            return steps;
        }

        renderNew() {
            super.renderNew();

            this.siteWizardStepForm.renderNew();
        }

        setPersistedItem(content: api_content.Content) {
            super.setPersistedItem(content);

            var formContext = new api_form.FormContextBuilder().
                setParentContent(this.getParentContent()).
                setPersistedContent(content).
                build();

            this.siteWizardStepForm.renderExisting(formContext, content.getSite());
        }

        persistNewItem(successCallback?: () => void) {
            var content: api_content.Content = this.getPersistedItem();
            if (content) {
                new api_content_site.CreateSiteRequest(content.getId())
                    .setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey())
                    .setModuleConfigs(this.siteWizardStepForm.getModuleConfigs())
                    .send().done((createResponse: api_rest.JsonResponse<api_content_json.ContentJson>) => {

                        api_notify.showFeedback('Site was created!');
                    });
            }
        }

        updatePersistedItem(successCallback?: () => void) {
            var content: api_content.Content = this.getPersistedItem();
            new api_content_site.UpdateSiteRequest(content.getId())
                .setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey())
                .setModuleConfigs(this.siteWizardStepForm.getModuleConfigs())
                .send().done((upateResponse: api_rest.JsonResponse<api_content_json.ContentJson>) => {

                    api_notify.showFeedback('Site was updated!');
                });
        }
    }

}