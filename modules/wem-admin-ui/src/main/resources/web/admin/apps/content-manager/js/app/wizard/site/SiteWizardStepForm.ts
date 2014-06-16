module app.wizard.site {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ContentType = api.schema.content.ContentType;
    import Site = api.content.site.Site;
    import Module = api.module.Module;

    export class SiteWizardStepForm extends api.app.wizard.WizardStepForm {

        private formContext: api.form.FormContext;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private siteTemplate: SiteTemplate;

        private siteTemplateView: SiteTemplateView;

        private moduleViewsContainer: api.dom.DivEl;

        private moduleViews: ModuleView[] = [];

        constructor(siteTemplate: SiteTemplate, contentType: ContentType) {
            super("site-wizard-step-form");

            this.siteTemplate = siteTemplate;
            this.siteTemplateView = new SiteTemplateView(contentType);
            this.appendChild(this.siteTemplateView);
            this.moduleViewsContainer = new api.dom.DivEl();
            this.appendChild(this.moduleViewsContainer);
        }

        public renderExisting(context: api.form.FormContext, site: Site): Q.Promise<void> {

            this.formContext = context;

            this.moduleConfigsByKey = [];
            var moduleConfigs: api.content.site.ModuleConfig[] = site.getModuleConfigs();
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }

            this.siteTemplateView.setValue(this.siteTemplate);

            return this.loadModules(site).
                then((modules: Module[]): void => {

                    this.layoutModules(modules);
                });
        }

        public getTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.siteTemplate.getKey();
        }

        public getModuleConfigs(): api.content.site.ModuleConfig[] {
            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            var moduleViews = this.moduleViewsContainer.getChildren();
            for (var i = 0; i < moduleViews.length; i++) {
                moduleConfigs.push((<ModuleView>moduleViews[i]).getModuleConfig());
            }
            return moduleConfigs;
        }

        private loadModules(site: Site): Q.Promise<Module[]> {

            var moduleRequestPromises = site.getModuleConfigs().map((moduleConfig: api.content.site.ModuleConfig) => {
                return new api.module.GetModuleRequest(moduleConfig.getModuleKey()).sendAndParse();
            });

            return Q.allSettled(moduleRequestPromises).then((results: Q.PromiseState<Module>[])=> {
                return results.filter((result: Q.PromiseState<Module>) => (result.state == "fulfilled")).
                    map((result:Q.PromiseState<Module>) => result.value);
            });
        }

        private layoutModules(modules: Module[]) {

            this.removeExistingModuleViews();
            this.moduleViewsContainer.removeChildren();

            modules.forEach((theModule: Module) => {
                if (theModule.getForm().getFormItems().length == 0) {
                    return;
                }

                var moduleView = new ModuleView(this.formContext, theModule,
                    this.moduleConfigsByKey[theModule.getModuleKey().toString()]);

                moduleView.onFocus((event: FocusEvent) => {
                    this.notifyFocused(event);
                });
                moduleView.onBlur((event: FocusEvent) => {
                    this.notifyBlurred(event);
                });

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
