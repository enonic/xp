module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
    import FormContextBuilder = api.form.FormContextBuilder;
    import FormView = api.form.FormView;
    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import Content = api.content.Content;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import Page = api.content.page.Page;
    import PageBuilder = api.content.page.PageBuilder;
    import PageTemplate = api.content.page.PageTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplateSummary = api.content.page.PageTemplateSummary;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import PageDescriptor = api.content.page.PageDescriptor;

    export interface PageInspectionPanelConfig {

        contentType:ContentTypeName;

        siteTemplateKey:SiteTemplateKey;
    }

    export class PageInspectionPanel extends BaseInspectionPanel {

        private siteTemplateKey: SiteTemplateKey;

        private content: Content;

        private formView: FormView;

        private pageTemplateSelector: PageTemplateSelector;

        private pageSelectorEl: api.dom.Element;

        private currentPageTemplate: PageTemplateKey;

        constructor(config: PageInspectionPanelConfig) {
            super("live-edit-font-icon-page", false);

            this.siteTemplateKey = config.siteTemplateKey;

            this.formView = null;
            this.pageSelectorEl = null;
            this.currentPageTemplate = null;

            var containerForm = new api.ui.form.Form('form-view');
            containerForm.setDoOffset(false);

            this.pageTemplateSelector = new PageTemplateSelector({
                form: containerForm,
                contentType: config.contentType,
                siteTemplateKey: config.siteTemplateKey});

            this.pageSelectorEl = containerForm;
            this.appendChild(containerForm);

            this.pageTemplateSelector.onPageTemplateChanged((pageTemplate: PageTemplateSummary) => {

                if (!pageTemplate || !pageTemplate.getKey().equals(this.currentPageTemplate)) {
                    this.handlePageTemplateChanged(pageTemplate);
                }
            });
        }

        private handlePageTemplateChanged(selectedPageTemplate: PageTemplateSummary) {

            this.removeChild(this.formView);
            this.formView = null;
            this.currentPageTemplate = null;

            if (selectedPageTemplate) {

                var getPageTemplatePromise: Q.Promise<PageTemplate> = new GetPageTemplateByKeyRequest(selectedPageTemplate.getKey()).
                    setSiteTemplateKey(this.siteTemplateKey).sendAndParse();
                getPageTemplatePromise.done((pageTemplate: PageTemplate) => {

                    new GetPageDescriptorByKeyRequest(pageTemplate.getDescriptorKey()).sendAndParse().
                        done((pageDescriptor: PageDescriptor) => {

                            this.pageTemplateSelector.setPageTemplate(selectedPageTemplate.getKey());
                            this.currentPageTemplate = pageTemplate.getKey();

                            this.refreshConfigForm(pageTemplate, pageDescriptor);
                        });
                });
            }
        }

        setPage(content: Content, pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {
            this.content = content;

            var pageTemplateKey = pageTemplate ? pageTemplate.getKey() : null;

            this.pageTemplateSelector.setPageTemplate(pageTemplateKey);
            this.currentPageTemplate = pageTemplateKey;

            this.refreshConfigForm(pageTemplate, pageDescriptor);
        }

        getPageTemplate(): PageTemplateKey {
            return this.currentPageTemplate ? this.currentPageTemplate : null;
        }

        getPageConfig(): RootDataSet {
            return this.currentPageTemplate ? this.formView.getData() : null;
        }

        private refreshConfigForm(pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {

            if (this.formView) {
                this.removeChild(this.formView);
            }

            if (!pageTemplate) {
                return;
            }

            var config: RootDataSet;
            if (this.content.isPage() && this.content.getPage().hasConfig()) {
                config = this.content.getPage().getConfig();
            } else {
                config = pageTemplate.getConfig();
            }

            this.formView = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }
    }
}