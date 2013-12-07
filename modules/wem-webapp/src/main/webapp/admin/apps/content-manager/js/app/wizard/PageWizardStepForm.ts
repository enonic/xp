module app_wizard {

    export interface PageWizardStepFormConfig {

        parentContent: api_content.Content;

        siteContent: api_content.Content;

    }

    export class PageWizardStepForm extends api_app_wizard.WizardStepForm {

        private parentContent: api_content.Content;

        private siteContent: api_content.Content;

        private pageTemplateComboBox:api_ui_combobox.ComboBox<api_content_page.PageTemplate>;

        private form: api_form.Form;

        private formView: api_form.FormView;

        constructor(config: PageWizardStepFormConfig) {
            super("PageWizardStepForm");
            this.parentContent = config.parentContent;

            // TODO
            var pageTemplateComboBoxConfig:api_ui_combobox.ComboBoxConfig<api_content_page.PageTemplate> = <api_ui_combobox.ComboBoxConfig<api_content_page.PageTemplate>> {
                maximumOccurrences: 1
            };
            this.pageTemplateComboBox = new api_ui_combobox.ComboBox<api_content_page.PageTemplate>("template", pageTemplateComboBoxConfig);
            // listen to selection in pageTemplateComboBox and add/replace this.form with form from selected PageTemplate.descriptor.form
        }

        renderNew() {

            // request all page templates in existing in SiteTemplate of siteContent
            // add page templates to pageTemplateComboBox
        }

        renderExisting(content: api_content.Content, pageTemplate: api_content_page.PageTemplate) {

            // request all page templates in existing in SiteTemplate of siteContent
            // add page templates to pageTemplateComboBox
            // ensure given pageTemplate is selected in  pageTemplateComboBox
            // ensure Form from selected pageTemplate descriptor is displayed below combobox
        }



    }
}
