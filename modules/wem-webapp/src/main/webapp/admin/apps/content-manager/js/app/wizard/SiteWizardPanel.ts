module app_wizard {

    export class SiteWizardPanel extends ContentWizardPanel {

        private site:api_content_site.Site;

        private siteModule:api_module.Module;

        private siteTemplate:api_content_site_template.SiteTemplateSummary;

        private siteWizardStepForm:SiteWizardStepForm;

        constructor(tabId:api_app.AppBarTabId, contentType:api_schema_content.ContentType, parentContent:api_content.Content) {

            this.siteWizardStepForm = new SiteWizardStepForm();

            super(tabId, contentType, parentContent);
        }

        createSteps():api_app_wizard.WizardStep[] {
            var steps:api_app_wizard.WizardStep[] = super.createSteps();
            steps.unshift(new api_app_wizard.WizardStep("Site", this.siteWizardStepForm));
            return steps;
        }

        renderNew() {
            super.renderNew();
        }

        setPersistedItem(content:api_content.Content) {
            super.setPersistedItem(content);
        }

        persistNewItem(successCallback?:() => void) {
            var content:api_content.Content = this.getPersistedItem();
            new api_content_site.CreateSiteRequest(content.getId())
                .setSiteTemplateKey(this.site.getTemplateName())
                .setModuleConfigs(this.site.getModuleConfigs())
                .send().done((siteResponse:api_rest.JsonResponse<api_content_site_json.SiteJson>) => {
                    //TODO
            });
        }

        updatePersistedItem(successCallback?:() => void) {
            var content:api_content.Content = this.getPersistedItem();
            new api_content_site.UpdateSiteRequest(content.getId())
                .setSiteTemplateId(this.site.getTemplateName())
                .setModuleConfigs(this.site.getModuleConfigs())
                .send().done((siteResponse:api_rest.JsonResponse<api_content_site_json.SiteJson>) => {
                    //TODO
            });
        }

        private getSite(callback:(site:api_content_site.Site) => void) {
            if(this.site) {
                callback(this.site);
            } else {
                var content:api_content.Content = this.getPersistedItem();
                new api_content_site.GetSiteRequest(content.getId() )
                    .send().done((siteResponse:api_rest.JsonResponse<api_content_site_json.SiteJson>) => {
                    this.site = new api_content_site.Site(siteResponse.getResult());
                    callback(this.site);
                })
            }
        }

        private getModule(callback:(siteModule:api_module.Module) => void) {
            if( this.siteModule ) {
                callback(this.siteModule);
            } else {
                var content:api_content.Content = this.getPersistedItem();
                new api_module.GetModuleRequest(content.getId() ).send().done((moduleResponse:api_rest.JsonResponse) => {
                    this.siteModule = new api_module.Module(moduleResponse.getResult());
                    callback(this.siteModule);
                });
            }
        }


        private getSiteTemplate(callback:(siteTemplate:api_content_site_template.SiteTemplateSummary) => void) {
            if(this.siteTemplate) {
                callback(this.siteTemplate)
            } else {
                var content:api_content.Content = this.getPersistedItem();
                new api_content_site_template.GetSiteTemplateRequest(content.getId()).send().done((templateResponse:api_rest.JsonResponse<api_content_site_template_json.SiteTemplateSummaryJson>) => {
                    this.siteTemplate = new api_content_site_template.SiteTemplateSummary(templateResponse.getResult());
                    callback(this.siteTemplate)
                });
            }
        }

    }

}