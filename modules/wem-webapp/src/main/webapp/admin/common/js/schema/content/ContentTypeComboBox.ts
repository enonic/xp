module api.schema.content {

    export class ContentTypeComboBox extends api.ui.selector.combobox.RichComboBox<ContentTypeSummary> {

        constructor(maximumOccurrences: number = 0) {
            var loader = new ContentTypeSummaryLoader();
            super(new api.ui.selector.combobox.RichComboBoxBuilder<ContentTypeSummary>()
                .setLoader(loader)
                .setSelectedOptionsView(new ContentTypeSelectedOptionsView())
                .setMaximumOccurrences(maximumOccurrences));
            loader.load();
        }

        optionFormatter(row: number, cell: number, content: ContentTypeSummary, columnDef: any,
                        dataContext: api.ui.selector.Option<ContentTypeSummary>): string {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();

            namesAndIconView
                .setIconUrl(content.getIconUrl())
                .setMainName(content.getDisplayName())
                .setSubName(content.getKey());

            return namesAndIconView.toString();
        }

    }

    export class ContentTypeSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<ContentTypeSummary> {

        createSelectedOption(option: api.ui.selector.Option<ContentTypeSummary>,
                             index: number): api.ui.selector.combobox.SelectedOption<ContentTypeSummary> {

            var optionView = new ContentTypeSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<ContentTypeSummary>(optionView, option, index);
        }
    }

    export class ContentTypeSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<ContentTypeSummary> {

        constructor(option: api.ui.selector.Option<ContentTypeSummary>) {
            super(option);
        }

        resolveIconUrl(content: ContentTypeSummary): string {
            return content.getIconUrl();
        }

        resolveTitle(content: ContentTypeSummary): string {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content: ContentTypeSummary): string {
            return content.getKey();
        }

    }
}