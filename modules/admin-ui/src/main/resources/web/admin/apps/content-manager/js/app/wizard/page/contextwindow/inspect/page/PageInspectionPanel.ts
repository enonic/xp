module app.wizard.page.contextwindow.inspect.page {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import PropertyTree = api.data.PropertyTree;
    import FormContextBuilder = api.form.FormContextBuilder;
    import FormView = api.form.FormView;
    import Content = api.content.Content;
    import Page = api.content.page.Page;
    import SetTemplate = api.content.page.SetTemplate;
    import PageModel = api.content.page.PageModel;
    import PageMode = api.content.page.PageMode;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageController = api.content.page.inputtype.pagecontroller.PageController;
    import GetPageDescriptorByKeyRequest = api.content.page.GetPageDescriptorByKeyRequest;

    export class PageInspectionPanel extends app.wizard.page.contextwindow.inspect.BaseInspectionPanel {

        private liveEditModel: LiveEditModel;

        private siteModel: SiteModel;

        private pageModel: PageModel;

        private pageTemplateSelector: PageTemplateSelector;

        private pageTemplateForm: PageTemplateForm;

        private pageControllerForm: PageControllerForm;

        private pageControllerSelector: PageControllerSelector;

        private inspectionHandler: BaseInspectionHandler;

        constructor() {
            super();
        }

        setModel(liveEditModel: LiveEditModel) {

            this.liveEditModel = liveEditModel;
            this.pageModel = liveEditModel.getPageModel();
            this.siteModel = liveEditModel.getSiteModel();

            this.pageTemplateSelector = new PageTemplateSelector();
            this.pageTemplateForm = new PageTemplateForm(this.pageTemplateSelector);
            this.pageTemplateForm.hide();
            this.appendChild(this.pageTemplateForm);

            this.pageControllerSelector = new PageControllerSelector();
            this.pageControllerForm = new PageControllerForm(this.pageControllerSelector);
            this.pageControllerForm.hide();
            this.appendChild(this.pageControllerForm);

            if (this.pageModel.isPageTemplate()) {

                this.inspectionHandler = new PageTemplateInspectionHandler();
                this.inspectionHandler.
                    setPageModel(this.pageModel).
                    setPageInspectionPanel(this).
                    setPageControllerForm(this.pageControllerForm).
                    setPageTemplateForm(this.pageTemplateForm).
                    setModel(liveEditModel);
            }
            else {
                this.inspectionHandler = new ContentInspectionHandler();
                this.inspectionHandler.
                    setPageModel(this.pageModel).
                    setPageInspectionPanel(this).
                    setPageControllerForm(this.pageControllerForm).
                    setPageTemplateForm(this.pageTemplateForm).
                    setModel(liveEditModel);
            }

            this.pageTemplateForm.getSelector().onSelection((pageTemplate: PageTemplate) => {
                    if (pageTemplate) {
                        new GetPageDescriptorByKeyRequest(pageTemplate.getController()).sendAndParse().
                            then((pageDescriptor: PageDescriptor) => {
                                var setTemplate = new SetTemplate(this).
                                    setTemplate(pageTemplate, pageDescriptor);
                                this.pageModel.setTemplate(setTemplate, true);
                            }).catch((reason: any) => {
                                api.DefaultErrorHandler.handle(reason);
                            }).done();
                    }
                    else {
                        this.pageModel.setAutomaticTemplate(this, true);
                    }
                }
            );
        }

        refreshInspectionHandler(liveEditModel: LiveEditModel) {
            this.inspectionHandler.refreshConfigView(liveEditModel);
        }
    }

    class BaseInspectionHandler {

        pageModel: PageModel;

        pageInspectionPanel: PageInspectionPanel;

        configForm: FormView;

        pageControllerForm: PageControllerForm;

        pageTemplateForm: PageTemplateForm;

        setPageModel(value: PageModel): BaseInspectionHandler {
            this.pageModel = value;
            return this;
        }

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

        refreshConfigForm(pageDescriptor: PageDescriptor, config: PropertyTree, context: FormContext) {
            if (this.configForm) {
                this.configForm.remove();
                this.configForm = null;
            }

            if (!pageDescriptor) {
                return;
            }

            this.configForm =
            new FormView(context ? context : new FormContextBuilder().build(), pageDescriptor.getConfig(), config.getRoot());
            this.pageInspectionPanel.appendChild(this.configForm);
            this.pageModel.setIgnorePropertyChanges(true);
            this.configForm.layout().catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).finally(() => {
                this.pageModel.setIgnorePropertyChanges(false);
            }).
                done();
        }

        refreshConfigView(liveEditModel: LiveEditModel) {
        }
    }

    class PageTemplateInspectionHandler extends BaseInspectionHandler {

        setModel(liveEditModel: LiveEditModel) {

            var pageModel = liveEditModel.getPageModel();

            this.pageControllerForm.getSelector().setModel(liveEditModel);
            this.pageControllerForm.show();

            this.refreshConfigForm(pageModel.getController(), pageModel.getConfig(), liveEditModel.getFormContext());


            pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                if (event.getPropertyName() == PageModel.PROPERTY_CONTROLLER && this !== event.getSource()) {

                    this.pageControllerForm.show();

                    this.refreshConfigForm(pageModel.getController(), pageModel.getConfig(), liveEditModel.getFormContext());
                }
                else if (event.getPropertyName() == PageModel.PROPERTY_CONFIG && this !== event.getSource()) {

                    this.pageControllerForm.show();

                    var controller = pageModel.getController();
                    if (controller) {
                        this.refreshConfigForm(controller, pageModel.getConfig(), liveEditModel.getFormContext());
                    }
                }
            });
        }
    }

    class ContentInspectionHandler extends BaseInspectionHandler {

        private propertyChangedListener: (event: PropertyChangedEvent) => void;

        refreshConfigView(liveEditModel: LiveEditModel) {
            var pageModel = liveEditModel.getPageModel();
            var pageMode = pageModel.getMode();

            if (pageMode == PageMode.FORCED_TEMPLATE) {
                this.showPageConfig(pageModel, liveEditModel.getFormContext());
            }
            else if (pageMode == PageMode.AUTOMATIC) {
                this.showDefaultPageTemplateConfig(pageModel, liveEditModel.getFormContext());
            }
            else {
                throw new Error("Unsupported PageMode: " + pageMode);
            }
        }

        setModel(liveEditModel: LiveEditModel) {

            var pageModel = liveEditModel.getPageModel();

            if (this.propertyChangedListener) {
                pageModel.unPropertyChanged(this.propertyChangedListener);
            }

            this.initListener(pageModel, liveEditModel);

            var pageMode = pageModel.getMode();

            this.pageTemplateForm.getSelector().setModel(liveEditModel);
            this.pageTemplateForm.show();

            if (pageMode == PageMode.FORCED_TEMPLATE) {
                this.showPageConfig(pageModel, liveEditModel.getFormContext());
            }
            else if (pageMode == PageMode.AUTOMATIC) {
                this.showDefaultPageTemplateConfig(pageModel, liveEditModel.getFormContext());
            }
            else {
                throw new Error("Unsupported PageMode: " + pageMode);
            }

            pageModel.onPropertyChanged(this.propertyChangedListener);
        }

        private initListener(pageModel: PageModel, liveEditModel: LiveEditModel) {
            this.propertyChangedListener = (event: PropertyChangedEvent) => {
                if (event.getPropertyName() == "controller" && this !== event.getSource()) {

                    this.pageControllerForm.show();

                    var controller = pageModel.getController();
                    if (controller) {
                        this.refreshConfigForm(controller, pageModel.getConfig(), liveEditModel.getFormContext());
                    }
                }
                else if (event.getPropertyName() == PageModel.PROPERTY_CONFIG && this !== event.getSource()) {

                    this.pageTemplateForm.show();

                    if (pageModel.getMode() == PageMode.AUTOMATIC) {
                        this.showDefaultPageTemplateConfig(pageModel, liveEditModel.getFormContext());
                    }
                    else {
                        this.showPageConfig(pageModel, liveEditModel.getFormContext());
                    }
                }
            };
        }

        private showPageConfig(pageModel: PageModel, formContext: FormContext) {

            this.refreshConfigForm(pageModel.getTemplateDescriptor(), pageModel.getConfig(), formContext);
        }

        private showDefaultPageTemplateConfig(pageModel: PageModel, formContext: FormContext) {

            var controller = pageModel.getDefaultPageTemplateController();
            var config = pageModel.getConfig();
            this.refreshConfigForm(controller, config, formContext);
        }
    }
}