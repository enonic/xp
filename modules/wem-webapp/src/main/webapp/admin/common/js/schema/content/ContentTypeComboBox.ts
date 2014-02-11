module api.schema.content {

    export class ContentTypeComboBox extends api.ui.combobox.RichComboBox<ContentTypeSummary> {

        constructor(maximumOccurrences: number = 0) {
            super(new api.ui.combobox.RichComboBoxBuilder<ContentTypeSummary>()
                .setLoader(new ContentTypeSummaryLoader())
                .setSelectedOptionsView(new ContentTypeSelectedOptionsView())
                .setMaximumOccurrences(maximumOccurrences));
        }

        optionFormatter(row: number, cell: number, content: ContentTypeSummary, columnDef: any,
                        dataContext: api.ui.combobox.Option<ContentTypeSummary>): string {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();

            namesAndIconView
                .setIconUrl(content.getIconUrl())
                .setMainName(content.getDisplayName())
                .setSubName(content.getKey());

            return namesAndIconView.toString();
        }

    }

    export class ContentTypeSelectedOptionsView extends api.ui.combobox.SelectedOptionsView<ContentTypeSummary> {

        createSelectedOption(option: api.ui.combobox.Option<ContentTypeSummary>,
                             index: number): ui.combobox.SelectedOption<ContentTypeSummary> {

            var optionView = new ContentTypeSelectedOptionView(option);
            return new api.ui.combobox.SelectedOption<ContentTypeSummary>(optionView, option, index);
        }
    }

    export class ContentTypeSelectedOptionView extends ui.combobox.RichSelectedOptionView<ContentTypeSummary> {

        constructor(option: ui.combobox.Option<ContentTypeSummary>) {
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