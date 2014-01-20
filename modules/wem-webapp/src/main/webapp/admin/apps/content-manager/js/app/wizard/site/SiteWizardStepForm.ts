module app.wizard.site {

    export class SiteWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: api.form.FormContext;

        private contentType: api.schema.content.ContentType;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private templateView: TemplateView;

        private moduleViewsContainer: api.dom.DivEl;

        private moduleViews: ModuleView[] = [];

        constructor() {
            super();
            this.addClass("site-wizard-step-form");

            this.templateView = new TemplateView();
            this.appendChild(this.templateView);
            this.moduleViewsContainer = new api.dom.DivEl();
            this.appendChild(this.moduleViewsContainer);
        }

        public renderNew() {
            //TODO
        }

        public renderExisting(context: api.form.FormContext, site: api.content.site.Site,
                              contentType: api.schema.content.ContentType): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.formContext = context;
            this.contentType = contentType;

            this.moduleConfigsByKey = [];
            var moduleConfigs: api.content.site.ModuleConfig[] = site.getModuleConfigs();
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }

            new api.content.site.template.GetSiteTemplateRequest(site.getTemplateKey()).
                sendAndParse().
                done((siteTemplate: api.content.site.template.SiteTemplate) => {

                    this.templateView.setValue(siteTemplate, this.contentType);

                    this.loadModules(site, (modules: api.module.Module[]) => {

                        this.removeExistingModuleViews();
                        this.layoutModules(modules);

                        deferred.resolve(null);
                    });
                });

            return deferred.promise;
        }

        public getTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.templateView.getSiteTemplateKey();
        }

        public getModuleConfigs(): api.content.site.ModuleConfig[] {
            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            var moduleViews = this.moduleViewsContainer.getChildren();
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

            modules.forEach((theModule: api.module.Module) => {

                var moduleView = new ModuleView(this.formContext, theModule,
                    this.moduleConfigsByKey[theModule.getModuleKey().toString()]);

                this.moduleViewsContainer.appendChild(moduleView)
                this.moduleViews.push(moduleView);
            });
        }

        private removeExistingModuleViews() {
            this.moduleViews.forEach((moduleView: ModuleView) => {
                moduleView.remove();
            });
            this.moduleViews = [];
        }

    }

}
