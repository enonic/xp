module app.wizard.page {

    export class PageTemplateSelectorForm extends api.ui.form.Form {

        private pageTemplateComboBox: api.content.page.PageTemplateComboBox;

        private pageTemplateChangedListeners: {(changedTo: api.content.page.PageTemplateSummary): void;}[] = [];

        constructor() {
            super("page-template-selector-form");

            this.pageTemplateComboBox = new api.content.page.PageTemplateComboBox();

            var fieldSet = new api.ui.form.Fieldset("Page Template");
            fieldSet.add(new api.ui.form.FormItem("Selected", this.pageTemplateComboBox));
            this.add(fieldSet);


            this.pageTemplateComboBox.addOptionSelectedListener((option: api.ui.combobox.Option<api.content.page.PageTemplateSummary>) => {
                this.notifyPageTemplateChanged(option.displayValue);
            });


            this.pageTemplateComboBox.addSelectedOptionRemovedListener(() => {
                this.notifyPageTemplateChanged(null);
            });
        }

        layoutExisting(siteTemplateKey: api.content.site.template.SiteTemplateKey,
                       selectedPageTemplate: api.content.page.PageTemplateKey): Q.Promise<void> {
            var deferred = Q.defer<void>();

            this.pageTemplateComboBox.setSiteTemplateKey(siteTemplateKey);
            if (selectedPageTemplate != null) {

                this.pageTemplateComboBox.addLoadedListener((pageTemplates: api.content.page.PageTemplateSummary[]) => {
                        pageTemplates.forEach((template: api.content.page.PageTemplateSummary) => {
                            if (template.getKey().toString() == selectedPageTemplate.toString()) {
                                this.pageTemplateComboBox.setTemplate(template);
                            }
                        });
                        deferred.resolve(null);
                    }
                );
            }
            else {
                deferred.resolve(null);
            }

            return deferred.promise;
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
