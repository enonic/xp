module app.wizard.page.contextwindow.inspect {

    import PropertyChangedEvent = api.PropertyChangedEvent;
    import ContentId = api.content.ContentId;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import PageModel = api.content.page.PageModel;

    export interface PageTemplateSelectorConfig {

        form: api.ui.form.Form;

        contentType:ContentTypeName;

        siteId:ContentId;
    }

    export class PageTemplateSelector extends api.dom.DivEl {

        private contentType: ContentTypeName;

        private siteId: ContentId;

        private pageTemplateDropdown: Dropdown<PageTemplateOption>;

        constructor(config: PageTemplateSelectorConfig) {
            super("page-template-selector-form");
            this.contentType = config.contentType;
            this.siteId = config.siteId;

            this.pageTemplateDropdown = new Dropdown<PageTemplateOption>("pageTemplate", <DropdownConfig<PageTemplateOption>>{
                optionDisplayValueViewer: new PageTemplateOptionViewer()
            });

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(this.pageTemplateDropdown).setLabel("Page Template").build());
            config.form.add(fieldSet);
        }

        setModel(pageModel: PageModel) {

            var pageTemplateOptions = new PageTemplateOptions(this.siteId, this.contentType, pageModel);
            pageTemplateOptions.getOptions().
                then((options: Option<PageTemplateOption>[]) => {

                    options.forEach((option: Option<PageTemplateOption>) => {
                        this.pageTemplateDropdown.addOption(option);
                    });

                    if (pageModel.hasTemplate()) {
                        this.selectTemplate(pageModel.getTemplateKey());
                    }
                    else {
                        this.pageTemplateDropdown.selectOption(pageTemplateOptions.getDefault(), true);
                    }


                    this.pageTemplateDropdown.onOptionSelected((event: OptionSelectedEvent<PageTemplateOption>) => {
                        var pageTemplate = event.getOption().displayValue.getPageTemplate();
                        if (pageTemplate) {
                            pageModel.setTemplate(pageTemplate, null, this);
                        }
                        else {
                            pageModel.setDefaultTemplate(this);
                        }
                    });

                    pageModel.onPropertyChanged((event: PropertyChangedEvent) => {
                        if (event.getPropertyName() == "template" && this !== event.getSource()) {
                            var pageTemplateKey = <PageTemplateKey>event.getNewValue();
                            if (pageTemplateKey) {
                                this.selectTemplate(pageTemplateKey);
                            } else {
                                this.pageTemplateDropdown.selectOption(pageTemplateOptions.getDefault());
                            }
                        }
                    });

                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }

        private selectTemplate(template: PageTemplateKey) {
            var optionToSelect = this.pageTemplateDropdown.getOptionByValue(template.toString());
            if (optionToSelect) {
                this.pageTemplateDropdown.selectOption(optionToSelect, true);
            }
        }
    }
}
