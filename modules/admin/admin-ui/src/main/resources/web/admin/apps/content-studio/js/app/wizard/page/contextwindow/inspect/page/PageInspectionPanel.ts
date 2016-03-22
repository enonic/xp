module app.wizard.page.contextwindow.inspect.page {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import PropertyTree = api.data.PropertyTree;
    import FormContextBuilder = api.form.FormContextBuilder;
    import FormView = api.form.FormView;
    import FormContext = api.form.FormContext;
    import Content = api.content.Content;
    import Page = api.content.page.Page;
    import SetTemplate = api.content.page.SetTemplate;
    import PageModel = api.content.page.PageModel;
    import PageMode = api.content.page.PageMode;
    import SiteModel = api.content.site.SiteModel;
    import LiveEditModel = api.liveedit.LiveEditModel;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
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

            this.pageControllerSelector = new PageControllerSelector(liveEditModel);
            this.pageControllerForm = new PageControllerForm(this.pageControllerSelector);
            this.pageControllerForm.hide();
            this.appendChild(this.pageControllerForm);

            this.inspectionHandler = this.pageModel.isPageTemplate() ? new PageTemplateInspectionHandler() : new ContentInspectionHandler();

            if (!this.pageModel.isPageTemplate()) { //init page controller selector in case of 'customized' template chosen or no template presents
                this.pageControllerSelector.load();

                if (this.pageModel.isCustomized()) {
                    this.addClass("customized");
                }

                if (this.pageModeImpliesPageControllerShown()) {
                    this.pageControllerForm.show();
                }

            }

            this.inspectionHandler.
                setPageModel(this.pageModel).
                setPageInspectionPanel(this).
                setPageControllerForm(this.pageControllerForm).
                setPageTemplateForm(this.pageTemplateForm).
                setModel(liveEditModel);

            this.pageTemplateForm.getSelector().onSelection((pageTemplate: PageTemplate) => {
                    this.removeClass("customized");
                    this.pageModel.setCustomized(false);

                    if (pageTemplate) {
                        this.pageControllerForm.hide();
                        new GetPageDescriptorByKeyRequest(pageTemplate.getController()).sendAndParse().
                            then((pageDescriptor: PageDescriptor) => {
                                var setTemplate = new SetTemplate(this).
                                    setTemplate(pageTemplate, pageDescriptor);
                                this.pageModel.setTemplate(setTemplate, true);
                            }).catch((reason: any) => {
                                api.DefaultErrorHandler.handle(reason);
                            }).done();
                    }
                    else if (this.pageModel.hasDefaultPageTemplate()) {
                        this.pageControllerForm.hide();
                        this.pageModel.setAutomaticTemplate(this, true);
                    }
                    else {
                        this.pageModel.reset(this);
                    }
                }
            );

            this.pageTemplateForm.getSelector().onCustomizedSelected(() => {
                this.addClass("customized");
                this.pageControllerForm.getSelector().reset();
                this.pageControllerForm.show();
                this.pageModel.setCustomized(true);
            });

            this.pageModel.onReset(() => {
                this.pageControllerForm.getSelector().reset();
                if (!this.pageModel.isPageTemplate() && !(this.pageModel.isCustomized() && this.pageModel.hasController())) {
                    this.pageControllerForm.hide()
                }
            });
        }

        refreshInspectionHandler(liveEditModel: LiveEditModel) {
            this.inspectionHandler.refreshConfigView(liveEditModel);
        }

        private pageModeImpliesPageControllerShown(): boolean {
            return (this.pageModel.isCustomized() && this.pageModel.hasController()) ||
                   this.pageModel.getMode() == PageMode.FORCED_CONTROLLER;
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

            this.pageControllerForm.getSelector().load();
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
                throw new Error("Unsupported PageMode: " + PageMode[pageMode]);
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
            if (pageMode !== PageMode.FRAGMENT) {
                this.pageTemplateForm.show();
            }

            if (pageMode == PageMode.AUTOMATIC) {
                this.showDefaultPageTemplateConfig(pageModel, liveEditModel.getFormContext());
            }
            else {
                this.showPageConfig(pageModel, liveEditModel.getFormContext());
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