module app.wizard.page {

    export interface PageWizardStepFormConfig {

        parentContent: api.content.Content;

        siteContent: api.content.Content;

    }

    export class PageWizardStepForm extends api.app.wizard.WizardStepForm {

        private parentContent: api.content.Content;

        private siteContent: api.content.Content;

        private pageTemplateForm: PageTemplateSelectorForm;

        private formView: api.form.FormView;

        constructor(config: PageWizardStepFormConfig) {
            super("PageWizardStepForm");
            this.parentContent = config.parentContent;
            this.siteContent = config.siteContent;

            this.pageTemplateForm = new PageTemplateSelectorForm();
            this.pageTemplateForm.addPageTemplateChangedListener((changedTo: api.content.page.PageTemplateSummary) => {
                this.handlePageTemplateChanged(changedTo);
            });

            this.appendChild(this.pageTemplateForm);
        }

        private handlePageTemplateChanged(changedTo: api.content.page.PageTemplateSummary) {

            if (this.formView != null) {
                this.formView.remove();
            }

            if (changedTo == null) {

            }
            else {
                new api.content.page.GetPageTemplateByKeyRequest(changedTo.getKey()).
                    sendAndParse().
                    done((pageTemplate: api.content.page.PageTemplate) => {

                        this.layoutPageTemplateForm(pageTemplate);
                    });
            }
        }

        private layoutPageTemplateForm(pageTemplate: api.content.page.PageTemplate) {

            var formContext = new api.form.FormContextBuilder().build();

            var form = pageTemplate.getDescriptor().getForm();
            var config = pageTemplate.getConfig();
            this.formView = new api.form.FormView(formContext, form, config);
            this.appendChild(this.formView);
        }

        renderNew() {

            // request all page templates in existing in SiteTemplate of siteContent
            // add page templates to pageTemplateComboBox
        }

        renderExisting(content: api.content.Content): Q.Promise<api.content.page.PageTemplate> {

            var deferred = Q.defer<api.content.page.PageTemplate>();

            var page: api.content.page.Page = content.getPage();

            if( !content.isPage() ) {
                deferred.resolve(null);
                return deferred.promise;
            }

            new api.content.page.GetPageTemplateByKeyRequest(page.getTemplate()).
                sendAndParse().
                done((pageTemplate: api.content.page.PageTemplate) => {

                    new api.content.page.GetPageTemplatesRequest(this.siteContent.getSite().getTemplateKey()).
                        sendAndParse().
                        done((pageTemplates: api.content.page.PageTemplateSummary[]) => {

                            this.pageTemplateForm.layoutExisting(pageTemplates, pageTemplate);

                            // ensure given pageTemplate is selected in  pageTemplateComboBox
                            // ensure Form from selected pageTemplate descriptor is displayed below combobox

                            deferred.resolve(pageTemplate);
                        });
                });

            return deferred.promise;
        }

    }
}
