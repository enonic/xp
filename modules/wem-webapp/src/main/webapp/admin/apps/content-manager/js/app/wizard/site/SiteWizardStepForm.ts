module app.wizard.site {

    export class SiteWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: api.form.FormContext;

        private contentType: api.schema.content.ContentType;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private templateView: TemplateView;

        private moduleViewsContainer: api.dom.DivEl;

        private moduleViews: ModuleView[] = [];

        constructor() {
            super("site-wizard-step-form");

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

            this.formContext = context;
            this.contentType = contentType;

            this.moduleConfigsByKey = [];
            var moduleConfigs: api.content.site.ModuleConfig[] = site.getModuleConfigs();
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }

            return new api.content.site.template.GetSiteTemplateRequest(site.getTemplateKey()).
                sendAndParse().
                then((siteTemplate: api.content.site.template.SiteTemplate) => {

                    this.templateView.setValue(siteTemplate, this.contentType);
                    return this.loadModules(site);

                }).then((modules: api.module.Module[]):void => {

                    this.layoutModules(modules);

                });
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

        private loadModules(site: api.content.site.Site): Q.Promise<api.module.Module[]> {

            var moduleRequestPromises = site.getModuleConfigs().map((moduleConfig: api.content.site.ModuleConfig) => {
                return new api.module.GetModuleRequest(moduleConfig.getModuleKey()).sendAndParse();
            });

            return Q.allSettled(moduleRequestPromises).then((results: Q.PromiseState<api.module.Module>[])=> {
                var modules: api.module.Module[] = [];
                results.forEach((result: Q.PromiseState<api.module.Module>)=> {
                    if (result.state == "fulfilled") {
                        modules.push(result.value);
                    }
                });
                return modules;
            });
        }

        private layoutModules(modules: api.module.Module[]) {

            this.removeExistingModuleViews();
            this.moduleViewsContainer.removeChildren();

            modules.forEach((theModule: api.module.Module) => {
                if (theModule.getForm().getFormItems().length == 0) {
                    return;
                }

                var moduleView = new ModuleView(this.formContext, theModule,
                    this.moduleConfigsByKey[theModule.getModuleKey().toString()]);

                this.moduleViewsContainer.appendChild(moduleView);
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
