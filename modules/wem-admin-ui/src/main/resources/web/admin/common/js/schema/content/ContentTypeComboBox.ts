module api.schema.content {

    export class ContentTypeComboBox extends api.ui.selector.combobox.RichComboBox<ContentTypeSummary> {

        constructor(maximumOccurrences: number = 0) {
            var loader = new ContentTypeSummaryLoader();
            super(new api.ui.selector.combobox.RichComboBoxBuilder<ContentTypeSummary>()
                .setLoader(loader)
                .setSelectedOptionsView(new ContentTypeSelectedOptionsView())
                .setOptionDisplayValueViewer(new ContentTypeSummaryViewer())
                .setMaximumOccurrences(maximumOccurrences));
            loader.load();
        }

    }

    export class ContentTypeSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<ContentTypeSummary> {

        createSelectedOption(option: api.ui.selector.Option<ContentTypeSummary>): api.ui.selector.combobox.SelectedOption<ContentTypeSummary> {

            var optionView = new ContentTypeSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<ContentTypeSummary>(optionView, this.count());
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
            return content.getName();
        }

    }
}