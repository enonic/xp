module app.wizard.page {

    export class PageTemplateSelectorForm extends api.ui.form.Form {

        private pageTemplateComboBox: api.ui.combobox.ComboBox<api.content.page.PageTemplateSummary>;

        private pageTemplateChangedListeners:{(changedTo:api.content.page.PageTemplateSummary): void;}[] = [];

        constructor() {
            super("PageTemplateSelectorForm");
            this.addClass("page-template-selector-form");

            var selectedOptionsView = new PageTemplateSelectedOptionsView();

            var pageTemplateComboBoxConfig = <api.ui.combobox.ComboBoxConfig<api.content.page.PageTemplateSummary>> {
                maximumOccurrences: 1,
                selectedOptionsView: selectedOptionsView,
                hideComboBoxWhenMaxReached: true,
                optionFormatter: this.optionFormatter
            };

            this.pageTemplateComboBox =
            new api.ui.combobox.ComboBox<api.content.page.PageTemplateSummary>("template", pageTemplateComboBoxConfig);

            var compositeFormInputEl = new api.ui.form.CompositeFormInputEl(this.pageTemplateComboBox, selectedOptionsView);

            var fieldSet = new api.ui.form.Fieldset(this, "Page Template");
            fieldSet.add(new api.ui.form.FormItem("Selected", compositeFormInputEl));
            this.fieldset(fieldSet);

            this.pageTemplateComboBox.addListener( <api.ui.combobox.ComboBoxListener<api.content.page.PageTemplateSummary>>{
                onInputValueChanged: () => null,
                onOptionSelected: (option:api.ui.combobox.Option<api.content.page.PageTemplateSummary>) => {

                    this.notifyPageTemplateChanged(option.displayValue);
                }
            });
            this.pageTemplateComboBox.addSelectedOptionRemovedListener(() => {
                this.notifyPageTemplateChanged(null);
            });
        }

        layoutExisting(pageTemplates: api.content.page.PageTemplateSummary[], selectedPageTemplate: api.content.page.PageTemplate) {

            var optionToSelect: api.ui.combobox.Option<api.content.page.PageTemplateSummary> = null;
            pageTemplates.forEach((pageTemplate: api.content.page.PageTemplateSummary) => {

                var option: api.ui.combobox.Option<api.content.page.PageTemplateSummary> = {
                    value: pageTemplate.getKey().toString(),
                    displayValue: pageTemplate
                };
                if (pageTemplate.getKey().toString() == selectedPageTemplate.getKey().toString()) {
                    optionToSelect = option;
                }
                this.pageTemplateComboBox.addOption(option);


            });
            this.pageTemplateComboBox.selectOption(optionToSelect);
        }

        private optionFormatter(row: number, cell: number, pageTemplateSummary: api.content.page.PageTemplateSummary, columnDef: any,
                                dataContext: api.ui.combobox.Option<api.content.page.PageTemplateSummary>): string {

            var summaryEl = new api.dom.DivEl();
            summaryEl.setClass("page-template-summary");

            var displayName = new api.dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", pageTemplateSummary.getDisplayName());
            displayName.getEl().setInnerHtml(pageTemplateSummary.getDisplayName());

            var path = new api.dom.DivEl();
            path.setClass("name");
            path.getEl().setAttribute("title", pageTemplateSummary.getName().toString());
            path.getEl().setInnerHtml(pageTemplateSummary.getName().toString());

            summaryEl.appendChild(displayName);
            summaryEl.appendChild(path);

            return summaryEl.toString();
        }

        private notifyPageTemplateChanged(changedTo:api.content.page.PageTemplateSummary) {
            this.pageTemplateChangedListeners.forEach( (listener) => {
                listener(changedTo);
            });
        }

        addPageTemplateChangedListener(listener:{(changedTo:api.content.page.PageTemplateSummary): void;}) {
            this.pageTemplateChangedListeners.push(listener);
        }

        removePageTemplateChangedListener(listener:{(changedTo:api.content.page.PageTemplateSummary): void;}) {
            this.pageTemplateChangedListeners = this.pageTemplateChangedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}
