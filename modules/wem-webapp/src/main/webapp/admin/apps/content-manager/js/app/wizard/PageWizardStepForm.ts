module app.wizard {

    export interface PageWizardStepFormConfig {

        parentContent: api.content.Content;

        siteContent: api.content.Content;

    }

    export class PageWizardStepForm extends api.app.wizard.WizardStepForm {

        private parentContent: api.content.Content;

        private siteContent: api.content.Content;

        private pageTemplateComboBox:api.ui.combobox.ComboBox<api.content.page.PageTemplate>;

        private form: api.form.Form;

        private formView: api.form.FormView;

        constructor(config: PageWizardStepFormConfig) {
            super("PageWizardStepForm");
            this.parentContent = config.parentContent;

            // TODO
            var pageTemplateComboBoxConfig:api.ui.combobox.ComboBoxConfig<api.content.page.PageTemplate> = <api.ui.combobox.ComboBoxConfig<api.content.page.PageTemplate>> {
                maximumOccurrences: 1
            };
            this.pageTemplateComboBox = new api.ui.combobox.ComboBox<api.content.page.PageTemplate>("template", pageTemplateComboBoxConfig);
            // listen to selection in pageTemplateComboBox and add/replace this.form with form from selected PageTemplate.descriptor.form
        }

        renderNew() {

            // request all page templates in existing in SiteTemplate of siteContent
            // add page templates to pageTemplateComboBox
        }

        renderExisting(content: api.content.Content, pageTemplate: api.content.page.PageTemplate) {

            // request all page templates in existing in SiteTemplate of siteContent
            // add page templates to pageTemplateComboBox
            // ensure given pageTemplate is selected in  pageTemplateComboBox
            // ensure Form from selected pageTemplate descriptor is displayed below combobox
        }



    }
}
