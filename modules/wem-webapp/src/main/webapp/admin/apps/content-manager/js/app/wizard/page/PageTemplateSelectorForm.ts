module app.wizard.page {

    export class PageTemplateSelectorForm extends api.ui.form.Form {

        private pageTemplateComboBox: api.content.page.PageTemplateComboBox;

        private pageTemplateChangedListeners: {(changedTo: api.content.page.PageTemplateSummary): void;}[] = [];

        private pageTemplateToSelect: api.content.page.PageTemplateKey;

        constructor() {
            super("page-template-selector-form");

            this.pageTemplateComboBox = new api.content.page.PageTemplateComboBox();

            var fieldSet = new api.ui.form.Fieldset("Page Template");
            fieldSet.add(new api.ui.form.FormItem("Selected", this.pageTemplateComboBox));
            this.add(fieldSet);


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
                                this.pageTemplateComboBox.setTemplate(template);
                            }
                        }
                    });
                }
            );
        }

        layoutExisting(siteTemplateKey: api.content.site.template.SiteTemplateKey,
                       selectedPageTemplate: api.content.page.PageTemplateKey): Q.Promise<void> {
            var deferred = Q.defer<void>();

            this.pageTemplateComboBox.setSiteTemplateKey(siteTemplateKey);

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

            var selectedOptions = this.pageTemplateComboBox.getSelectedData();
            if (selectedOptions.length == 0) {
                return null;
            }

            return selectedOptions[0].displayValue.getKey();
        }
    }
}
