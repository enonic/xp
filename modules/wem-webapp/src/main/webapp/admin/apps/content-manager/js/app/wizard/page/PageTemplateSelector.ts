module app.wizard.page {

    import SiteTemplateKey = api.content.site.template.SiteTemplateKey;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplateSummary = api.content.page.PageTemplateSummary;
    import PageTemplateSummaryLoader = api.content.page.PageTemplateSummaryLoader;
    import GetPageTemplatesByCanRenderRequest = api.content.page.GetPageTemplatesByCanRenderRequest;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class PageTemplateSelector extends api.dom.DivEl {

        private pageTemplateDropdown: Dropdown<PageTemplateOption>;

        private pageTemplateChangedListeners: {(changedTo: PageTemplateSummary): void;}[] = [];

        private pageTemplateToSelect: PageTemplateKey;

        constructor(form: api.ui.form.Form) {
            super("page-template-selector-form");

            this.pageTemplateDropdown = new Dropdown<PageTemplateOption>("pageTemplate", <DropdownConfig<PageTemplateOption>>{
                optionDisplayValueViewer: new PageTemplateOptionViewer(),
                rowHeight: 50
            });

            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItemBuilder(this.pageTemplateDropdown).setLabel("Page Template").build());
            form.add(fieldSet);

            this.pageTemplateDropdown.onOptionSelected((event: OptionSelectedEvent<PageTemplateOption>) => {
                var pageTemplate = event.getItem().displayValue.getPageTemplate();
                this.pageTemplateToSelect = pageTemplate ? pageTemplate.getKey() : null;
                this.notifyPageTemplateChanged(pageTemplate);
            });
        }

        // TODO: PageTemplateSelector needs only ContenTypeName and SiteTemplateKey, which can be given in constructor instead
        // TODO: since they do not change during the life of a ContentWizard
        layoutExisting(content: api.content.Content, siteTemplateKey: SiteTemplateKey,
                       selectedPageTemplate: PageTemplateKey): Q.Promise<void> {

            var deferred = Q.defer<void>();

            this.pageTemplateToSelect = selectedPageTemplate;
            this.pageTemplateDropdown.removeAllOptions();

            var pageTemplateOptions = new PageTemplateOptions(siteTemplateKey, content.getType());
            pageTemplateOptions.getOptions().done((options: Option<PageTemplateOption>[]) => {
                options.forEach((option: Option<PageTemplateOption>) => {
                    this.pageTemplateDropdown.addOption(option);
                });

                if (this.pageTemplateToSelect) {
                    var optionToSelect = this.pageTemplateDropdown.getOptionByValue(this.pageTemplateToSelect.toString());
                    this.pageTemplateDropdown.selectOption(optionToSelect);
                }
                else {
                    this.pageTemplateDropdown.selectOption(PageTemplateOptions.getDefault());
                }
            });

            // Safe to resolve promise immediately?
            deferred.resolve(null);

            return deferred.promise;
        }

        setPageTemplateToSelect(value: PageTemplateKey) {
            this.pageTemplateToSelect = value;
        }

        private notifyPageTemplateChanged(changedTo: PageTemplateSummary) {
            this.pageTemplateChangedListeners.forEach((listener) => {
                listener(changedTo);
            });
        }

        addPageTemplateChangedListener(listener: {(changedTo: PageTemplateSummary): void;}) {
            this.pageTemplateChangedListeners.push(listener);
        }

        removePageTemplateChangedListener(listener: {(changedTo: PageTemplateSummary): void;}) {
            this.pageTemplateChangedListeners = this.pageTemplateChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}
