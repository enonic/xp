module api.schema.content {

    import BaseLoader = api.util.loader.BaseLoader;
    import ContentTypeSummaryListJson = api.schema.content.ContentTypeSummaryListJson;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import RichComboBox = api.ui.selector.combobox.RichComboBox;
    import RichComboBoxBuilder = api.ui.selector.combobox.RichComboBoxBuilder;
    import BaseSelectedOptionsView = api.ui.selector.combobox.BaseSelectedOptionsView;
    import RichSelectedOptionView = api.ui.selector.combobox.RichSelectedOptionView;
    import RichSelectedOptionViewBuilder = api.ui.selector.combobox.RichSelectedOptionViewBuilder;

    export class ContentTypeComboBox extends RichComboBox<ContentTypeSummary> {

        constructor(maximumOccurrences: number = 0,
                    loader: BaseLoader<ContentTypeSummaryListJson, ContentTypeSummary> = new ContentTypeSummaryLoader()) {
            super(new RichComboBoxBuilder<ContentTypeSummary>()
                .setLoader(loader)
                .setSelectedOptionsView(new ContentTypeSelectedOptionsView())
                .setOptionDisplayValueViewer(new ContentTypeSummaryViewer())
                .setMaximumOccurrences(maximumOccurrences));
        }

    }

    export class ContentTypeSelectedOptionsView extends BaseSelectedOptionsView<ContentTypeSummary> {

        createSelectedOption(option: api.ui.selector.Option<ContentTypeSummary>): SelectedOption<ContentTypeSummary> {

            let optionView = new ContentTypeSelectedOptionView(option);
            return new SelectedOption<ContentTypeSummary>(optionView, this.count());
        }
    }

    export class ContentTypeSelectedOptionView extends RichSelectedOptionView<ContentTypeSummary> {

        constructor(option: api.ui.selector.Option<ContentTypeSummary>) {
            super(new RichSelectedOptionViewBuilder<ContentTypeSummary>(option));
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