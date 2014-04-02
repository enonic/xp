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
            super();

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
        }

        setPage(content: Content, pageTemplate: PageTemplate, pageDescriptor: PageDescriptor, config: api.data.RootDataSet) {

            var pageTemplateKey = pageTemplate ? pageTemplate.getKey() : null;

            this.content = content;



            this.pageTemplateSelector.setPageTemplate(pageTemplateKey);
            this.currentPageTemplate = pageTemplateKey;

            this.refreshConfigForm(pageDescriptor, config);
        }

        onPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageTemplateSelector.onPageTemplateChanged(listener);
        }

        unPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageTemplateSelector.unPageTemplateChanged(listener);
        }

        private refreshConfigForm(pageDescriptor: PageDescriptor, config: api.data.RootDataSet) {

            if (this.formView) {
                this.removeChild(this.formView);
            }

            if (!pageDescriptor) {
                return;
            }

            this.formView = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config);
            this.formView.setDoOffset(false);
            this.appendChild(this.formView);
        }
    }
}