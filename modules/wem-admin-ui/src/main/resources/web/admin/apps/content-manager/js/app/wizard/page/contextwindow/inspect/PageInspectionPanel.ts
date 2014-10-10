module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
    import FormContextBuilder = api.form.FormContextBuilder;
    import FormView = api.form.FormView;
    import Content = api.content.Content;
    import Site = api.content.site.Site;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplate = api.content.page.PageTemplate;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PageController = api.content.page.inputtype.pagecontroller.PageController;
    import PageDescriptorDropdown = api.content.page.PageDescriptorDropdown;
    import GetPageDescriptorsByModulesRequest = api.content.page.GetPageDescriptorsByModulesRequest;
    import PageDescriptorsJson = api.content.page.PageDescriptorsJson;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export interface PageInspectionPanelConfig {

        site: Site;

        contentType:ContentTypeName;
    }

    export class PageInspectionPanel extends BaseInspectionPanel {

        private site: Site;

        private content: Content;

        private pageTemplateSelector: PageTemplateSelector;

        private pageTemplateSelectorForm: api.ui.form.Form;

        private pageTemplateConfigForm: FormView;

        private pageControllerSelectorForm: api.ui.form.Form;

        private pageControllerDropdown: PageDescriptorDropdown;

        private pageControllerConfigForm: FormView;

        private pageControllerChangedListeners: {(event: PageControllerChangedEvent): void;}[] = [];

        constructor(config: PageInspectionPanelConfig) {
            super();
            this.site = config.site;
            this.pageTemplateConfigForm = null;
            this.pageTemplateSelectorForm = this.buildPageTemplateForm(config);
            this.pageTemplateSelectorForm.hide();
            this.pageControllerSelectorForm = this.buildPageControllerForm();
            this.pageControllerSelectorForm.hide();
            this.appendChild(this.pageControllerSelectorForm);
            this.appendChild(this.pageTemplateSelectorForm);
        }

        private buildPageTemplateForm(config: PageInspectionPanelConfig): api.ui.form.Form {
            var form = new api.ui.form.Form('form-view');

            this.pageTemplateSelector = new PageTemplateSelector({
                form: form,
                contentType: config.contentType,
                siteId: config.site.getContentId()});

            return form;
        }

        setPage(content: Content, pageTemplate: PageTemplate, pageDescriptor: PageDescriptor, config: RootDataSet) {

            this.content = content;

            if (api.ObjectHelper.iFrameSafeInstanceOf(content, PageTemplate)) {
                this.pageTemplateSelectorForm.hide();
                this.pageControllerSelectorForm.show();
                var contentAsPageTemplate = <PageTemplate>content;
                var optionToSelect: api.ui.selector.Option<PageDescriptor> = null;
                if (contentAsPageTemplate.getController()) {
                    optionToSelect = this.pageControllerDropdown.getOptionByValue(contentAsPageTemplate.getController().toString());
                    if (optionToSelect) {
                        this.pageControllerDropdown.selectOption(optionToSelect, true);
                        this.refreshPageTemplateConfigForm(pageDescriptor, config);
                    }
                }
            }
            else {
                this.pageControllerSelectorForm.hide();

                var pageTemplateKey = pageTemplate ? pageTemplate.getKey() : null;
                this.pageTemplateSelector.setPageTemplate(pageTemplateKey);
                this.pageTemplateSelectorForm.show();
                this.refreshPageTemplateConfigForm(pageDescriptor, config);
            }
        }

        private buildPageControllerForm(): api.ui.form.Form {

            var moduleKeys = this.site.getModuleKeys(),
                request = new GetPageDescriptorsByModulesRequest(moduleKeys),
                loader = new api.util.loader.BaseLoader<PageDescriptorsJson, PageDescriptor>(request);

            this.pageControllerDropdown = new PageDescriptorDropdown('page-controller', {
                loader: loader
            });
            this.pageControllerDropdown.onOptionSelected((event: OptionSelectedEvent<PageDescriptor>) => {
                this.notifyPageControllerChanged(new PageControllerChangedEvent(event.getOption().displayValue));
            });

            var form = new api.ui.form.Form('form-view');
            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(this.pageControllerDropdown).setLabel("Page Controller").build());
            form.add(fieldSet);
            return form;
        }

        private refreshPageControllerConfigForm(pageDescriptor: PageDescriptor, config: RootDataSet) {

            if (this.pageTemplateConfigForm) {
                this.removeChild(this.pageTemplateConfigForm);
            }

            if (!pageDescriptor) {
                return;
            }

            this.pageTemplateConfigForm = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config);
            this.appendChild(this.pageTemplateConfigForm);
        }

        private refreshPageTemplateConfigForm(pageDescriptor: PageDescriptor, config: RootDataSet) {

            if (this.pageTemplateConfigForm) {
                this.removeChild(this.pageTemplateConfigForm);
            }

            if (!pageDescriptor) {
                return;
            }

            this.pageTemplateConfigForm = new FormView(new FormContextBuilder().build(), pageDescriptor.getConfig(), config);
            this.appendChild(this.pageTemplateConfigForm);
        }

        onPageControllerChanged(listener: {(event: PageControllerChangedEvent): void;}) {
            this.pageControllerChangedListeners.push(listener);
        }

        unPageControllerChanged(listener: {(event: PageControllerChangedEvent): void;}) {
            this.pageControllerChangedListeners = this.pageControllerChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageControllerChanged(event: PageControllerChangedEvent) {
            this.pageControllerChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

        onPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageTemplateSelector.onPageTemplateChanged(listener);
        }

        unPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageTemplateSelector.unPageTemplateChanged(listener);
        }
    }
}