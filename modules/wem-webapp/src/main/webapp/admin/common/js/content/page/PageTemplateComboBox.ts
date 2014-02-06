module api.content.page {

    export class PageTemplateComboBox extends api.ui.combobox.RichComboBox<PageTemplateSummary> {

        constructor()
        {
            super(new api.ui.combobox.RichComboBoxBuilder<PageTemplateSummary>().
                setSelectedOptionsView(new PageTemplateSelectedOptionsView()).setIdentifierMethod("getKey"));
        }

        setTemplate(pageTemplate:PageTemplateSummary) {
            var option: api.ui.combobox.Option<PageTemplateSummary> = {
                value: pageTemplate.getKey().toString(),
                displayValue: pageTemplate
            };
            this.comboBox.selectOption(option);
        }

        optionFormatter(row: number, cell: number, pageTemplateSummary: PageTemplateSummary, columnDef: any,
                        dataContext: api.ui.combobox.Option<PageTemplateSummary>): string {

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

    export class PageTemplateSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<PageTemplateSummary> {

        createSelectedOption(option:api.ui.combobox.Option<PageTemplateSummary>, index:number):api.ui.combobox.SelectedOption<PageTemplateSummary> {
            return new api.ui.combobox.SelectedOption<PageTemplateSummary>(new PageTemplateSelectedOptionView(option), option, index);
        }
    }

    export class PageTemplateSelectedOptionView extends api.ui.combobox.SelectedOptionView<PageTemplateSummary> {

        private pageTemplate: PageTemplateSummary;

        constructor(option: api.ui.combobox.Option<PageTemplateSummary>) {
            this.pageTemplate = option.displayValue;
            super(option);
            this.addClass("page-template-selected-option-view");
        }

        layout() {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();
            namesAndIconView.setIconUrl( api.util.getAdminUri('common/images/icons/icoMoon/32x32/earth.png') )
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
            this.appendChild(namesAndIconView);
        }

    }
}