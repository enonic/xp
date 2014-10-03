module app.wizard.site {

    import Site = api.content.site.Site;
    import Module = api.module.Module;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import RootDataSet = api.data.RootDataSet;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import WizardStepValidityChangedEvent = app.wizard.WizardStepValidityChangedEvent;

    export class SiteTemplateWizardStepForm extends BaseContentWizardStepForm {

        private formContext: api.content.form.ContentFormContext;

        private moduleConfigsByKey: api.content.site.ModuleConfig[];

        private moduleViewsContainer: api.dom.DivEl;

        private moduleViews: ModuleView[] = [];

        private moduleViewsValid: {[moduleKey: string]: boolean} = {};

        constructor() {
            super("site-wizard-step-form");

            //var fieldSet = new api.ui.form.Fieldset();
            //fieldSet.addClass('site-template-field');
            //fieldSet.add(new api.ui.form.FormItemBuilder(this.siteTemplateDropdown).setLabel('Site Template').build());
            //this.appendChild(fieldSet);

            this.moduleViewsContainer = new api.dom.DivEl();
            this.appendChild(this.moduleViewsContainer);
        }

        public displayValidationErrors(display: boolean) {
            this.moduleViews.forEach((moduleView: ModuleView) => {
                moduleView.getFormView().displayValidationErrors(display);
            })
        }

        public validate(silent?: boolean): api.form.ValidationRecording {
            var recording = new api.form.ValidationRecording();
            this.moduleViews.forEach((moduleView: ModuleView) => {
                recording.flatten(moduleView.getFormView().validate(silent));
            });
            this.previousValidation = recording;
            return recording;
        }

        private setModuleConfigs(moduleConfigs: api.content.site.ModuleConfig[]): void {
            this.moduleConfigsByKey = [];
            for (var i = 0; i < moduleConfigs.length; i++) {
                this.moduleConfigsByKey[moduleConfigs[i].getModuleKey().toString()] = moduleConfigs[i];
            }
        }

        public layout(context: api.content.form.ContentFormContext, site: Site): wemQ.Promise<void> {
            this.setFormContext(context);
            return this.doLayout(site);
        }

        private doLayout(site: Site): wemQ.Promise<void> {

            this.setModuleConfigs(site.getModuleConfigs());


            /*if (this.siteTemplateDropdownLoaded) {
             var option = this.siteTemplateDropdown.getOptionByValue(this.siteTemplate.getKey().toString());
             var selectedOption = this.siteTemplateDropdown.getSelectedOption();
             if (!selectedOption || (selectedOption.value !== option.value)) {
             this.siteTemplateDropdown.selectOption(option, true);
             }
             }*/

            return this.loadModules(site.getModuleConfigs()).
                then((modules: Module[]): void => {
                    this.layoutModules(modules);
                });
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

                    if (!this.previousValidation) {
                        this.previousValidation = event.getRecording();
                        this.notifyValidityChanged(new WizardStepValidityChangedEvent(this.previousValidation.isValid()));
                    } else {
                        var previousValidState = this.previousValidation.isValid();

                        if (event.isValid()) {
                            // TODO: no origin in FormValidityChangedEvent
                            // this.previousValidation.removeByPath(event.getOrigin())
                        } else {
                            this.previousValidation.flatten(event.getRecording());
                        }

                        if (previousValidState != this.previousValidation.isValid()) {
                            this.notifyValidityChanged(new WizardStepValidityChangedEvent(this.previousValidation.isValid()))
                        }
                    }

                });

                this.moduleViewsContainer.appendChild(moduleView);
                this.moduleViews.push(moduleView);
                var moduleKey = moduleView.getModuleConfig().getModuleKey().toString();
                this.moduleViewsValid[moduleKey] = false;
            });
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
