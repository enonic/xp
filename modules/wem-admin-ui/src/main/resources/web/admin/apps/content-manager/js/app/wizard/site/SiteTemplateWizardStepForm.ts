module app.wizard.site {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import Site = api.content.site.Site;
    import Module = api.module.Module;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;
    import GetAllSiteTemplatesRequest = api.content.site.template.GetAllSiteTemplatesRequest;
    import RootDataSet = api.data.RootDataSet;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import WizardStepValidityChangedEvent = app.wizard.WizardStepValidityChangedEvent;

    export class SiteTemplateWizardStepForm extends BaseContentWizardStepForm {

        private formContext: api.content.form.ContentFormContext;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private siteTemplate: SiteTemplate;

        private siteTemplateDropdown: Dropdown<SiteTemplateSummary>;

        private siteTemplateDropdownLoaded: boolean;

        private moduleViewsContainer: api.dom.DivEl;

        private moduleViews: ModuleView[] = [];

        private moduleViewsValid: {[moduleKey: string]: boolean} = {};

        private previousValidation: boolean = false;

        private siteTemplateChangedListeners: {(event: SiteTemplateChangedEvent) : void}[] = [];

        constructor(siteTemplate: SiteTemplate) {
            super("site-wizard-step-form");

            this.siteTemplate = siteTemplate;

            this.siteTemplateDropdown = new Dropdown<SiteTemplateSummary>('siteTemplate', <DropdownConfig<SiteTemplateSummary>>{
                optionDisplayValueViewer: new api.content.site.template.SiteTemplateSummaryViewer()
            });
            this.siteTemplateDropdown.addClass('site-template-combo');

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.addClass('site-template-field');
            fieldSet.add(new api.ui.form.FormItemBuilder(this.siteTemplateDropdown).setLabel('Site Template').build());
            this.appendChild(fieldSet);
            this.loadSiteTemplateDropdown().then(()=> {
                this.siteTemplateDropdown.onOptionSelected((event: OptionSelectedEvent<SiteTemplate>) => this.handleSiteTemplateComboBoxOptionSelected(event));
            });

            this.moduleViewsContainer = new api.dom.DivEl();
            this.appendChild(this.moduleViewsContainer);
        }

        private loadSiteTemplateDropdown(): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            new GetAllSiteTemplatesRequest().sendAndParse()
                .then((siteTemplates: SiteTemplateSummary[]) => {
                    var selecteSiteTemplateKey = this.siteTemplate ? this.siteTemplate.getKey().toString() : '';
                    var selectedSiteOption: Option<SiteTemplateSummary> = null;
                    siteTemplates.forEach((siteTemplate: SiteTemplateSummary) => {
                        var option = {
                            value: siteTemplate.getKey().toString(),
                            displayValue: siteTemplate
                        };
                        this.siteTemplateDropdown.addOption(option);
                        if (option.value === selecteSiteTemplateKey) {
                            selectedSiteOption = option;
                        }
                    });

                    if (selectedSiteOption) {
                        this.siteTemplateDropdown.selectOption(selectedSiteOption);
                    }

                    this.siteTemplateDropdownLoaded = true;
                    deferred.resolve(null);
                });

            return deferred.promise;
        }

        private handleSiteTemplateComboBoxOptionSelected(event: OptionSelectedEvent<SiteTemplate>): void {
            this.removeExistingModuleViews();

            this.siteTemplate = event.getOption().displayValue;
            var moduleConfigs: api.content.site.ModuleConfig[] = [];
            this.siteTemplate.getModules().forEach((moduleKey: api.module.ModuleKey) => {
                var moduleConfig = new api.content.site.ModuleConfigBuilder().
                    setModuleKey(moduleKey).
                    setConfig(new RootDataSet()).
                    build();
                moduleConfigs.push(moduleConfig);
            });
            this.doLayout(moduleConfigs).then(() => {
                this.notifySiteTemplateChanged(this.siteTemplate);
            });
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

        public layout(context: api.content.form.ContentFormContext, site: Site): wemQ.Promise<void> {
            this.setFormContext(context);
            return this.doLayout(site.getModuleConfigs());
        }

        private doLayout(moduleConfigs: api.content.site.ModuleConfig[]): wemQ.Promise<void> {
            this.setModuleConfigs(moduleConfigs);

            if (this.siteTemplateDropdownLoaded) {
                var option = this.siteTemplateDropdown.getOptionByValue(this.siteTemplate.getKey().toString());
                var selectedOption = this.siteTemplateDropdown.getSelectedOption();
                if (!selectedOption || (selectedOption.value !== option.value)) {
                    this.siteTemplateDropdown.selectOption(option, true);
                }
            }

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

        public setFormContext(formContext: api.content.form.ContentFormContext) {
            this.formContext = formContext;
        }

        private loadModules(moduleConfigs: api.content.site.ModuleConfig[]): wemQ.Promise<Module[]> {

            var moduleRequestPromises = moduleConfigs.map((moduleConfig: api.content.site.ModuleConfig) => {
                return new api.module.GetModuleRequest(moduleConfig.getModuleKey()).sendAndParse();
            });

            return wemQ.allSettled(moduleRequestPromises).then((results: wemQ.PromiseState<Module>[])=> {
                return results.filter((result: wemQ.PromiseState<Module>) => (result.state == "fulfilled")).
                    map((result: wemQ.PromiseState<Module>) => result.value);
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
