module app.wizard.page {

    export class PageTemplateSelector extends api.dom.DivEl {

        private pageTemplateComboBox: api.content.page.TemplateComboBox;

        private pageTemplateChangedListeners: {(changedTo: api.content.page.PageTemplateSummary): void;}[] = [];

        private pageTemplateToSelect: api.content.page.TemplateKey;

        constructor(form:PageWizardStepForm) {
            super("page-template-selector-form");

            this.pageTemplateComboBox = new api.content.page.TemplateComboBox();

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
                       selectedPageTemplate: api.content.page.TemplateKey): Q.Promise<void> {
            var deferred = Q.defer<void>();

            this.pageTemplateComboBox.setLoader(new api.content.page.PageTemplateSummaryLoader(siteTemplateKey));


            this.pageTemplateToSelect = selectedPageTemplate;

            deferred.resolve(null);

            return deferred.promise;
        }

        setPageTemplateToSelect(value: api.content.page.TemplateKey) {
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

        public getPageTemplateKey(): api.content.page.TemplateKey {

            var values = this.pageTemplateComboBox.getValues();

            return values[0] ? values[0].getKey() : null;
        }
    }
}
