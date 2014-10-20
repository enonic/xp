module app.wizard.page.contextwindow.inspect {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import RootDataSet = api.data.RootDataSet;
    import FormContextBuilder = api.form.FormContextBuilder;
    import FormView = api.form.FormView;
    import Content = api.content.Content;
    import Page = api.content.page.Page;
    import PageModel = api.content.page.PageModel;
    import Site = api.content.site.Site;
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

    export interface PageInspectionPanelConfig {

        site: Site;

        contentType:ContentTypeName;

        pageModel:PageModel;
    }

    export class PageInspectionPanel extends BaseInspectionPanel {

        private site: Site;

        private pageModel: PageModel;

        private pageTemplateSelector: PageTemplateSelector;

        private pageTemplateSelectorForm: api.ui.form.Form;

        private configForm: FormView;

        private pageControllerSelectorForm: api.ui.form.Form;

        private pageControllerDropdown: PageDescriptorDropdown;

        constructor(config: PageInspectionPanelConfig) {
            super();
            this.site = config.site;

            this.pageControllerSelectorForm = this.buildPageControllerForm();
            this.pageControllerSelectorForm.hide();

            this.pageTemplateSelectorForm = this.buildPageTemplateForm(config);
            this.pageTemplateSelectorForm.hide();

            this.appendChild(this.pageControllerSelectorForm);
            this.appendChild(this.pageTemplateSelectorForm);
        }

        setModel(pageModel: PageModel) {

            this.pageModel = pageModel;
            this.pageTemplateSelector.setModel(this.pageModel);

            if (this.pageModel.isPageTemplate()) {
                if (this.pageModel.hasController()) {
                    this.selectController(this.pageModel.getController());
                    this.pageControllerSelectorForm.show();
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
                    this.selectController(this.pageModel.getController());
                    this.refreshConfigForm(this.pageModel.getController(), this.pageModel.getConfig());
                    this.pageControllerSelectorForm.show();
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

        private buildPageTemplateForm(config: PageInspectionPanelConfig): api.ui.form.Form {

            var form = new api.ui.form.Form('form-view');
            this.pageTemplateSelector = new PageTemplateSelector({
                form: form,
                contentType: config.contentType,
                siteId: config.site.getContentId()
            });

            return form;
        }

        private buildPageControllerForm(): api.ui.form.Form {

            var moduleKeys = this.site.getModuleKeys(),
                request = new GetPageDescriptorsByModulesRequest(moduleKeys),
                loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request);

            this.pageControllerDropdown = new PageDescriptorDropdown('page-controller', {
                loader: loader
            });
            this.pageControllerDropdown.onOptionSelected((event: OptionSelectedEvent<PageDescriptor>) => {
                var pageDescriptor = event.getOption().displayValue;
                this.pageModel.setController(pageDescriptor, this);

                this.refreshConfigForm(pageDescriptor, this.pageModel.getConfig());
            });

            var form = new api.ui.form.Form('form-view');
            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(this.pageControllerDropdown).setLabel("Page Controller").build());
            form.add(fieldSet);
            return form;
        }

        private refreshConfigForm(pageDescriptor: PageDescriptor, config: RootDataSet) {

            if (this.configForm) {
                this.removeChild(this.configForm);
            }

            if (!pageDescriptor) {
                return;
            }

            this.configForm = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config);
            this.appendChild(this.configForm);
        }
    }
}