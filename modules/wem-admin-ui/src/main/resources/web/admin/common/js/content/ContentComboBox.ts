module api.content {

    export class ContentComboBox extends api.ui.selector.combobox.RichComboBox<api.content.ContentSummary> {

        constructor(builder: ContentComboBoxBuilder) {

            var loader = builder.loader ? builder.loader : new ContentSummaryLoader();
            loader.setAllowedContentTypes(builder.allowedContentTypes);

            var richComboBoxBuilder: api.ui.selector.combobox.RichComboBoxBuilder<api.content.ContentSummary> = new api.ui.selector.combobox.RichComboBoxBuilder<api.content.ContentSummary>();
            richComboBoxBuilder
                .setComboBoxName(builder.name ? builder.name : 'contentSelector')
                .setLoader(loader)
                .setSelectedOptionsView(new ContentSelectedOptionsView())
                .setMaximumOccurrences(builder.maximumOccurrences)
                .setOptionDisplayValueViewer(new api.content.ContentSummaryViewer())
                .setDelayedInputValueChangedHandling(500);

            super(richComboBoxBuilder);
        }
    }

    export class ContentSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<api.content.ContentSummary> {

        createSelectedOption(option: api.ui.selector.Option<api.content.ContentSummary>,
                             index: number): api.ui.selector.combobox.SelectedOption<api.content.ContentSummary> {
            var optionView = new ContentSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.content.ContentSummary>(optionView, option, index);
        }
    }

    export class ContentSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.content.ContentSummary> {


        constructor(option: api.ui.selector.Option<api.content.ContentSummary>) {
            super(option);
        }

        resolveIconUrl(content: api.content.ContentSummary): string {
            return content.getIconUrl();
        }

        resolveTitle(content: api.content.ContentSummary): string {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content: api.content.ContentSummary): string {
            return content.getPath().toString();
        }

    }

    export class ContentComboBoxBuilder {

        name: string;

        maximumOccurrences: number = 0;

        loader: ContentSummaryLoader;

        allowedContentTypes: string[];

        setName(value: string): ContentComboBoxBuilder {
            this.name = value;
            return this;
        }

        setMaximumOccurrences(maximumOccurrences: number): ContentComboBoxBuilder {
            this.maximumOccurrences = maximumOccurrences;
            return this;
        }

        setLoader(loader: ContentSummaryLoader): ContentComboBoxBuilder {
            this.loader = loader;
            return this;
        }

        setAllowedContentTypes(allowedTypes: string[]): ContentComboBoxBuilder {
            this.allowedContentTypes = allowedTypes;
            return this;
        }

        build(): ContentComboBox {
            return new ContentComboBox(this);
        }

    }
}