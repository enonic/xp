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
            var namesView = new api.app.NamesView()
                .setMainName( pageTemplateSummary.getDisplayName() )
                .setSubName(pageTemplateSummary.getDescriptorKey().toString());

            return namesView.toString();
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
            var namesView = new api.app.NamesView()
                .setMainName( this.pageTemplate.getDisplayName() )
                .setSubName( this.pageTemplate.getDescriptorKey().toString() );

            var removeButtonEl = new api.dom.AEl("remove");
            removeButtonEl.getEl().addEventListener('click', (event: Event) => {
                this.notifySelectedOptionToBeRemoved();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(namesView);
        }

    }
}