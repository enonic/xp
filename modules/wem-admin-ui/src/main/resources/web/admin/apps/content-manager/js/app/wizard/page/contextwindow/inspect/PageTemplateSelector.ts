module app.wizard.page.contextwindow.inspect {

    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplateSummary = api.content.page.PageTemplateSummary;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplateSummaryLoader = api.content.page.PageTemplateSummaryLoader;
    import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export interface PageTemplateSelectorConfig {

        form: api.ui.form.Form;

        contentType:ContentTypeName;

        siteTemplateKey:SiteTemplateKey;
    }

    export class PageTemplateSelector extends api.dom.DivEl {

        private contentType: ContentTypeName;

        private siteTemplateKey: SiteTemplateKey;

        private pageTemplateDropdown: Dropdown<PageTemplateOption>;

        private pageTemplateChangedListeners: {(event: PageTemplateChangedEvent): void;}[] = [];

        private pageTemplateToSelect: PageTemplateKey;

        constructor(config: PageTemplateSelectorConfig) {
            super("page-template-selector-form");
            this.contentType = config.contentType;
            this.siteTemplateKey = config.siteTemplateKey;

            this.pageTemplateDropdown = new Dropdown<PageTemplateOption>("pageTemplate", <DropdownConfig<PageTemplateOption>>{
                optionDisplayValueViewer: new PageTemplateOptionViewer()
            });

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(this.pageTemplateDropdown).setLabel("Page Template").build());
            config.form.add(fieldSet);

            var pageTemplateOptions = new PageTemplateOptions(this.siteTemplateKey, this.contentType);
            pageTemplateOptions.getOptions().
                done((options: Option<PageTemplateOption>[]) => {

                    options.forEach((option: Option<PageTemplateOption>) => {
                        this.pageTemplateDropdown.addOption(option);
                    });
                });


            this.pageTemplateDropdown.onOptionSelected((event: OptionSelectedEvent<PageTemplateOption>) => {

                var pageTemplate = event.getOption().displayValue.getPageTemplate();
                var pageTemplateKey = pageTemplate ? pageTemplate.getKey() : null;
                var selectedValueChanged: boolean = !((this.pageTemplateToSelect === null && pageTemplateKey === null) ||
                                                      (this.pageTemplateToSelect && this.pageTemplateToSelect.equals(pageTemplateKey)));
                if (selectedValueChanged) {
                    this.pageTemplateToSelect = pageTemplateKey;
                    this.notifyPageTemplateChanged(new PageTemplateChangedEvent(pageTemplate));
                }
            });
        }

        setPageTemplate(selectedPageTemplate: PageTemplateKey): void {
            this.pageTemplateToSelect = selectedPageTemplate;

            if (this.pageTemplateToSelect) {
                var optionToSelect = this.pageTemplateDropdown.getOptionByValue(this.pageTemplateToSelect.toString());
                this.pageTemplateDropdown.selectOption(optionToSelect);
            } else {
                this.pageTemplateDropdown.selectOption(PageTemplateOptions.getDefault());
            }
        }

        onPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageTemplateChangedListeners.push(listener);
        }

        unPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageTemplateChangedListeners = this.pageTemplateChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyPageTemplateChanged(event: PageTemplateChangedEvent) {
            this.pageTemplateChangedListeners.forEach((listener) => {
                listener(event);
            });
        }
    }
}
