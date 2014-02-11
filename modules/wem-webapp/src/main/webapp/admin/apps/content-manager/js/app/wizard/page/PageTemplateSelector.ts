module app.wizard.page {

    export class PageTemplateSelector extends api.dom.DivEl {

        private pageTemplateComboBox: api.content.page.PageTemplateComboBox;

        private pageTemplateChangedListeners: {(changedTo: api.content.page.PageTemplateSummary): void;}[] = [];

        private pageTemplateToSelect: api.content.page.PageTemplateKey;

        private form:PageWizardStepForm;

        constructor(form:PageWizardStepForm) {
            super("page-template-selector-form");

            this.pageTemplateComboBox = new api.content.page.PageTemplateComboBox();

            this.form = form;
            var fieldSet = new api.ui.form.Fieldset();
            fieldSet.add(new api.ui.form.FormItem(new api.ui.form.FormItemBuilder(this.pageTemplateComboBox).setLabel("Page Template")));
            form.add(fieldSet);

            this.pageTemplateComboBox.addOptionSelectedListener((option: api.ui.combobox.Option<api.content.page.PageTemplateSummary>) => {
                this.pageTemplateToSelect = option.displayValue.getKey();
                this.notifyPageTemplateChanged(option.displayValue);
            });

            this.pageTemplateComboBox.addSelectedOptionRemovedListener(() => {
                this.pageTemplateToSelect = null;
                this.notifyPageTemplateChanged(null);
            });
            this.pageTemplateComboBox.addLoadedListener((pageTemplates: api.content.page.PageTemplateSummary[]) => {

                    pageTemplates.forEach((template: api.content.page.PageTemplateSummary) => {
                        if (this.pageTemplateToSelect) {
                            if (template.getKey().toString() == this.pageTemplateToSelect.toString()) {
                                this.pageTemplateComboBox.select(template);
                            }
                        }
                    });
                }
            );
        }

        layoutExisting(siteTemplateKey: api.content.site.template.SiteTemplateKey,
                       selectedPageTemplate: api.content.page.PageTemplateKey): Q.Promise<void> {
            var deferred = Q.defer<void>();
            var request:api.content.page.GetPageTemplatesByCanRenderRequest = new api.content.page.GetPageTemplatesByCanRenderRequest(siteTemplateKey, this.form.getContent().getType());
            var loader:api.util.Loader = new api.content.page.PageTemplateSummaryLoader(request);

            this.pageTemplateComboBox.setLoader(loader);

            this.pageTemplateToSelect = selectedPageTemplate;

            deferred.resolve(null);

            return deferred.promise;
        }

        setPageTemplateToSelect(value: api.content.page.PageTemplateKey) {
            this.pageTemplateToSelect = value;
        }

        private notifyPageTemplateChanged(changedTo: api.content.page.PageTemplateSummary) {
            this.pageTemplateChangedListeners.forEach((listener) => {
                listener(changedTo);
            });
        }

        addPageTemplateChangedListener(listener: {(changedTo: api.content.page.PageTemplateSummary): void;}) {
            this.pageTemplateChangedListeners.push(listener);
        }

        removePageTemplateChangedListener(listener: {(changedTo: api.content.page.PageTemplateSummary): void;}) {
            this.pageTemplateChangedListeners = this.pageTemplateChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        public getPageTemplateKey(): api.content.page.PageTemplateKey {

            var values = this.pageTemplateComboBox.getValues();

            return values[0] ? values[0].getKey() : null;
        }
    }
}
