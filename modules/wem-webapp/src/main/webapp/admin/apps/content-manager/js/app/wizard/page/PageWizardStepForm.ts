module app.wizard.page {

    export interface PageWizardStepFormConfig {

        liveFormPanel: LiveFormPanel;

        parentContent: api.content.Content;

        siteTemplate: api.content.site.template.SiteTemplate;

        showLiveEditAction: api.ui.Action;

    }

    export class PageWizardStepForm extends api.app.wizard.WizardStepForm {

        private liveFormPanel: LiveFormPanel;

        private parentContent: api.content.Content;

        private siteTemplate: api.content.site.template.SiteTemplate;

        private showLiveEditAction: api.ui.Action;

        private content: api.content.Content;

        private pageTemplateSelectorForm: PageTemplateSelector;

        private selectedPageTemplate: api.content.page.PageTemplate;

        private formView: api.form.FormView;

        private configFormWrapper: api.dom.DivEl;

        constructor(config: PageWizardStepFormConfig) {
            super();
            this.addClass("page-wizard-step-form");
            this.liveFormPanel = config.liveFormPanel;
            this.parentContent = config.parentContent;
            this.siteTemplate = config.siteTemplate;
            this.showLiveEditAction = config.showLiveEditAction;

            this.pageTemplateSelectorForm = new PageTemplateSelector(this);
            this.pageTemplateSelectorForm.addPageTemplateChangedListener((changedTo: api.content.page.PageTemplateSummary) => {
                this.handlePageTemplateChanged(changedTo);
            });

            this.appendChild(this.pageTemplateSelectorForm);

            this.configFormWrapper = new api.dom.DivEl();
            this.appendChild(this.configFormWrapper);
        }

        layout(content: api.content.Content, siteContent: api.content.Content): Q.Promise<void> {

            console.log("PageWizardStepForm.layout() ... ");
            var deferred = Q.defer<void>();

            this.content = content;

            var page: api.content.page.Page = content.getPage();

            if (page != null && page.getTemplate() != null) {

                this.showLiveEditAction.setEnabled(true);

                new api.content.page.GetPageTemplateByKeyRequest(page.getTemplate()).
                    setSiteTemplateKey(this.siteTemplate.getKey()).
                    sendAndParse().
                    done((pageTemplate: api.content.page.PageTemplate) => {

                        this.selectedPageTemplate = pageTemplate;
                        this.pageTemplateSelectorForm.layoutExisting(content, siteContent.getSite().getTemplateKey(), page.getTemplate()).
                            done(()=> {

                                deferred.resolve(null);
                            });
                    });
            }
            else {
                this.showLiveEditAction.setEnabled(false);

                this.pageTemplateSelectorForm.layoutExisting(content, siteContent.getSite().getTemplateKey(), null).
                    done(()=> {

                        deferred.resolve(null);
                    });
            }

            return deferred.promise;
        }

        private handlePageTemplateChanged(changedTo: api.content.page.PageTemplateSummary) {

            console.log("PageWizardStepForm.handlePageTemplateChanged() ... ");

            if (changedTo == null) {

                this.showLiveEditAction.setEnabled(false);
                this.selectedPageTemplate = null;
                this.configFormWrapper.removeChildren();

                console.log("PageWizardStepForm.handlePageTemplateChanged() ... changed to null");
            }
            else {

                console.log("PageWizardStepForm.handlePageTemplateChanged() ... changed to something (loading...)");
                new api.content.page.GetPageTemplateByKeyRequest(changedTo.getKey()).
                    setSiteTemplateKey(this.siteTemplate.getKey()).
                    sendAndParse().
                    done((pageTemplate: api.content.page.PageTemplate) => {

                        this.selectedPageTemplate = pageTemplate;

                        this.liveFormPanel.renderExisting(this.content, this.selectedPageTemplate);

                        var changedToSameAsPersisted:boolean = this.content.getPage().getTemplate().toString() == changedTo.getKey().toString();
                        this.showLiveEditAction.setEnabled(changedToSameAsPersisted);

                        new api.content.page.GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).
                            sendAndParse().
                            done((pageDescriptor: api.content.page.PageDescriptor) => {
                                this.layoutPageTemplateForm(pageTemplate, pageDescriptor);
                            });
                    });
            }
        }

        private layoutPageTemplateForm(pageTemplate: api.content.page.PageTemplate, pageDescriptor: api.content.page.PageDescriptor) {

            var formContext = new api.form.FormContextBuilder().build();

            var form = pageDescriptor.getConfig();
            var config = pageTemplate.getConfig();
            if (this.content.isPage() && this.content.getPage().hasConfig()) {
                config = this.content.getPage().getConfig();
            }
            this.formView = new api.form.FormView(formContext, form, config);
            this.configFormWrapper.removeChildren();
            this.configFormWrapper.appendChild(this.formView);

        }

        public getPageTemplate(): api.content.page.PageTemplate {
            return this.selectedPageTemplate;
        }

        public getConfig(): api.data.RootDataSet {
            if (this.formView == null) {
                return new api.data.RootDataSet();
            }
            var config = this.formView.getData();
            return  config;
        }

    }
}