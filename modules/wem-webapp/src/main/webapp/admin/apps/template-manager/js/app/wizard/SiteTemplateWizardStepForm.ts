module app.wizard {

    export class SiteTemplateWizardStepForm extends api.app.wizard.WizardStepForm {

        private descriptionField: api.ui.TextInput;
        private moduleComboBox: api.module.ModuleComboBox;
        private rootContentTypeComboBox: api.schema.content.ContentTypeComboBox;

        constructor() {
            super();
            this.descriptionField = api.ui.TextInput.middle().setName("description");
            this.moduleComboBox = new api.module.ModuleComboBox();
            this.rootContentTypeComboBox = new api.schema.content.ContentTypeComboBox(1);
        }

        renderNew() {
            var fieldSet = new api.ui.form.Fieldset("Site Template");
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.descriptionField).
                setLabel("Description")));
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.rootContentTypeComboBox).
                setLabel("Root Content Type").
                setValidator(api.ui.form.Validators.notEmpty)));
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.moduleComboBox).
                setLabel("Modules").
                setValidator(api.ui.form.Validators.notEmpty)));

            this.add(fieldSet);
        }

        renderExisting(siteTemplate: api.content.site.template.SiteTemplate) {

            this.renderNew();

            console.log("Rendering existing template", siteTemplate);
            this.descriptionField.setValue(siteTemplate.getDescription());

            var setRootContentTypesListener = (contentTypes: api.schema.content.ContentTypeSummary[]) => {
                var contentType: api.schema.content.ContentTypeSummary;
                for (var i = 0; i < contentTypes.length; i++) {
                    contentType = contentTypes[i];
                    if (siteTemplate.getRootContentType().equals(contentType.getContentTypeName())) {
                        this.rootContentTypeComboBox.select(contentType);
                        break;
                    }
                }
                this.rootContentTypeComboBox.removeLoadedListener(setRootContentTypesListener);
            };
            this.rootContentTypeComboBox.addLoadedListener(setRootContentTypesListener);

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
                this.moduleComboBox.removeLoadedListener(setModulesListener);
            };
            this.moduleComboBox.addLoadedListener(setModulesListener);
        }


    }
}