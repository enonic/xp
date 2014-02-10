module api.content {
    export class ContentComboBox extends api.ui.combobox.RichComboBox<api.content.ContentSummary> {

        constructor(contentComboBoxBuilder:ContentComboBoxBuilder)
        {
            var builder:api.ui.combobox.RichComboBoxBuilder<api.content.ContentSummary> = new api.ui.combobox.RichComboBoxBuilder<api.content.ContentSummary>();

            builder
                .setComboBoxName(contentComboBoxBuilder.name ? contentComboBoxBuilder.name : 'contentSelector')
                .setLoader(contentComboBoxBuilder.loader ? contentComboBoxBuilder.loader : new api.form.inputtype.content.ContentSummaryLoader())
                .setSelectedOptionsView(new ContentSelectedOptionsView())
                .setMaximumOccurrences(contentComboBoxBuilder.maximumOccurrences);

            super(builder);
        }


        optionFormatter(row:number, cell:number, content:api.content.ContentSummary, columnDef:any, dataContext:api.ui.combobox.Option<api.content.ContentSummary>):string
        {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();

            namesAndIconView
                .setIconUrl(content.getIconUrl())
                .setMainName(content.getDisplayName())
                .setSubName(content.getPath().toString());

            return namesAndIconView.toString();
        }
    }

    export class ContentSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<api.content.ContentSummary> {

        createSelectedOption(option:api.ui.combobox.Option<api.content.ContentSummary>, index:number):api.ui.combobox.SelectedOption<api.content.ContentSummary> {
            var optionView = new ContentSelectedOptionView( option );
            return new api.ui.combobox.SelectedOption<api.content.ContentSummary>( optionView, option, index);
        }
    }

    export class ContentSelectedOptionView extends api.ui.combobox.RichSelectedOptionView<api.content.ContentSummary> {


        constructor(option:api.ui.combobox.Option<api.content.ContentSummary>) {
            super(option);
        }

        resolveIconUrl(content:api.content.ContentSummary):string
        {
            return content.getIconUrl();
        }

        resolveTitle(content:api.content.ContentSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:api.content.ContentSummary):string
        {
            return content.getPath().toString();
        }

    }

    export class ContentComboBoxBuilder {

        name:string;

        maximumOccurrences:number = 0;

        loader:api.util.Loader;

        setName(value:string):ContentComboBoxBuilder {
            this.name = value;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences:number):ContentComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader:api.util.Loader):ContentComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        build():ContentComboBox {
            return new ContentComboBox(this);
        }

    }
}