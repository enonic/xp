module app.wizard.page.contextwindow.inspect.page {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import PropertyTree = api.data.PropertyTree;
    import FormContextBuilder = api.form.FormContextBuilder;
    import FormView = api.form.FormView;
    import Content = api.content.Content;
    import Page = api.content.page.Page;
    import PageModel = api.content.page.PageModel;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplate = api.content.page.PageTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageDescriptor = api.content.page.PageDescriptor;
    import DescriptorKey = api.content.page.DescriptorKey;
    import PageController = api.content.page.inputtype.pagecontroller.PageController;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import GetPageDescriptorsByModulesRequest = api.content.page.GetPageDescriptorsByModulesRequest;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;
    import GetPageTemplateByKeyRequest = api.content.page.GetPageTemplateByKeyRequest;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class PageInspectionPanel extends app.wizard.page.contextwindow.inspect.BaseInspectionPanel {

        private liveEditModel: LiveEditModel;

        private siteModel: SiteModel;

        private pageModel: PageModel;

        private contentType: ContentTypeName;

        private pageTemplateSelector: PageTemplateSelector;

        private pageTemplateForm: PageTemplateForm;

        private pageControllerForm: PageControllerForm;

        private pageControllerSelector: PageControllerSelector;

        constructor() {
            super();
        }

        setModel(liveEditModel: LiveEditModel) {

            this.liveEditModel = liveEditModel;
            this.pageModel = liveEditModel.getPageModel();
            this.siteModel = liveEditModel.getSiteModel();
            this.contentType = liveEditModel.getContent().getType();

            this.pageTemplateSelector = new PageTemplateSelector();
            this.pageTemplateForm = new PageTemplateForm(this.pageTemplateSelector);
            this.pageTemplateForm.hide();
            this.appendChild(this.pageTemplateForm);

            this.pageControllerSelector = new PageControllerSelector();
            this.pageControllerForm = new PageControllerForm(this.pageControllerSelector);
            this.pageControllerForm.hide();
            this.appendChild(this.pageControllerForm);

            if (this.pageModel.isPageTemplate()) {

                new PageTemplateInspectionHandler().
                    setPageInspectionPanel(this).
                    setPageControllerForm(this.pageControllerForm).
                    setPageTemplateForm(this.pageTemplateForm).
                    setModel(liveEditModel);
            }
            else {
                new ContentInspectionHandler().
                    setPageInspectionPanel(this).
                    setPageControllerForm(this.pageControllerForm).
                    setPageTemplateForm(this.pageTemplateForm).
                    setModel(liveEditModel);
            }
        }
    }

    class BaseInspectionHandler {

        pageInspectionPanel: PageInspectionPanel;

        configForm: FormView;

        pageControllerForm: PageControllerForm;

        pageTemplateForm: PageTemplateForm;

        setPageInspectionPanel(value: PageInspectionPanel): BaseInspectionHandler {
            this.pageInspectionPanel = value;
            return this;
        }

        setPageControllerForm(value: PageControllerForm): BaseInspectionHandler {
            this.pageControllerForm = value;
            return this;
        }

        setPageTemplateForm(value: PageTemplateForm): BaseInspectionHandler {
            this.pageTemplateForm = value;
            return this;
        }

        setModel(liveEditModel: LiveEditModel) {
            throw new Error("Must be implemented by inheritors");
        }

        refreshConfigForm(pageDescriptor: PageDescriptor, config: PropertyTree) {

            if (this.configForm) {
                this.configForm.remove();
                this.configForm = null;
            }

            if (!pageDescriptor) {
                return;
            }

            this.configForm = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config.getRoot());
            this.pageInspectionPanel.appendChild(this.configForm);
            this.configForm.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();
        }
    }

    class PageTemplateInspectionHandler extends BaseInspectionHandler {

        setModel(liveEditModel: LiveEditModel) {

            var pageModel = liveEditModel.getPageModel();

            this.pageControllerForm.getSelector().setModel(liveEditModel);

            if (pageModel.hasTemplate()) {
                this.pageTemplateForm.show();
            }
            else {
                this.pageControllerForm.show();
            }

            this.refreshConfigForm(pageModel.getController(), pageModel.getConfig());


            pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                if (event.getPropertyName() == "controller" && this !== event.getSource()) {

                    this.pageControllerForm.show();

                    var controller = pageModel.getController();
                    if (controller) {
                        this.refreshConfigForm(controller, pageModel.getConfig());
                    }
                }
            });
        }
    }

    class ContentInspectionHandler extends BaseInspectionHandler {

        setModel(liveEditModel: LiveEditModel) {

            var pageModel = liveEditModel.getPageModel();

            if (pageModel.hasTemplate()) {

                this.pageTemplateForm.getSelector().setModel(liveEditModel);
                this.pageTemplateForm.show();

                this.showPageConfig(pageModel);
            }
            else if (pageModel.isUsingDefaultTemplate()) {

                this.pageTemplateForm.getSelector().setModel(liveEditModel);
                this.pageTemplateForm.show();

                this.showDefaultPageTemplateConfig(pageModel);
            }
            else {

                this.pageControllerForm.getSelector().setModel(liveEditModel);
                this.pageControllerForm.show();

                this.refreshConfigForm(pageModel.getController(), pageModel.getConfig());
            }

            pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                if (event.getPropertyName() == "controller" && this !== event.getSource()) {

                    this.pageControllerForm.show();

                    var controller = pageModel.getController();
                    if (controller) {
                        this.refreshConfigForm(controller, pageModel.getConfig());
                    }
                }
                else if (event.getPropertyName() == "template" && this !== event.getSource()) {

                    if (pageModel.hasTemplate()) {
                        this.pageTemplateForm.show();
                        this.showPageConfig(pageModel);
                    }
                    else {
                        this.showDefaultPageTemplateConfig(pageModel);
                    }
                }
            });
        }

        private showPageConfig(pageModel: PageModel) {

            var controllerKey = pageModel.getTemplate().getController();
            new GetPageDescriptorByKeyRequest(controllerKey).sendAndParse().
                then((pageDescriptor: PageDescriptor) => {

                    this.refreshConfigForm(pageDescriptor, pageModel.getConfig());
                }).catch((reason: any) => api.DefaultErrorHandler.handle(reason)).done();
        }

        private showDefaultPageTemplateConfig(pageModel: PageModel) {

            var controller = pageModel.getDefaultPageTemplateController();
            var config = pageModel.getDefaultPageTemplate().getConfig().copy();
            this.refreshConfigForm(controller, config);
        }
    }
}