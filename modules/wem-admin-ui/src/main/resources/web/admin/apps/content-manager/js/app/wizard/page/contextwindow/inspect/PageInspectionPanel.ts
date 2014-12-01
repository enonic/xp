module app.wizard.page.contextwindow.inspect {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import PropertyTree = api.data2.PropertyTree;
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

    export class PageInspectionPanel extends BaseInspectionPanel {

        private siteModel: SiteModel;

        private pageModel: PageModel;

        private contentType: ContentTypeName;

        private pageTemplateSelector: PageTemplateSelector;

        private pageTemplateSelectorForm: api.ui.form.Form;

        private configForm: FormView;

        private pageControllerSelectorForm: api.ui.form.Form;

        private pageControllerDropdown: PageDescriptorDropdown;

        private getPageDescriptorsByModulesRequest: GetPageDescriptorsByModulesRequest;

        constructor() {
            super();
        }

        setModel(liveEditModel: LiveEditModel) {

            this.pageModel = liveEditModel.getPageModel();
            this.siteModel = liveEditModel.getSiteModel();
            this.contentType = liveEditModel.getContent().getType();

            this.refreshPageTemplateForm();
            this.refreshPageControllerForm();

            this.pageTemplateSelector.setModel(this.pageModel);

            if (this.pageModel.isPageTemplate()) {
                if (this.pageModel.hasController()) {

                    this.pageControllerDropdown.onLoadedData(() => {
                        this.selectController(this.pageModel.getController());
                        this.pageControllerSelectorForm.show();
                    });
                }
                else if (this.pageModel.hasTemplate()) {
                    this.pageTemplateSelectorForm.show();
                }
                else {
                    this.pageControllerSelectorForm.show();
                }
            }
            else {
                if (this.pageModel.hasController()) {

                    this.pageControllerDropdown.onLoadedData(() => {
                        this.selectController(this.pageModel.getController());
                        this.refreshConfigForm(this.pageModel.getController(), this.pageModel.getConfig());
                        this.pageControllerSelectorForm.show();
                    });
                }
                else if (this.pageModel.hasTemplate() || this.pageModel.isUsingDefaultTemplate()) {
                    this.pageTemplateSelectorForm.show();
                }
                else {
                    this.pageControllerSelectorForm.show();
                }
            }

            this.pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                if (event.getPropertyName() == "controller" && this !== event.getSource()) {

                    this.pageControllerSelectorForm.show();

                    var controller = this.pageModel.getController();
                    this.selectController(controller);
                }
                else if (event.getPropertyName() == "template" && this !== event.getSource()) {

                    this.pageTemplateSelectorForm.show();

                    if (this.pageModel.hasTemplate()) {
                        var controllerKey = this.pageModel.getTemplate().getController();
                        new GetPageDescriptorByKeyRequest(controllerKey).sendAndParse().
                            then((pageDescriptor: PageDescriptor) => {
                                this.refreshConfigForm(pageDescriptor, this.pageModel.getConfig());
                            }).catch((reason: any) => api.DefaultErrorHandler.handle(reason)).done();
                    }
                }
            });
        }

        private selectController(controller: PageDescriptor) {

            var controllerKey = controller ? controller.getKey() : null;
            if (!controllerKey) {
                // TODO: ??
            }
            else {
                var optionToSelect = this.pageControllerDropdown.getOptionByValue(controllerKey.toString());
                if (optionToSelect) {
                    this.pageControllerDropdown.selectOption(optionToSelect, true);
                }

                this.refreshConfigForm(controller, this.pageModel.getConfig());
            }
        }

        private refreshPageTemplateForm() {

            if (this.pageTemplateSelectorForm) {
                this.pageControllerSelectorForm.remove();
            }

            this.pageTemplateSelectorForm = this.buildPageTemplateForm();
            this.pageTemplateSelectorForm.hide();
            this.appendChild(this.pageTemplateSelectorForm);
        }

        private buildPageTemplateForm(): api.ui.form.Form {

            var form = new api.ui.form.Form('form-view');
            this.pageTemplateSelector = new PageTemplateSelector({
                form: form,
                contentType: this.contentType,
                siteId: this.siteModel.getSiteId()
            });

            return form;
        }

        private refreshPageControllerForm() {

            if (this.pageControllerSelectorForm) {
                this.pageControllerSelectorForm.remove();
            }

            this.pageControllerSelectorForm = this.buildPageControllerForm();
            this.pageControllerSelectorForm.hide();
            this.appendChild(this.pageControllerSelectorForm);
        }

        private buildPageControllerForm(): api.ui.form.Form {

            var moduleKeys = this.siteModel.getModuleKeys();
            this.getPageDescriptorsByModulesRequest = new GetPageDescriptorsByModulesRequest(moduleKeys);
            var loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(this.getPageDescriptorsByModulesRequest);

            this.pageControllerDropdown = new PageDescriptorDropdown('page-controller', {
                loader: loader
            });
            this.pageControllerDropdown.onOptionSelected((event: OptionSelectedEvent<PageDescriptor>) => {
                var pageDescriptor = event.getOption().displayValue;
                this.pageModel.setController(pageDescriptor, this);

                this.refreshConfigForm(pageDescriptor, this.pageModel.getConfig());
            });

            this.siteModel.onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() == SiteModel.PROPERTY_NAME_MODULE_CONFIGS) {
                    this.getPageDescriptorsByModulesRequest.setModuleKeys(this.siteModel.getModuleKeys());
                    loader.load();
                }
            });

            var form = new api.ui.form.Form('form-view');
            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(this.pageControllerDropdown).setLabel("Page Controller").build());
            form.add(fieldSet);
            return form;
        }

        private refreshConfigForm(pageDescriptor: PageDescriptor, config: PropertyTree) {

            if (this.configForm) {
                this.removeChild(this.configForm);
            }

            if (!pageDescriptor) {
                return;
            }

            this.configForm = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config.getRoot());
            this.appendChild(this.configForm);
        }
    }
}