module api.content.page {
    export class PageTemplateComboBox extends api.ui.combobox.RichComboBox<PageTemplateSummary> {


        constructor()
        {
            super(new api.ui.combobox.RichComboBoxBuilder<PageTemplateSummary>().setSelectedOptionsView(new PageTemplateSelectedOptionsView()).setIdentifierMethod("getKey"));
        }

        setSiteTemplateKey(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            this.setLoader(new PageTemplateSummaryLoader(siteTemplateKey))
        }

        setTemplate(pageTemplate:api.content.page.PageTemplateSummary) {
            var option: api.ui.combobox.Option<api.content.page.PageTemplateSummary> = {
                value: pageTemplate.getKey().toString(),
                displayValue: pageTemplate
            };
            this.comboBox.selectOption(option);
        }


        optionFormatter(row: number, cell: number, pageTemplateSummary: api.content.page.PageTemplateSummary, columnDef: any,
                                dataContext: api.ui.combobox.Option<api.content.page.PageTemplateSummary>): string {

            var summaryEl = new api.dom.DivEl();
            summaryEl.setClass("page-template-summary");

            var displayName = new api.dom.DivEl();
            displayName.setClass("display-name");
            displayName.getEl().setAttribute("title", pageTemplateSummary.getDisplayName());
            displayName.getEl().setInnerHtml(pageTemplateSummary.getDisplayName());

            var path = new api.dom.DivEl();
            path.setClass("name");
            path.getEl().setAttribute("title", pageTemplateSummary.getDescriptorKey().toString());
            path.getEl().setInnerHtml(pageTemplateSummary.getDescriptorKey().toString());


            summaryEl.appendChild(displayName);
            summaryEl.appendChild(path);

            return summaryEl.toString();
        }

        createConfig():api.ui.combobox.ComboBoxConfig<PageTemplateSummary> {
            var config:api.ui.combobox.ComboBoxConfig<PageTemplateSummary> = super.createConfig();
            config.maximumOccurrences = 1;


            return config;
        }

        getSelectedData():api.ui.combobox.Option<PageTemplateSummary>[] {
            return this.comboBox.getSelectedData();
        }

    }

    export class PageTemplateSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.page.PageTemplateSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.content.page.PageTemplateSummary>, index:number):api.ui.combobox.SelectedOption<api.content.page.PageTemplateSummary> {
            return new api.ui.combobox.SelectedOption<api.content.page.PageTemplateSummary>(new PageTemplateSelectedOptionView(option), option, index);
        }
    }

    export class PageTemplateSelectedOptionView extends api.ui.combobox.SelectedOptionView<api.content.page.PageTemplateSummary> {

        private pageTemplate: api.content.page.PageTemplateSummary;

        constructor(option: api.ui.combobox.Option<api.content.page.PageTemplateSummary>) {
            this.pageTemplate = option.displayValue;
            super(option);
            this.addClass("page-template-selected-option-view");
        }

        layout() {
            var removeButtonEl = new api.dom.AEl("remove");
            var optionValueEl = new api.dom.DivEl('option-value');

            this.appendChild(removeButtonEl);
            this.appendChild(optionValueEl);

            var pageTemplateSummaryEl = new api.dom.DivEl();
            pageTemplateSummaryEl.setClass("page-template-summary");

            var displayNameEl = new api.dom.DivEl();
            displayNameEl.setClass("display-name");
            displayNameEl.getEl().setAttribute("title", this.pageTemplate.getDisplayName());
            displayNameEl.getEl().setInnerHtml(this.pageTemplate.getDisplayName());

            var path = new api.dom.DivEl();
            path.addClass("name");
            path.getEl().setAttribute("title", this.pageTemplate.getDescriptorKey().toString());
            path.getEl().setInnerHtml(this.pageTemplate.getDescriptorKey().toString());

            pageTemplateSummaryEl.appendChild(displayNameEl);
            pageTemplateSummaryEl.appendChild(path);

            optionValueEl.appendChild(pageTemplateSummaryEl);

            removeButtonEl.getEl().addEventListener('click', (event: Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });
        }

    }
}