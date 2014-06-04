module app.wizard {

    export class SiteTemplateWizardStepForm extends api.app.wizard.WizardStepForm {

        private descriptionField: api.ui.TextInput;
        private moduleComboBox: api.module.ModuleComboBox;

        constructor() {
            super();
            this.descriptionField = api.ui.TextInput.middle().setName("description");
            this.moduleComboBox = new api.module.ModuleComboBox();
        }

        renderNew() {
            var fieldSet = new api.ui.form.Fieldset();
            var descriptionField = new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.descriptionField).
                setLabel("Description"));
            descriptionField.getInput().wrapWithElement(new api.dom.DivEl("input-wrapper"));
            fieldSet.add(descriptionField);
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.moduleComboBox).
                setLabel("Modules").
                setValidator(api.ui.form.Validators.notEmpty)));

            this.add(fieldSet);
        }

        renderExisting(siteTemplate: api.content.site.template.SiteTemplate) {

            this.renderNew();

            this.descriptionField.setValue(siteTemplate.getDescription());

            var setModulesListener = (modules: api.module.ModuleSummary[]) => {
                siteTemplate.getModules().forEach((moduleKey: api.module.ModuleKey) => {
                    var aModule: api.module.ModuleSummary;
                    for (var i = 0; i < modules.length; i++) {
                        aModule = modules[i];
                        if (moduleKey.equals(aModule.getModuleKey())) {
                            this.moduleComboBox.select(aModule);
                            break;
                        }
                    }
                });
                this.moduleComboBox.unLoaded(setModulesListener);
            };
            this.moduleComboBox.onLoaded(setModulesListener);
        }


    }
}