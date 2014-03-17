module app.contextwindow.inspect {

    export class PageInspectionPanel extends BaseInspectionPanel {

        private page: api.content.Content;

        private formView: api.form.FormView;
        private pageSelectorEl: api.dom.Element;
        private currentPageTemplate: api.content.page.PageTemplateKey;

        constructor() {
            super("live-edit-font-icon-page", false);
            this.formView = null;
            this.pageSelectorEl = null;
            this.currentPageTemplate = null;
        }

        setPage(pageContent: api.content.Content, pageTemplate: api.content.page.PageTemplate,
                pageDescriptor: api.content.page.PageDescriptor) {
            this.page = pageContent;


            this.setupPageTemplateSelector(pageContent, pageTemplate, pageDescriptor);
            this.setupForm(pageContent, pageTemplate, pageDescriptor);
        }

        private setupPageTemplateSelector(pageContent: api.content.Content, pageTemplate: api.content.page.PageTemplate,
                                          pageDescriptor: api.content.page.PageDescriptor) {
            if (this.pageSelectorEl) {
                this.removeChild(this.pageSelectorEl);
                this.currentPageTemplate = null;
            }
            if (!pageContent) {
                return;
            }

            var containerForm = new api.ui.form.Form('form-view');
            containerForm.setDoOffset(false);
            var pageTemplateSelector = new app.wizard.page.PageTemplateSelector(containerForm);
            pageTemplateSelector.addPageTemplateChangedListener((selectedPageTemplate: api.content.page.PageTemplateSummary) => {
                if (!selectedPageTemplate || !selectedPageTemplate.getKey().equals(this.currentPageTemplate)) {
                    this.handlePageTemplateChanged(selectedPageTemplate, pageContent);
                }
            });

            pageTemplateSelector.layoutExisting(pageContent, pageContent.getSite().getTemplateKey(), pageTemplate.getKey());
            this.currentPageTemplate = pageTemplate.getKey();

            this.pageSelectorEl = containerForm;
            this.appendChild(containerForm);
        }

        private setupForm(pageContent: api.content.Content, pageTemplate: api.content.page.PageTemplate,
                          pageDescriptor: api.content.page.PageDescriptor) {
            if (this.formView) {
                this.removeChild(this.formView);
            }
            if (!pageContent) {
                return;
            }

            var formContext = new api.form.FormContextBuilder().build();
            var form = pageDescriptor.getConfig();
            var config: api.data.RootDataSet;
            if (pageContent.getPage().hasConfig()) {
                config = pageContent.getPage().getConfig();
            } else {
                config = pageTemplate.getConfig();
            }

            this.formView = new api.form.FormView(formContext, form, config);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }

        private handlePageTemplateChanged(selectedPageTemplate: api.content.page.PageTemplateSummary, pageContent: api.content.Content) {
            this.removeChild(this.formView);
            this.formView = null;
            this.currentPageTemplate = null;

            if (selectedPageTemplate) {
                var siteTemplateKey = pageContent.getSite().getTemplateKey();
                var getPageTemplatePromise: Q.Promise<api.content.page.PageTemplate> = new api.content.page.GetPageTemplateByKeyRequest(selectedPageTemplate.getKey()).
                    setSiteTemplateKey(siteTemplateKey).sendAndParse();

                getPageTemplatePromise.done((pageTemplate: api.content.page.PageTemplate) => {
                    new api.content.page.GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).sendAndParse().
                        done((pageDescriptor: api.content.page.PageDescriptor) => {
                            this.setupPageTemplateSelector(pageContent, pageTemplate, pageDescriptor);
                            this.setupForm(pageContent, pageTemplate, pageDescriptor);
                        });
                });
            }
        }

    }
}