module app_wizard {

    export class SiteWizardPanelParams extends ContentWizardPanelParams {

    }

    export class SiteWizardPanel extends ContentWizardPanel {

        private persistedSite: api_content_site.Site;

        private siteModule: api_module.Module;

        private siteTemplate: api_content_site_template.SiteTemplateSummary;

        private siteWizardStepForm: SiteWizardStepForm;

        constructor(params: SiteWizardPanelParams, callback:(wizard:SiteWizardPanel) => void) {

            this.persistedSite = params.persistedContent.getSite();
            this.siteWizardStepForm = new SiteWizardStepForm();

            super(params, () => {
                callback(this);
            });
        }

        createSteps(): api_app_wizard.WizardStep[] {
            var steps: api_app_wizard.WizardStep[] = super.createSteps();
            steps.unshift(new api_app_wizard.WizardStep("Site", this.siteWizardStepForm));
            return steps;
        }

        postRenderNew(callBack:Function) {
            super.postRenderNew(() => {
                this.siteWizardStepForm.renderNew();
                callBack();
            });
        }

        setPersistedItem(content: api_content.Content, callback:Function) {
            super.setPersistedItem(content, () => {
                var formContext = new api_form.FormContextBuilder().
                    setParentContent(this.getParentContent()).
                    setPersistedContent(content).
                    build();

                this.siteWizardStepForm.renderExisting(formContext, content.getSite(), this.getContentType(), () => {
                    callback();
                });
            });
        }

        persistNewItem(callback: (persistedContent:api_content.Content) => void) {
            var content: api_content.Content = this.getPersistedItem();
            if (content) {
                new api_content_site.CreateSiteRequest(content.getId())
                    .setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey())
                    .setModuleConfigs(this.siteWizardStepForm.getModuleConfigs())
                    .send().done((createResponse: api_rest.JsonResponse<api_content_json.ContentJson>) => {

                        api_notify.showFeedback('Site was created!');
                        callback(null);
                    });
            }
        }

        updatePersistedItem(callback: (persistedContent:api_content.Content) => void) {
            var content: api_content.Content = this.getPersistedItem();
            new api_content_site.UpdateSiteRequest(content.getId())
                .setSiteTemplateKey(this.siteWizardStepForm.getTemplateKey())
                .setModuleConfigs(this.siteWizardStepForm.getModuleConfigs())
                .send().done((upateResponse: api_rest.JsonResponse<api_content_json.ContentJson>) => {

                    api_notify.showFeedback('Site was updated!');
                    callback(null);
                });
        }
    }

}