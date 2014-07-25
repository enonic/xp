module app.wizard.site {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import Site = api.content.site.Site;
    import Module = api.module.Module;
    import SiteTemplateComboBox = api.content.site.template.SiteTemplateComboBox;
    import RootDataSet = api.data.RootDataSet;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import WizardStepValidityChangedEvent = app.wizard.WizardStepValidityChangedEvent;

    export class SiteTemplateWizardStepForm extends BaseContentWizardStepForm {

        private formContext: api.form.FormContext;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private siteTemplate: SiteTemplate;

        private siteTemplateComboBox: SiteTemplateComboBox;

        private moduleViewsContainer: api.dom.DivEl;

        private moduleViews: ModuleView[] = [];

        private moduleViewsValid: {[moduleKey: string]: boolean} = {};

        private previousValidation: boolean = false;

        private siteTemplateChangedListeners: {(event: SiteTemplateChangedEvent) : void}[] = [];

        constructor(siteTemplate: SiteTemplate) {
            super("site-wizard-step-form");

            this.siteTemplate = siteTemplate;
            this.siteTemplateComboBox = new SiteTemplateComboBox();
            this.siteTemplateComboBox.addClass('site-template-combo');
            if (siteTemplate) {
                this.siteTemplateComboBox.select(siteTemplate);
            }
            this.siteTemplateComboBox.onOptionSelected((event: OptionSelectedEvent<SiteTemplate>) => this.handleSiteTemplateComboBoxOptionSelected(event));
            this.siteTemplateComboBox.onSelectedOptionRemoved((removed: SelectedOption<SiteTemplate>) => this.handleSelectedOptionRemoved());
            this.appendChild(this.siteTemplateComboBox);

            this.moduleViewsContainer = new api.dom.DivEl();
            this.appendChild(this.moduleViewsContainer);
        }

        private handleSiteTemplateComboBoxOptionSelected(event: OptionSelectedEvent<SiteTemplate>): void {
            this.siteTemplate = event.getOption().displayValue;
            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            this.siteTemplate.getModules().forEach((moduleKey: api.module.ModuleKey) => {
                var moduleConfig = new api.content.site.ModuleConfigBuilder().
                    setModuleKey(moduleKey).
                    setConfig(new RootDataSet()).
                    build();
                moduleConfigs.push(moduleConfig);
            });
            this.doRenderExisting(moduleConfigs).then(() => {
                this.notifySiteTemplateChanged(this.siteTemplate);
            });
        }

        private handleSelectedOptionRemoved(): void {
            this.siteTemplate = null;
            this.removeExistingModuleViews();
            this.notifySiteTemplateChanged(null);
        }

        onSiteTemplateChanged(listener: (event: SiteTemplateChangedEvent) => void) {
            this.siteTemplateChangedListeners.push(listener);
        }

        unSiteTemplateChanged(listener: (event: SiteTemplateChangedEvent) => void) {
            this.siteTemplateChangedListeners = this.siteTemplateChangedListeners.filter((curr) => {
                return curr !== listener;
            })
        }

        private notifySiteTemplateChanged(siteTemplate: SiteTemplate) {
            var shownEvent = new SiteTemplateChangedEvent(siteTemplate);
            this.siteTemplateChangedListeners.forEach((listener) => {
                listener(shownEvent);
            });
        }

        private setModuleConfigs(moduleConfigs: api.content.site.ModuleConfig[]): void {
            this.moduleConfigsByKey = [];
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }
        }

        public renderExisting(context: api.form.FormContext, site: Site): Q.Promise<void> {
            this.setFormContext(context);
            return this.doRenderExisting(site.getModuleConfigs());
        }

        private doRenderExisting(moduleConfigs: api.content.site.ModuleConfig[]): Q.Promise<void> {
            this.setModuleConfigs(moduleConfigs);

            this.siteTemplateComboBox.select(this.siteTemplate);

            return this.loadModules(moduleConfigs).
                then((modules: Module[]): void => {

                    this.layoutModules(modules);
                });
        }

        public getTemplateKey(): api.content.site.template.SiteTemplateKey {
            return this.siteTemplate && this.siteTemplate.getKey();
        }

        public getModuleConfigs(): api.content.site.ModuleConfig[] {
            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            var moduleViews = this.moduleViewsContainer.getChildren();
            for (var i = 0; i < moduleViews.length; i++) {
                moduleConfigs.push((<ModuleView>moduleViews[i]).getModuleConfig());
            }
            return moduleConfigs;
        }

        public setFormContext(formContext: api.form.FormContext) {
            this.formContext = formContext;
        }

        private loadModules(moduleConfigs: api.content.site.ModuleConfig[]): Q.Promise<Module[]> {

            var moduleRequestPromises = moduleConfigs.map((moduleConfig: api.content.site.ModuleConfig) => {
                return new api.module.GetModuleRequest(moduleConfig.getModuleKey()).sendAndParse();
            });

            return Q.allSettled(moduleRequestPromises).then((results: Q.PromiseState<Module>[])=> {
                return results.filter((result: Q.PromiseState<Module>) => (result.state == "fulfilled")).
                    map((result: Q.PromiseState<Module>) => result.value);
            });
        }

        private layoutModules(modules: Module[]) {

            this.removeExistingModuleViews();

            modules.forEach((theModule: Module) => {
                if (theModule.getForm().getFormItems().length == 0) {
                    return;
                }

                var moduleConfig: api.content.site.ModuleConfig = this.moduleConfigsByKey[theModule.getModuleKey().toString()];
                var moduleView = new ModuleView(this.formContext, theModule, moduleConfig);
                moduleView.onEditContentRequest((content: api.content.ContentSummary) => {
                    new app.browse.EditContentEvent([content]).fire();
                });

                moduleView.onFocus((event: FocusEvent) => {
                    this.notifyFocused(event);
                });
                moduleView.onBlur((event: FocusEvent) => {
                    this.notifyBlurred(event);
                });
                moduleView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                    var moduleKey = moduleView.getModuleConfig().getModuleKey().toString();
                    this.moduleViewsValid[moduleKey] = event.isValid();
                    var allModuleFormsValid = this.areAllModuleFormsValid();
                    if (this.previousValidation !== allModuleFormsValid) {
                        this.notifyValidityChanged(new WizardStepValidityChangedEvent(allModuleFormsValid));
                    }
                    this.previousValidation = allModuleFormsValid;
                });

                this.moduleViewsContainer.appendChild(moduleView);
                this.moduleViews.push(moduleView);
                var moduleKey = moduleView.getModuleConfig().getModuleKey().toString();
                this.moduleViewsValid[moduleKey] = false;
            });
            this.previousValidation = this.areAllModuleFormsValid();
        }

        private areAllModuleFormsValid(): boolean {
            return this.moduleViews.every((mod: ModuleView) => this.moduleViewsValid[mod.getModuleConfig().getModuleKey().toString()]);
        }

        private removeExistingModuleViews() {
            this.moduleViews.forEach((moduleView: ModuleView) => {
                moduleView.remove();
            });
            this.moduleViews = [];
            this.moduleViewsValid = {};
            this.moduleViewsContainer.removeChildren();
        }

    }

}
