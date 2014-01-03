module app.wizard.site {

    export class SiteWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: api.form.FormContext;

        private contentType: api.schema.content.ContentType;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private moduleViewsCtr: api.dom.DivEl;

        private templateViewCtr: api.dom.DivEl;

        constructor() {
            super("SiteWizardStepForm");

            this.templateViewCtr = new api.dom.DivEl();
            this.appendChild(this.templateViewCtr);
            this.moduleViewsCtr = new api.dom.DivEl();
            this.appendChild(this.moduleViewsCtr);
        }

        public renderNew() {
            //TODO
        }

        public renderExisting(context: api.form.FormContext, site: api.content.site.Site, contentType: api.schema.content.ContentType, callback:Function) {
            this.formContext = context;
            this.contentType = contentType;

            this.moduleConfigsByKey = [];
            var moduleConfigs: api.content.site.ModuleConfig[] = site.getModuleConfigs();
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }

            this.loadModules(site, (modules: api.module.Module[]) => {
                this.layoutModules(modules);

                this.loadSiteTemplate(site, (template: api.content.site.template.SiteTemplate) => {
                    this.layoutSiteTemplate(template);
                    callback();
                });
            });
        }

        public getTemplateKey(): api.content.site.template.SiteTemplateKey {
            return (<TemplateView>this.templateViewCtr.getFirstChild()).getSiteTemplateKey();
        }

        public getModuleConfigs(): api.content.site.ModuleConfig[] {
            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            var moduleViews = this.moduleViewsCtr.getChildren();
            for (var i = 0; i < moduleViews.length; i++) {
                moduleConfigs.push((<ModuleView>moduleViews[i]).getModuleConfig());
            }
            return moduleConfigs;
        }

        private loadModules(site: api.content.site.Site, callback: (siteModules: api.module.Module[]) => void) {
            var moduleConfigs: api.content.site.ModuleConfig[] = site.getModuleConfigs();
            var moduleRequests = [];
            for (var i = 0; i < moduleConfigs.length; i++) {
                var request = new api.module.GetModuleRequest(moduleConfigs[i].getModuleKey()).send();
                moduleRequests.push(request);
            }
            // Using .apply() here to pass array of requests as arguments enum
            jQuery.when.apply(jQuery, moduleRequests).then((moduleResponses: api.rest.JsonResponse<api.module.json.ModuleJson>[]) => {
                var modules = [];
                // Make array in case there's only one response
                moduleResponses = new Array<api.rest.JsonResponse<api.module.json.ModuleJson>>().concat(moduleResponses);
                for (var i = 0; i < moduleResponses.length; i++) {
                    modules.push(new api.module.Module(moduleResponses[i].getResult()));
                }
                callback(modules);
            })
        }

        private layoutModules(modules: api.module.Module[]) {
            this.moduleViewsCtr.removeChildren();
            modules.forEach((theModule: api.module.Module) => {
                var moduleView = new ModuleView(this.formContext, theModule,
                    this.moduleConfigsByKey[theModule.getModuleKey().toString()]);
                this.moduleViewsCtr.appendChild(moduleView)
            });
        }

        private loadSiteTemplate(site: api.content.site.Site, callback: (siteTemplate: api.content.site.template.SiteTemplate) => void) {
            new api.content.site.template.GetSiteTemplateRequest(site.getTemplateKey()).send()
                .done((templateResponse: api.rest.JsonResponse<api.content.site.template.json.SiteTemplateJson>) => {
                    var template = new api.content.site.template.SiteTemplate(templateResponse.getResult());
                    callback(template);
                }
            );
        }

        private layoutSiteTemplate(template: api.content.site.template.SiteTemplate) {
            this.templateViewCtr.removeChildren();
            this.templateViewCtr.appendChild(new TemplateView(template, this.contentType))
        }

    }

}
