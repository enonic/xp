module api.content.page {

    export class TemplateComboBox extends api.ui.combobox.RichComboBox<TemplateSummary> {


        constructor()
        {
            super(new api.ui.combobox.RichComboBoxBuilder<TemplateSummary>().
                setSelectedOptionsView(new TemplateSelectedOptionsView()).setIdentifierMethod("getKey"));
        }

        setTemplate(pageTemplate:TemplateSummary) {
            var option: api.ui.combobox.Option<TemplateSummary> = {
                value: pageTemplate.getKey().toString(),
                displayValue: pageTemplate
            };
            this.comboBox.selectOption(option);
        }

        optionFormatter(row: number, cell: number, pageTemplateSummary: TemplateSummary, columnDef: any,
                        dataContext: api.ui.combobox.Option<TemplateSummary>): string {
            var namesView = new api.app.NamesView()
                .setMainName( pageTemplateSummary.getDisplayName() )
                .setSubName(pageTemplateSummary.getDescriptorKey().toString());

            return namesView.toString();
        }

        createConfig():api.ui.combobox.ComboBoxConfig<TemplateSummary> {
            var config:api.ui.combobox.ComboBoxConfig<TemplateSummary> = super.createConfig();
            config.maximumOccurrences = 1;

            return config;
        }

        getSelectedData():api.ui.combobox.Option<TemplateSummary>[] {
            return this.comboBox.getSelectedData();
        }

    }

    export class TemplateSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<TemplateSummary> {

        createSelectedOption(option:api.ui.combobox.Option<TemplateSummary>, index:number):api.ui.combobox.SelectedOption<TemplateSummary> {
            return new api.ui.combobox.SelectedOption<TemplateSummary>(new TemplateSelectedOptionView(option), option, index);
        }
    }

    export class TemplateSelectedOptionView extends api.ui.combobox.SelectedOptionView<TemplateSummary> {

        private pageTemplate: TemplateSummary;

        constructor(option: api.ui.combobox.Option<TemplateSummary>) {
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