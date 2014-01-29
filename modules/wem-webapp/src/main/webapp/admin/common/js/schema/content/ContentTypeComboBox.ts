module api.schema.content {
    export class ContentTypeComboBox extends api.ui.combobox.RichComboBox<ContentTypeSummary> {

        private multipleSelection:boolean;

        constructor(multiple:boolean = true)
        {
            super(new api.ui.combobox.RichComboBoxBuilder<ContentTypeSummary>().setLoader(new ContentTypeSummaryLoader()).setSelectedOptionsView(new RootContentTypeSelectedOptionsView()));
            this.multipleSelection = multiple;
        }


        optionFormatter(row:number, cell:number, content:ContentTypeSummary, columnDef:any, dataContext:api.ui.combobox.Option<ContentTypeSummary>):string
        {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();

            namesAndIconView
                .setIconUrl(content.getIconUrl())
                .setMainName(content.getDisplayName())
                .setSubName(content.getKey());

            return namesAndIconView.toString();
        }

        createConfig():api.ui.combobox.ComboBoxConfig<ContentTypeSummary> {
            var config:api.ui.combobox.ComboBoxConfig<ContentTypeSummary> = super.createConfig();
            config.maximumOccurrences = this.multipleSelection ? 0 : 1;

            return config;
        }
    }

    export class RootContentTypeSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<ContentTypeSummary> {

        createSelectedOption(option:api.ui.combobox.Option<ContentTypeSummary>, index:number):ui.combobox.SelectedOption<ContentTypeSummary> {
            var optionView = new RootContentSelectedOptionView( option );
            return new api.ui.combobox.SelectedOption<ContentTypeSummary>( optionView, option, index);
        }
    }

    export class RootContentSelectedOptionView extends ui.combobox.RichSelectedOptionView<ContentTypeSummary> {


        constructor(option:ui.combobox.Option<ContentTypeSummary>) {
            super(option);
        }

        resolveIconUrl(content:ContentTypeSummary):string
        {
            return content.getIconUrl();
        }

        resolveTitle(content:ContentTypeSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:ContentTypeSummary):string
        {
            return content.getKey();
        }

    }
}